package com.activiti.android.app.activity


import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.activiti.android.app.R
import com.activiti.android.app.common.getViewModel
import com.activiti.android.app.fragments.account.OptionalFragment
import com.activiti.android.app.fragments.account.aims.AIMSBasicAuthFragment
import com.activiti.android.app.fragments.account.aims.AIMSSSOAuthFragment
import com.activiti.android.app.fragments.account.aims.AIMSWelcomeFragment
import com.activiti.android.platform.EventBusManager
import com.activiti.android.platform.account.AccountsPreferences
import com.activiti.android.platform.account.ActivitiAccount
import com.activiti.android.platform.account.ActivitiAccountManager
import com.activiti.android.platform.integration.analytics.AnalyticsHelper
import com.activiti.android.platform.integration.analytics.AnalyticsManager
import com.activiti.android.platform.provider.app.RuntimeAppInstanceManager
import com.activiti.android.platform.provider.group.GroupInstanceManager
import com.activiti.android.platform.provider.integration.IntegrationManager
import com.activiti.android.platform.provider.integration.IntegrationSyncEvent
import com.activiti.android.platform.provider.processdefinition.ProcessDefinitionModelManager
import com.activiti.android.sdk.ActivitiSession
import com.activiti.android.sdk.model.runtime.AppVersion
import com.activiti.android.ui.fragments.FragmentDisplayer
import com.activiti.client.api.model.idm.UserRepresentation
import com.activiti.client.api.model.runtime.AppVersionRepresentation
import com.alfresco.auth.ui.AlfrescoAuthActivity
import com.alfresco.auth.ui.PkceAuthUiModel
import com.alfresco.auth.ui.observe
import com.alfresco.client.AuthorizationCredentials
import com.alfresco.client.SSOAuthorizationCredentials
import com.squareup.otto.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AIMSWelcomeActivity : AlfrescoAuthActivity<AIMSWelcomeViewModel>() {

    override val viewModel: AIMSWelcomeViewModel by lazy {
        getViewModel { AIMSWelcomeViewModel(applicationContext) }
    }

    private lateinit var progressView: RelativeLayout

    private lateinit var toolbar: Toolbar

    private var acc: ActivitiAccount? = null

    private var activitiSession: ActivitiSession? = null

    private var user: UserRepresentation? = null

    private var authCredentials: AuthorizationCredentials? = null

    private var version: AppVersion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContentView(R.layout.aims_activity_welcome)

        setupToolbar()

        progressView = findViewById(R.id.rlProgress)
        ViewCompat.setElevation(progressView, 10f)

        if (supportFragmentManager.findFragmentByTag(AIMSWelcomeFragment.TAG) == null) {
            FragmentDisplayer.with(this).load(AIMSWelcomeFragment.with(this)
                    .addExtra(getIntent().getExtras()).createFragment()).animate(null)
                    .back(false).into(FragmentDisplayer.PANEL_LEFT)
        }

        observe(viewModel.authType, ::onAuthType)
        observe(viewModel.authResult, ::onPkceAuthUiModel)

        observe(viewModel.hasNavigation, ::onNavigation)

        observe(viewModel.startSSO, ::aimsLogin)
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.aims_save_settings -> return false

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onLoading(isLoading: Boolean) {
        progressView.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun onNavigation(hasNavigation: Boolean) {
        if (hasNavigation) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbar.setNavigationOnClickListener { onBackPressed() }

        } else {
            toolbar.setNavigationIcon(null)
            toolbar.setNavigationOnClickListener(null)
        }
    }

    fun onAuthType(authType: AuthenticationType) {
        when (authType) {
            is AuthenticationType.SSO -> {
                AIMSSSOAuthFragment.with(this)
                        .identityServiceUrl(authType.endpoint)
                        .processRepositoryLocation("alfresco-cs-repository.mobile.dev.alfresco.me").display()
            }

            is AuthenticationType.Basic -> {
                AIMSBasicAuthFragment.with(this).withHostname(authType.hostname).withCloud(authType.withCloud).display()
            }

            is AuthenticationType.Unknown -> {
                Toast.makeText(this, "Auth type: unknown", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onPkceAuthUiModel(authResult: PkceAuthUiModel) {
        when (authResult.success) {
            true -> {

                authCredentials = SSOAuthorizationCredentials(authResult.userEmail, authResult.accessToken)

                connect(authCredentials!!)
            }

            false -> {
                Toast.makeText(this, "Login Failed: " + authResult.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onStart() {
        super.onStart()
        EventBusManager.getInstance().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusManager.getInstance().unregister(this)
    }

    @Subscribe
    fun onIntegrationSyncEvent(event: IntegrationSyncEvent) {
        if (acc == null) {
            return
        }

        sync()
        OptionalFragment.with(this).acocuntId(acc!!.id).back(false).display()
    }

    private fun sync() { // Sync all required Informations
        RuntimeAppInstanceManager.sync(this)
        ProcessDefinitionModelManager.sync(this)
        GroupInstanceManager.sync(this)
    }

    private fun connect(authCredentials: AuthorizationCredentials) {
        val endpoint = "http://alfresco-cs-repository.mobile.dev.alfresco.me/activiti-app/"

        try {
            activitiSession = ActivitiSession.Builder().connect(endpoint, authCredentials).build()
            activitiSession!!.serviceRegistry.profileService.getProfile(object : Callback<UserRepresentation> {

                override fun onFailure(call: Call<UserRepresentation>?, t: Throwable?) {
                    Toast.makeText(this@AIMSWelcomeActivity, "Get Profile failed!", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<UserRepresentation>?, response: Response<UserRepresentation>?) {
                    if (response!!.isSuccessful) {
                        user = response.body()
                        retrieveServerInfo()

                    } else if (response.code() == 401) {
                        Toast.makeText(this@AIMSWelcomeActivity, "Get Profile failed! 401", Toast.LENGTH_LONG).show()
                    }
                }
            })

        } catch (ex: Exception) {
            Toast.makeText(this@AIMSWelcomeActivity, "Get Profile failed!", Toast.LENGTH_LONG).show()
        }
    }

    private fun retrieveServerInfo() {
        activitiSession!!.serviceRegistry.infoService.getInfo(object : Callback<AppVersionRepresentation> {

            override fun onFailure(call: Call<AppVersionRepresentation>?, t: Throwable?) {
                version = null

                createAccount()
            }

            override fun onResponse(call: Call<AppVersionRepresentation>?, response: Response<AppVersionRepresentation>?) {
                if (response!!.isSuccessful) {
                    version = AppVersion(response.body())

                    createAccount()

                } else {
                    version = null

                    createAccount()
                }
            }
        })
    }

    private fun createAccount() {
        val endpoint = "http://alfresco-cs-repository.mobile.dev.alfresco.me/activiti-app/"

        acc = null

        if (user == null) {
            return
        }

        val userId = user!!.id.toString()
        val fullName = user!!.fullname

        val tenantId = if (user!!.tenantId != null) user!!.tenantId.toString() else null

        // If no version info it means Activiti pre 1.2
        if (version == null) {

            acc = ActivitiAccountManager.getInstance(this).create(authCredentials, endpoint,
                    "Activiti Server", "bpmSuite", "Alfresco Activiti Enterprise BPM Suite", "1.1.0",
                    userId, fullName, tenantId)

        } else {
            acc = ActivitiAccountManager.getInstance(this).create(authCredentials, endpoint,
                    "Activiti Server", version!!.type, version!!.edition, version!!.fullVersion,
                    userId, fullName, tenantId);
        }

        // Create My Tasks Applications
        RuntimeAppInstanceManager.getInstance(this).createAppInstance(acc!!.id, -1L, "My Tasks", "", "",
                "Access your full task getProcessInstances and work on any tasks assigned to you from any process app",
                "", "", 0, 0, 0);

        // Set as Default
        AccountsPreferences.setDefaultAccount(this, acc!!.id)

        // Start a sync for integration
        IntegrationManager.sync(this)

        // Analytics
        AnalyticsHelper.reportOperationEvent(this, AnalyticsManager.CATEGORY_ACCOUNT,
                AnalyticsManager.ACTION_CREATE, acc!!.serverType, 1, false);
    }
}
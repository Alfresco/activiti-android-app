package com.activiti.android.app.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.activiti.android.app.R
import com.activiti.android.app.common.startActivity
import com.activiti.android.app.fragments.account.aims.AIMSBasicAuthFragment
import com.activiti.android.app.fragments.account.aims.AIMSSSOAuthFragment
import com.activiti.android.app.fragments.account.aims.AIMSWelcomeFragment
import com.activiti.android.ui.fragments.FragmentDisplayer
import com.alfresco.auth.ui.AlfrescoAuthActivity
import com.alfresco.auth.ui.PkceAuthUiModel
import com.alfresco.auth.ui.observe

class AIMSWelcomeActivity : AlfrescoAuthActivity<AIMSWelcomeViewModel>() {

    override val viewModel: AIMSWelcomeViewModel by viewModels()

    private lateinit var progressView: RelativeLayout

    private lateinit var toolbar: Toolbar

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
                Toast.makeText(this, "Access: " + authResult.accessToken, Toast.LENGTH_LONG).show()

                startActivity<WelcomeActivity>()
                finish()
            }

            false -> {
                Toast.makeText(this, "Login Failed: " + authResult.error, Toast.LENGTH_LONG).show()
            }
        }
    }
}
package com.activiti.android.app.fragments.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.activiti.android.app.R
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
import com.activiti.client.api.model.idm.UserRepresentation
import com.activiti.client.api.model.runtime.AppVersionRepresentation
import com.alfresco.auth.AlfrescoAuth
import com.squareup.otto.Subscribe
import kotlinx.coroutines.SupervisorJob
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Bogdan Roatis on 11/7/2019.
 */
class LoginUsernamePasswordViewModel(application: Application) : AndroidViewModel(application) {

    private var session: ActivitiSession? = null
    private var version: AppVersion? = null
    private var account: ActivitiAccount? = null
    private var user: UserRepresentation? = null

    private lateinit var username: String
    private lateinit var password: String
    private var endpoint: String? = null

    /**
     * Private mutable implementation that should be used inside the viewmodel
     * as opposed to [onResult] which is the one that is being observed from the outside
     */
    private val _onResult = MutableLiveData<LoginResult>()

    /**
     * Used to send the loading status of the operation.
     * The loading status is true when loading and false otherwise
     */
    val onResult: LiveData<LoginResult> get() = _onResult

    /**
     * Private mutable implementation that should be used inside the viewmodel
     * as opposed to [onLoading] which is the one that is being observed from the outside
     */
    private val _onLoading = MutableLiveData<Boolean>()

    /**
     * Used to send the loading status of the operation.
     * The loading status is true when loading and false otherwise
     */
    val onLoading: LiveData<Boolean> get() = _onLoading

    /**
     * The supervisor job for all the coroutines
     */
    private val job = SupervisorJob()

    init {
        try {
            EventBusManager.getInstance().register(this)
        } catch (exception: Exception) {
            // do nothing
        }
    }

    fun login(username: String, password: String, endpoint: String?, https: Boolean?) {
        this.username = username
        this.password = password
        this.endpoint = AlfrescoAuth.formatEndpoint(endpoint, https ?: false)

        _onLoading.value = true
        try {
            session = ActivitiSession.Builder().connect(this.endpoint, username, password).build()
            session?.serviceRegistry?.profileService?.getProfile(object : Callback<UserRepresentation> {
                override fun onResponse(call: Call<UserRepresentation>, response: Response<UserRepresentation>) {
                    if (response.isSuccessful) {
                        user = response.body()
                        retrieveServerInfo()
                    } else if (response.code() == 401) {
                        _onLoading.value = false
                        _onResult.value = LoginResult(success = false, passwordError = R.string.error_incorrect_password)
                    }
                }

                override fun onFailure(call: Call<UserRepresentation>, error: Throwable) {
                    _onLoading.value = false
                    _onResult.value = LoginResult(success = false, error = error.message)
//                    showConnectionFailure(error)
                }
            })
        } catch (illegalArgumentException: IllegalArgumentException) {
            _onLoading.value = false
            _onResult.value = LoginResult(success = false, error = illegalArgumentException.message)
        }
    }

    private fun retrieveServerInfo() {
        session?.serviceRegistry?.infoService?.getInfo(object : Callback<AppVersionRepresentation> {
            override fun onResponse(call: Call<AppVersionRepresentation>, response: Response<AppVersionRepresentation>) {
                version = if (response.isSuccessful) {
                    // BPM Suite 1.2
                    AppVersion(response.body()!!)
                } else {
                    // BPM Suite 1.1
                    null
                }
                createAccount()
            }

            override fun onFailure(call: Call<AppVersionRepresentation>, error: Throwable) {
                // BPM Suite 1.1
                version = null
                createAccount()
            }
        })
    }

    private fun createAccount() {
        // If no version info it means Activiti pre 1.2
        account = if (version == null) {
            ActivitiAccountManager.getInstance(getApplication()).create(username, password, endpoint,
                    "Activiti Server", "bpmSuite", "Alfresco Activiti Enterprise BPM Suite", "1.1.0",
                    user?.id.toString(), user?.fullname,
                    if (user?.tenantId != null) user?.tenantId.toString() else null)
        } else {
            ActivitiAccountManager.getInstance(getApplication()).create(username, password, endpoint,
                    "Activiti Server", version?.type, version?.edition, version?.fullVersion,
                    user?.id.toString(), user?.fullname,
                    if (user?.tenantId != null) user?.tenantId.toString() else null)
        }

        // Create My Tasks Applications
        RuntimeAppInstanceManager.getInstance(getApplication()).createAppInstance(account?.id, -1L, "My Tasks", "", "",
                "Access your full task getProcessInstances and work on any tasks assigned to you from any process app",
                "", "", 0, 0, 0)

        // Set as Default
        AccountsPreferences.setDefaultAccount(getApplication(), account?.id!!)

        // Start a sync for integration
        IntegrationManager.sync(getApplication())

        // Analytics
        AnalyticsHelper.reportOperationEvent(getApplication(), AnalyticsManager.CATEGORY_ACCOUNT,
                AnalyticsManager.ACTION_CREATE, account?.serverType, 1, false)
    }

    @Subscribe
    fun onIntegrationSyncEvent(event: IntegrationSyncEvent) {
        if (account == null) {
            return
        }

        RuntimeAppInstanceManager.sync(getApplication())
        ProcessDefinitionModelManager.sync(getApplication())
        GroupInstanceManager.sync(getApplication())

        try {
            EventBusManager.getInstance().unregister(this)
        } catch (exception: Exception) {
            // do nothing
        }

        _onResult.value = LoginResult(success = true)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            EventBusManager.getInstance().unregister(this)
        } catch (exception: Exception) {
            // do nothing
        }
    }
}

data class LoginResult(
        val success: Boolean = false,
        val error: String? = null,
        val usernameError: Int? = null,
        val passwordError: Int? = null
)

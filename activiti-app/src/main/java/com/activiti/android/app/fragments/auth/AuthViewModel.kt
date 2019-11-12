package com.activiti.android.app.fragments.auth

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.activiti.android.app.fragments.account.SingleLiveEvent
import com.alfresco.auth.AlfrescoAuth
import com.alfresco.auth.AuthType
import com.alfresco.auth.GlobalAuthConfig
import com.alfresco.auth.pkce.PkceAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Created by Bogdan Roatis on 10/29/2019.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val pkceAuthService = PkceAuthService()

    /**
     * Private mutable implementation that should be used inside the viewmodel
     * as opposed to [isLoading] which is the one that is being observed from the outside
     */
    private val _isLoading = MutableLiveData<Boolean>()

    /**
     * Used to send the loading status of the operation.
     * The loading status is true when loading and false otherwise
     */
    val isLoading: LiveData<Boolean> get() = _isLoading

    /**
     * Private mutable implementation that should be used inside the viewmodel
     * as opposed to [authResult] which is the one that is being observed from the outside
     */
    private val _authResult = MutableLiveData<PkceAuthUiModel>()

    /**
     * Used to send the rates
     */
    val authResult: LiveData<PkceAuthUiModel> get() = _authResult

    private val _onResult = SingleLiveEvent<ConnectResult>()
    val onResult: LiveData<ConnectResult> get() = _onResult

    /**
     * Private mutable implementation that should be used inside the viewmodel
     * as opposed to [authConfig] which is the one that is being observed from the outside
     */
    private val _authConfig = MutableLiveData<GlobalAuthConfig>()

    /**
     * Used to send the rates
     */
    val authConfig: LiveData<GlobalAuthConfig> get() = _authConfig

    private var defaultAuthConfig = GlobalAuthConfig(
            https = false,
            port = "443",
            serviceDocuments = "activiti-app",
            realm = "alfresco",
            clientId = "iosapp",
            redirectUrl = "iosapp://fake.url.here/auth"
    )

    /**
     * The supervisor job for all the coroutines
     */
    private val job = SupervisorJob()

    init {
        _authConfig.value = defaultAuthConfig
    }

    fun changeSettings(realm: String, clientId: String) {
        defaultAuthConfig = defaultAuthConfig.copy(realm = realm, clientId = clientId)
        _authConfig.value = defaultAuthConfig
        pkceAuthService.setGlobalAuthConfig(defaultAuthConfig)
    }

    fun connect(endpoint: String) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.Main + job) {
            when (AlfrescoAuth.getAuthType(endpoint, defaultAuthConfig)) {
                AuthType.SSO -> {
                    _isLoading.value = false
                    _onResult.value = ConnectResult(isIdentityUrl = true)
                }
                AuthType.BASIC -> {
                    _isLoading.value = false
                    _onResult.value = ConnectResult(isBasicUrl = true)
                }
                else -> {
                    _isLoading.value = false
                    _onResult.value = ConnectResult(error = "Unknown type of url")
                }
            }
        }
    }

    fun initiateLogin(issuerUrl: String, activity: Activity, requestCode: Int) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.Main) {
            try {
                pkceAuthService.initiateLogin(issuerUrl, activity, requestCode)
            } catch (exception: Exception) {
                _isLoading.value = false
                _authResult.value = PkceAuthUiModel(false, error = exception.message)
            }
        }
    }

    fun handleResult(intent: Intent) {
        viewModelScope.launch {
            val tokenResult = pkceAuthService.getAuthResponse(intent)

            tokenResult.onSuccess {
                _authResult.value = PkceAuthUiModel(true, it.accessToken)
            }

            tokenResult.onError { _authResult.value = PkceAuthUiModel(false, error = it.message) }
        }
    }

    fun signOut() {
        pkceAuthService.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}

data class ConnectResult(
        val isBasicUrl: Boolean = false,
        val isIdentityUrl: Boolean = false,
        val error: String? = null
)

data class PkceAuthUiModel(
        val success: Boolean,
        val error: String? = null
)

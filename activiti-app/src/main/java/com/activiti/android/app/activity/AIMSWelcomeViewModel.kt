package com.activiti.android.app.activity

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.activiti.android.app.common.SingleLiveEvent
import com.activiti.android.platform.authentication.AuthenticationConstants
import com.alfresco.auth.AuthType
import com.alfresco.auth.GlobalAuthConfig
import com.alfresco.auth.ui.BaseAuthViewModel
import com.alfresco.auth.ui.PkceAuthUiModel

class AIMSWelcomeViewModel : BaseAuthViewModel() {

    override var globalAuthConfig: GlobalAuthConfig = GlobalAuthConfig(
            https = AuthenticationConstants.USE_HTTPS,
            clientId = AuthenticationConstants.CLIENT_ID,
            realm = AuthenticationConstants.REALM,
            redirectUrl = AuthenticationConstants.REDIRECT_URI,
            port = if (AuthenticationConstants.USE_HTTPS)
                AuthenticationConstants.HTTPS_PORT else AuthenticationConstants.HTTP_PORT,
            serviceDocuments = AuthenticationConstants.SERVICE_DOCUMENT
    )

    var editAuthConfig = globalAuthConfig

    private val _hasNavigation = MutableLiveData<Boolean>()

    val hasNavigation: LiveData<Boolean> get() = _hasNavigation

    private val _authType = MutableLiveData<AuthenticationType>()

    val authType: LiveData<AuthenticationType> get() = _authType

    private val _authResult = MutableLiveData<PkceAuthUiModel>()

    val authResult: LiveData<PkceAuthUiModel> get() = _authResult

    private val _startSSO = SingleLiveEvent<String>()

    val startSSO: LiveData<String> get() = _startSSO

    private var identityServiceUrl: String? = null
    private var processRepositoryUrl: String? = null

    fun cloudAuthType() {
        _authType.value = AuthenticationType.Basic(hostname = "activiti.alfresco.com", withCloud = true)
    }

    override fun handleAuthType(endpoint: String, authType: AuthType) {
        when (authType) {
            AuthType.SSO ->  _authType.value = AuthenticationType.SSO(endpoint)

            AuthType.BASIC -> _authType.value = AuthenticationType.Basic(hostname = endpoint, withCloud = false)

            AuthType.UNKNOWN -> _authType.value = AuthenticationType.Unknown()
        }
    }

    override fun handleSSOTokenResponse(model: PkceAuthUiModel) {
        _authResult.value = model
    }

    fun setHasNavigation(enableNavigation: Boolean) {
        _hasNavigation.value = enableNavigation
    }

    fun ssoLogin(identityServiceUrl: String, processRepositoryUrl: String) {
        this.identityServiceUrl = identityServiceUrl
        this.processRepositoryUrl = processRepositoryUrl

        _startSSO.value = identityServiceUrl
    }
}

sealed class AuthenticationType {

    data class Basic(val hostname: String, val withCloud: Boolean) : AuthenticationType()

    data class SSO(val endpoint: String) : AuthenticationType()

    class Unknown : AuthenticationType()
}


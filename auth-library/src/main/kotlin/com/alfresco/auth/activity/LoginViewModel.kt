package com.alfresco.auth.activity

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alfresco.android.aims.BuildConfig
import com.alfresco.android.aims.R
import com.alfresco.auth.AuthConfig
import com.alfresco.auth.Credentials
import com.alfresco.auth.config.defaultConfig
import com.alfresco.auth.ui.AuthenticationViewModel
import com.alfresco.core.data.LiveEvent
import com.alfresco.core.data.MutableLiveEvent
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class LoginViewModel(private val applicationContext: Context) : AuthenticationViewModel() {

    lateinit var authConfig: AuthConfig
    override var context = applicationContext

    private val _hasNavigation = MutableLiveData<Boolean>()
    private val _onShowHelp = MutableLiveEvent<Int>()
    private val _onShowSettings = MutableLiveEvent<Int>()
    private val _startSSO = MutableLiveEvent<String>()

    val hasNavigation: LiveData<Boolean> get() = _hasNavigation
    val onShowHelp: LiveEvent<Int> = _onShowHelp
    val onShowSettings: LiveEvent<Int> = _onShowSettings
    val identityUrl = MutableLiveData<String>()
    val applicationUrl = MutableLiveData<String>()
    val startSSO: LiveEvent<String> get() = _startSSO

    lateinit var authConfigEditor: AuthConfigEditor
        private set

    init {
        loadSavedConfig()

        if (BuildConfig.DEBUG) {
            identityUrl.value = "alfresco-identity-service.mobile.dev.alfresco.me"
//            identityUrl.value = "activiti.alfresco.com"
            applicationUrl.value = "alfresco-cs-repository.mobile.dev.alfresco.me"
        }
    }

    fun getApplicationServiceUrl(): String {
        val protocol = if (authConfig.https) "https" else "http"
        val port = if ((authConfig.https && authConfig.port == "443") ||
                (!authConfig.https && authConfig.port == "80")) ""
                else ":${authConfig.port}"
        return "${protocol}://${applicationUrl.value}${port}/${authConfig.serviceDocuments}/"
    }

    fun setHasNavigation(enableNavigation: Boolean) {
        _hasNavigation.value = enableNavigation
    }

    fun ssoLogin(identityServiceUrl: String) {
        _startSSO.value = identityServiceUrl
    }

    fun startEditing() {
        authConfigEditor = AuthConfigEditor()
        authConfigEditor.reset(authConfig)
    }

    fun connect() {
        initServiceWith(authConfig)
        identityUrl.value?.let {
            checkAuthType(it)
        }
    }

    fun ssoLogin() {
        ssoLogin(identityUrl.value!!)
    }

    fun showSettings() {
        _onShowSettings.value = 0
    }

    fun showWelcomeHelp() {
        _onShowHelp.value = R.string.auth_help_identity_body
    }

    fun showSettingsHelp() {
        _onShowHelp.value = R.string.auth_help_settings_body
    }

    fun showSsoHelp() {
        _onShowHelp.value = R.string.auth_help_sso_body
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun loadSavedConfig() {
        val sharedPrefs  = applicationContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val configJson = sharedPrefs.getString(SHARED_PREFS_CONFIG_KEY, null)

        authConfig = try {
            if (configJson != null)
                Gson().fromJson(configJson, AuthConfig::class.java)
            else
                AuthConfig.defaultConfig
        } catch (e: JsonSyntaxException) {
            AuthConfig.defaultConfig
        }
    }

    fun saveConfigChanges() {
        val config = authConfigEditor.get()

        val sharedPrefs  = applicationContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString(SHARED_PREFS_CONFIG_KEY, Gson().toJson(config))
        editor.apply()

        authConfig = config
    }

    fun resetToDefaultConfig() {
        authConfigEditor.reset(AuthConfig.defaultConfig)
    }

    val basicAuth = BasicAuth()
    inner class BasicAuth {
        val email = MutableLiveData<String>()
        val password = MutableLiveData<String>()

        fun login() {
            // Assume application url is the same as identity for basic auth
            applicationUrl.value = identityUrl.value

            // TODO: nullability check
            _onCredentials.value = Credentials.Basic(email.value!!, password.value!!)
        }
    }

    companion object {
        private const val SHARED_PREFS_NAME = "org.activiti.aims.android.auth"
        private const val SHARED_PREFS_CONFIG_KEY = "config"

        fun with(context: Context): LoginViewModel {
            return LoginViewModel(context)
        }
    }

    class AuthConfigEditor() {
        val https = MutableLiveData<Boolean>()
        val port = MutableLiveData<String>()
        val serviceDocuments = MutableLiveData<String>()
        val realm = MutableLiveData<String>()
        val clientId = MutableLiveData<String>()
        val redirectUrl = MutableLiveData<String>()

        init {
            https.observeForever{
                port.value = if(it == true) DEFAULT_HTTPS_PORT else DEFAULT_HTTP_PORT
            }
        }

        fun reset(config: AuthConfig) {
            https.value = config.https
            port.value = config.port
            serviceDocuments.value = config.serviceDocuments
            realm.value = config.realm
            clientId.value = config.clientId
            redirectUrl.value = config.redirectUrl
        }

        fun get(): AuthConfig {
            return AuthConfig(
                    https = https.value ?: false,
                    port = port.value ?: "",
                    serviceDocuments = serviceDocuments.value ?: "",
                    realm = realm.value ?: "",
                    clientId = clientId.value ?: "",
                    redirectUrl = redirectUrl.value ?: ""
            )
        }

        companion object {
            private const val DEFAULT_HTTP_PORT = "80"
            private const val DEFAULT_HTTPS_PORT = "443"
        }
    }
}

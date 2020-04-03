package com.alfresco.auth.activity

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.alfresco.android.aims.BuildConfig
import com.alfresco.android.aims.R
import com.alfresco.auth.AuthConfig
import com.alfresco.auth.Credentials
import com.alfresco.auth.config.defaultConfig
import com.alfresco.auth.ui.AuthenticationViewModel
import com.alfresco.core.data.LiveEvent
import com.alfresco.core.data.MutableLiveEvent
import com.alfresco.core.extension.isNotBlankNorEmpty
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
    val isLoading = MutableLiveData<Boolean>()
    val identityUrl = MutableLiveData<String>("")
    val applicationUrl = MutableLiveData<String>("")
    val startSSO: LiveEvent<String> get() = _startSSO

    val connectEnabled: LiveData<Boolean>
    val ssoLoginEnabled: LiveData<Boolean>

    lateinit var authConfigEditor: AuthConfigEditor
        private set

    init {
        loadSavedConfig()

        connectEnabled = Transformations.map(identityUrl) { it.isNotBlankNorEmpty() }
        ssoLoginEnabled = Transformations.map(applicationUrl) { it.isNotBlankNorEmpty() }

        if (BuildConfig.DEBUG) {
            identityUrl.value = "alfresco-identity-service.mobile.dev.alfresco.me"
//            identityUrl.value = "activiti.alfresco.com"
            applicationUrl.value = "alfresco-cs-repository.mobile.dev.alfresco.me"
        }
    }

    fun getApplicationServiceUrl(): String {
        return authService.serviceDocumentsEndpoint(applicationUrl.value!!).toString()
    }

    fun setHasNavigation(enableNavigation: Boolean) {
        _hasNavigation.value = enableNavigation
    }

    fun startEditing() {
        authConfigEditor = AuthConfigEditor()
        authConfigEditor.reset(authConfig)
    }

    fun connect() {
        isLoading.value = true

        try {
            initServiceWith(authConfig)
            checkAuthType(identityUrl.value!!)
        } catch (ex: Exception) {
            _onError.value = ex.message
        }
    }

    fun ssoLogin() {
        isLoading.value = true

        try {
            _startSSO.value = identityUrl.value!!
        } catch (ex: Exception) {
            _onError.value = ex.message
        }
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

        // Save state to persistent storage
        val sharedPrefs  = applicationContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString(SHARED_PREFS_CONFIG_KEY, Gson().toJson(config))
        editor.apply()

        // Update local field
        authConfig = config

        // Reset the editor (will update changed state)
        authConfigEditor.reset(config)
    }

    val basicAuth = BasicAuth()
    inner class BasicAuth {
        private val _enabled = MediatorLiveData<Boolean>()

        val email = MutableLiveData<String>()
        val password = MutableLiveData<String>()
        val enabled: LiveData<Boolean> get() = _enabled

        init {
            _enabled.addSource(email, this::onFieldChange)
            _enabled.addSource(password, this::onFieldChange)
        }

        private fun onFieldChange(ignored: String) {
            _enabled.value = !email.value.isNullOrEmpty() && !password.value.isNullOrEmpty()
        }

        fun login() {
            isLoading.value = true

            // Assume application url is the same as identity for basic auth
            applicationUrl.value = identityUrl.value

            _onCredentials.value = Credentials.Basic(email.value ?: "", password.value ?: "")
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
        private lateinit var source: AuthConfig
        private val _changed = MediatorLiveData<Boolean>()

        val https = MutableLiveData<Boolean>()
        val port = MediatorLiveData<String>()
        val serviceDocuments = MutableLiveData<String>()
        val realm = MutableLiveData<String>()
        val clientId = MutableLiveData<String>()
        val redirectUrl = MutableLiveData<String>()

        val changed: LiveData<Boolean> get() = _changed

        init {
            port.addSource(https) {
                port.value = if(it == true) DEFAULT_HTTPS_PORT else DEFAULT_HTTP_PORT
            }

            _changed.addSource(port, this::onChange)
            _changed.addSource(serviceDocuments, this::onChange)
            _changed.addSource(realm, this::onChange)
            _changed.addSource(clientId, this::onChange)
        }

        private fun onChange(ignored: Boolean) {
            onChange()
        }

        private fun onChange(ignored: String) {
            onChange()
        }

        private fun onChange() {
            _changed.value = get() != source
        }

        fun reset(config: AuthConfig) {
            source = config
            load(config)
        }

        fun resetToDefaultConfig() {
            // Source is not changed as resetting to default does not commit changes
            load(AuthConfig.defaultConfig)
        }

        private fun load(config: AuthConfig) {
            https.value = config.https
            port.value = config.port
            serviceDocuments.value = config.serviceDocuments
            realm.value = config.realm
            clientId.value = config.clientId
            redirectUrl.value = config.redirectUrl
            onChange()
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

package com.alfresco.auth.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.alfresco.android.aims.R
import com.alfresco.auth.*
import com.alfresco.auth.config.defaultConfig
import com.alfresco.auth.ui.AuthenticationViewModel
import com.alfresco.core.data.LiveEvent
import com.alfresco.core.data.MutableLiveEvent
import com.alfresco.core.extension.isNotBlankNorEmpty
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class LoginViewModel(private val applicationContext: Context, authType: AuthType?, authState: String?, authConfig: AuthConfig?, endpoint: String?) : AuthenticationViewModel() {

    lateinit var authConfig: AuthConfig
    override var context = applicationContext

    private val _hasNavigation = MutableLiveData<Boolean>()
    private val _step = MutableLiveData<Step>()
    private val _onShowHelp = MutableLiveEvent<Int>()
    private val _onShowSettings = MutableLiveEvent<Int>()
    private val _onSsoLogin = MutableLiveEvent<String>()

    val hasNavigation: LiveData<Boolean> get() = _hasNavigation
    val step: LiveData<Step> get () =  _step
    val onShowHelp: LiveEvent<Int> = _onShowHelp
    val onShowSettings: LiveEvent<Int> = _onShowSettings
    val onSsoLogin: LiveEvent<String> get() = _onSsoLogin
    val isLoading = MutableLiveData<Boolean>()
    val identityUrl = MutableLiveData<String>("")
    val applicationUrl = MutableLiveData<String>("")

    val connectEnabled: LiveData<Boolean>
    val ssoLoginEnabled: LiveData<Boolean>

    lateinit var authConfigEditor: AuthConfigEditor
        private set

    private val previousAppEndpoint: String? = endpoint
    private val previousAuthState: String? = authState

    val canonicalApplicationUrl: String
        get()  {
            return previousAppEndpoint
                    ?: discoveryService.serviceDocumentsEndpoint(applicationUrl.value!!).toString()
        }

    // Used for display purposes
    val applicationUrlHost: String
        get() {
            return Uri.parse(canonicalApplicationUrl).host ?: ""
        }

    init {
        if (previousAuthState != null) {
            isReLogin = true

            if (authType == AuthType.PKCE) {
                moveToStep(Step.EnterPkceCredentials)
            } else {
                moveToStep(Step.EnterBasicCredentials)
            }
        } else {
            moveToStep(Step.InputIdentityServer)
        }

        if (authConfig != null) {
            this.authConfig = authConfig
        } else {
            loadSavedConfig()
        }

        connectEnabled = Transformations.map(identityUrl) { it.isNotBlankNorEmpty() }
        ssoLoginEnabled = Transformations.map(applicationUrl) { it.isNotBlankNorEmpty() }
    }

    fun setHasNavigation(enableNavigation: Boolean) {
        _hasNavigation.value = enableNavigation
    }

    override fun onAuthType(authType: AuthType) {
        when (authType) {
            AuthType.PKCE -> {
                moveToStep(Step.InputAppServer)
            }

            AuthType.BASIC -> {
                moveToStep(Step.EnterBasicCredentials)
            }

            AuthType.UNKNOWN -> {
                _onError.value = context.getString(R.string.auth_error_check_connect_url)
            }
        }
    }

    fun startEditing() {
        authConfigEditor = AuthConfigEditor()
        authConfigEditor.reset(authConfig)
    }

    fun connect() {
        isLoading.value = true

        try {
            checkAuthType(identityUrl.value!!, authConfig)
        } catch (ex: Exception) {
            _onError.value = ex.message
        }
    }

    fun ssoLogin() {
        isLoading.value = true

        pkceAuth.initServiceWith(authConfig, previousAuthState)

        try {
            _onSsoLogin.value = identityUrl.value!!
        } catch (ex: Exception) {
            _onError.value = ex.message
        }
    }

    override fun onPkceAuthCancelled() {
        if (isReLogin) {
            moveToStep(Step.Cancelled)
        } else {
            isLoading.value = false
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

    private fun moveToStep(step: Step) {
        this.isLoading.value = false

        when (step) {
            Step.InputIdentityServer -> { }
            Step.InputAppServer -> {
                applicationUrl.value = ""
            }
            Step.EnterBasicCredentials -> {
                // Assume application url is the same as identity for basic auth
                applicationUrl.value = identityUrl.value
            }
            Step.EnterPkceCredentials -> { }
            Step.Cancelled -> { }
        }

        _step.value = step
    }

    enum class Step {
        InputIdentityServer,
        InputAppServer,
        EnterBasicCredentials,
        EnterPkceCredentials,
        Cancelled;
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

            val username = email.value ?: ""
            val state = AuthInterceptor.basicState(username, password.value ?: "")
            _onCredentials.value = Credentials(username, state, AuthType.BASIC.value)
        }
    }

    companion object {
        private const val SHARED_PREFS_NAME = "org.activiti.aims.android.auth"
        private const val SHARED_PREFS_CONFIG_KEY = "config"

        const val EXTRA_ENDPOINT = "endpoint"
        const val EXTRA_AUTH_TYPE = "authType"
        const val EXTRA_AUTH_STATE = "authState"
        const val EXTRA_AUTH_CONFIG = "authConfig"

        fun with(context: Context, intent: Intent): LoginViewModel {
            var config: AuthConfig? = null
            var stateString: String? = null
            var authType: AuthType? = null
            var endpoint: String? = null

            val extras = intent.extras
            if (extras != null) {
                config = try {
                    AuthConfig.jsonDeserialize(extras.getString(EXTRA_AUTH_CONFIG)!!)
                } catch (ex: Exception) { null }

                stateString = extras.getString(EXTRA_AUTH_STATE)
                endpoint = extras.getString(EXTRA_ENDPOINT)
                authType = extras.getString(EXTRA_AUTH_TYPE)?.let { AuthType.fromValue(it) }
            }

            return LoginViewModel(context, authType, stateString, config, endpoint)
        }
    }

    class AuthConfigEditor() {
        private lateinit var source: AuthConfig
        private val _changed = MediatorLiveData<Boolean>()

        val https = MutableLiveData<Boolean>()
        val port = MutableLiveData<String>()
        val serviceDocuments = MutableLiveData<String>()
        val realm = MutableLiveData<String>()
        val clientId = MutableLiveData<String>()
        val redirectUrl = MutableLiveData<String>()

        val changed: LiveData<Boolean> get() = _changed

        init {
            _changed.addSource(https, this::onChange)
            _changed.addSource(port, this::onChange)
            _changed.addSource(serviceDocuments, this::onChange)
            _changed.addSource(realm, this::onChange)
            _changed.addSource(clientId, this::onChange)
        }

        /**
         * This function is meant to change the port when the user interacts with it.
         *
         * It is important that this function is bound [android.view.View.OnClickListener]
         * instead of [android.widget.CompoundButton.setOnCheckedChangeListener] or as
         * a [MediatorLiveData]  object as it will change the [port] incorrectly when
         * loading the bindings.
         */
        fun onHttpsToggle() {
            port.value = if(https.value == true) DEFAULT_HTTPS_PORT else DEFAULT_HTTP_PORT
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

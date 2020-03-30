package com.alfresco.auth.activity

import android.content.Context
import android.os.Bundle
import com.alfresco.auth.AuthConfig
import com.alfresco.auth.ui.EndSessionActivity
import com.alfresco.auth.ui.EndSessionViewModel
import com.alfresco.common.getViewModel
import org.json.JSONException

class LogoutViewModel(context: Context, authState: String, authConfig: AuthConfig) : EndSessionViewModel(context, authState, authConfig) {
    companion object {
        const val EXTRA_AUTH_STATE = "authState"
        const val EXTRA_AUTH_CONFIG = "authConfig"

        fun with(context: Context, bundle: Bundle?): LogoutViewModel {
            requireNotNull(bundle)

            val stateString = bundle.getString(EXTRA_AUTH_STATE)
            val configString = bundle.getString(EXTRA_AUTH_CONFIG)

            val config = try {
                if (configString != null) {
                    AuthConfig.jsonDeserialize(configString)
                } else {
                    null
                } } catch (ex: JSONException) {
                null
            }

            requireNotNull(stateString)
            requireNotNull(config)

            return LogoutViewModel(context, stateString, config)
        }
    }
}

class LogoutActivity : EndSessionActivity<LogoutViewModel>() {
    override val viewModel:LogoutViewModel  by lazy {
        getViewModel {
            LogoutViewModel.with(applicationContext, intent.extras)
        }
    }

}

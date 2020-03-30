package com.alfresco.auth.activity

import android.content.Context
import android.os.Bundle
import com.alfresco.auth.AuthConfig
import com.alfresco.auth.ui.ReAuthenticateActivity
import com.alfresco.auth.ui.ReAuthenticateViewModel
import com.alfresco.common.getViewModel
import org.json.JSONException

class ReloginViewModel(context: Context, authState: String, authConfig: AuthConfig) :
        ReAuthenticateViewModel(context, authState, authConfig) {

    companion object {
        const val EXTRA_AUTH_STATE = "authState"
        const val EXTRA_AUTH_CONFIG = "authConfig"

        fun with(context: Context, bundle: Bundle?): ReloginViewModel {
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

            return ReloginViewModel(context, stateString, config)
        }
    }
}

abstract class ReloginActivity : ReAuthenticateActivity<ReloginViewModel>() {

    override val viewModel: ReloginViewModel by lazy {
        getViewModel {
            ReloginViewModel.with(applicationContext, intent.extras)
        }
    }
}

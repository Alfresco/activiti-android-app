package com.alfresco.auth.utils

import android.content.Context
import com.alfresco.android.aims.R
import com.alfresco.auth.AuthConfig
import com.alfresco.auth.AuthType
import net.openid.appauth.AuthState

class AccountDetailsHelper {

    data class Item (
        var title: String,
        var value: String
    )

    companion object {
        @JvmStatic
        fun itemsWith(context: Context, username: String, authType: String, authState: String, authConfig: String, serverUrl: String) : List<Item> {
            val type = AuthType.fromValue(authType)
            val result = ArrayList<Item>()

            val appName = context.getString(R.string.auth_app_name)
            result.add(Item(context.getString(R.string.auth_account_details_server, appName), serverUrl))

            when (type) {
                AuthType.BASIC -> result.addAll(basicInformation(context, authConfig))
                AuthType.PKCE -> result.addAll(pkceInformation(context, authState, authConfig))
                AuthType.UNKNOWN -> return emptyList()
            }

            return result
        }

        private fun commonInformation(context: Context, authConfig: AuthConfig) : List<Item> {
            val list = ArrayList<Item>()

            list.add(Item(context.getString(R.string.auth_account_details_protocol), if (authConfig.https) "HTTPS" else "HTTP"))
            list.add(Item(context.getString(R.string.auth_account_details_port), authConfig.port))
            list.add(Item(context.getString(R.string.auth_account_details_service_docs), authConfig.serviceDocuments))

            return list
        }

        private fun basicInformation(context: Context, authConfig: String) : List<Item> {
            val list = ArrayList<Item>()

            AuthConfig.jsonDeserialize(authConfig)?.let {
                list.addAll(commonInformation(context, it))
            }

            return list
        }

        private fun pkceInformation(context: Context, authState: String, authConfig: String) : List<Item> {
            val list = ArrayList<Item>()

            AuthState.jsonDeserialize(authState)?.let {
                list.add(Item(context.getString(R.string.auth_account_details_identity), it.authorizationServiceConfiguration?.authorizationEndpoint.toString()))
            }

            AuthConfig.jsonDeserialize(authConfig)?.let {
                list.addAll(commonInformation(context, it))
                list.add(Item(context.getString(R.string.auth_account_details_client_id), it.clientId))
                list.add(Item(context.getString(R.string.auth_account_details_realm), it.realm))
            }

            return list
        }
    }
}

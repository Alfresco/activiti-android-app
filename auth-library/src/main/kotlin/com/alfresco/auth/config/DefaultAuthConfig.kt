package com.alfresco.auth.config

import com.alfresco.auth.AuthConfig

class DefaultAuthConfig {

    companion object {
        fun get(): AuthConfig {
            return AuthConfig(
                    https = false,
                    port = "80",
                    clientId = "alfresco-android-aps-app",
                    realm = "alfresco",
                    redirectUrl = "androidapsapp://aims/auth",
                    serviceDocuments = "activiti-app"
            )
        }
    }
}

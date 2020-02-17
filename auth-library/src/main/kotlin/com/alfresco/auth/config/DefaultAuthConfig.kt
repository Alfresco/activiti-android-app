package com.alfresco.auth.activity

import com.alfresco.auth.GlobalAuthConfig

class DefaultAuthConfig {

    companion object {
        fun get(): GlobalAuthConfig {
            return GlobalAuthConfig(
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

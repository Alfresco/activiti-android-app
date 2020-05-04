package com.alfresco.auth.config

import com.alfresco.auth.AuthConfig

val AuthConfig.Companion.defaultConfig: AuthConfig
    get() = AuthConfig(
            https = true,
            port = "443",
            clientId = "alfresco-android-aps-app",
            realm = "alfresco",
            redirectUrl = "androidapsapp://aims/auth",
            serviceDocuments = "activiti-app"
    )

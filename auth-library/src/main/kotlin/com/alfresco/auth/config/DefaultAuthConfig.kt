package com.alfresco.auth.config

import com.alfresco.auth.AuthConfig

val AuthConfig.Companion.defaultConfig: AuthConfig
    get() = AuthConfig(
            https = false,
            port = "80",
            clientId = "alfresco-android-aps-app",
            realm = "alfresco",
            redirectUrl = "androidapsapp://aims/auth",
            serviceDocuments = "activiti-app"
    )

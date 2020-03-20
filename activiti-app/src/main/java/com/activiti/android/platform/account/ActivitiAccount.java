/*
 *  Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco Activiti Mobile for Android.
 *
 * Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.activiti.android.platform.account;

import java.io.Serializable;

import android.accounts.AccountManager;

import com.activiti.android.app.BuildConfig;
import com.alfresco.auth.AuthConfig;
import com.alfresco.client.AbstractClient.AuthType;
import com.google.gson.Gson;

import net.openid.appauth.AuthState;

public class ActivitiAccount implements Serializable
{
    /**
     * Account Manager : Account authType
     */
    public static final String ACCOUNT_TYPE = BuildConfig.ACCOUNT_ID;

    // ///////////////////////////////////////////////////////////////////////////
    // ACCOUNT INFO
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * Unique internal account ID
     */
    public static final String ACCOUNT_ID = ACCOUNT_TYPE.concat(".providerId");

    /**
     * Name/Label (Description) of the account
     */
    public static final String ACCOUNT_TITLE = ACCOUNT_TYPE.concat(".title");

    /**
     * Type of credentials, either basic or bearer. Basic is assumed if missing.
     */
    public static final String ACCOUNT_AUTH_TYPE = ACCOUNT_TYPE.concat(".authType");

    public static final String ACCOUNT_AUTH_STATE = ACCOUNT_TYPE.concat(".authState");

    public static final String ACCOUNT_AUTH_CONFIG = ACCOUNT_TYPE.concat(".authConfig");

    // ///////////////////////////////////////////////////////////////////////////
    // SERVER INFO
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * Endpoint URL of the server
     */
    public static final String ACCOUNT_SERVER_URL = ACCOUNT_TYPE.concat(".serverUrl");

    /**
     * Server Type
     */
    public static final String ACCOUNT_SERVER_TYPE = ACCOUNT_TYPE.concat(".serverType");

    /**
     * Server Edition
     */
    public static final String ACCOUNT_SERVER_EDITION = ACCOUNT_TYPE.concat(".serverEdition");

    /**
     * Server Version Number
     */
    public static final String ACCOUNT_SERVER_VERSION = ACCOUNT_TYPE.concat(".serverVersion");

    // ///////////////////////////////////////////////////////////////////////////
    // USER INFO
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * Username
     */
    public static final String ACCOUNT_USERNAME = ACCOUNT_TYPE.concat(".username");

    /**
     * Activiti User Id
     */
    public static final String ACCOUNT_USER_ID = ACCOUNT_TYPE.concat(".userId");

    /**
     * Activiti Tenant Id
     */
    public static final String ACCOUNT_TENANT_ID = ACCOUNT_TYPE.concat(".tenantId");

    /**
     * Activiti FullName
     */
    public static final String ACCOUNT_USER_FULLNAME = ACCOUNT_TYPE.concat(".fullname");

    private static final long serialVersionUID = 1L;

    // ///////////////////////////////////////////////////////////////////////////
    // MEMBERS
    // ///////////////////////////////////////////////////////////////////////////
    private long id;

    private String label;

    private String serverUrl;

    private String serverType;

    private String serverEdition;

    private String serverVersion;

    private String userId;

    private String fullname;

    private String username;

    private String password;

    private String authType;

    private String authState;

    private String authConfig;

    private String tenantId;

    private boolean isAdmin = false;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public ActivitiAccount()
    {
    }

    /** Create a Activiti account. */
    public ActivitiAccount(long id, String username, String password, String authType, String authState, String authConfig, String serverUrl, String label,
            String serverType, String serverEdition, String serverVersion, String userId, String fullname,
            String tenantId)
    {
        super();
        this.id = id;
        this.label = label;
        this.userId = userId;
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.authType = authType;
        this.authState = authState;
        this.authConfig = authConfig;
        this.serverUrl = serverUrl;
        this.serverType = serverType;
        this.serverEdition = serverEdition;
        this.serverVersion = serverVersion;
        this.fullname = fullname;
        this.tenantId = tenantId;
    }

    public static ActivitiAccount parse(AccountManager mAccountManager, android.accounts.Account account)
    {
        ActivitiAccount acc = new ActivitiAccount();
        acc.id = Long.parseLong(mAccountManager.getUserData(account, ACCOUNT_ID));
        acc.label = mAccountManager.getUserData(account, ACCOUNT_TITLE);
        acc.serverUrl = mAccountManager.getUserData(account, ACCOUNT_SERVER_URL);
        acc.username = mAccountManager.getUserData(account, ACCOUNT_USERNAME);
        acc.password = mAccountManager.getPassword(account);
        acc.userId = mAccountManager.getUserData(account, ACCOUNT_USER_ID);
        acc.serverType = mAccountManager.getUserData(account, ACCOUNT_SERVER_TYPE);
        acc.serverEdition = mAccountManager.getUserData(account, ACCOUNT_SERVER_EDITION);
        acc.serverVersion = mAccountManager.getUserData(account, ACCOUNT_SERVER_VERSION);
        acc.label = mAccountManager.getUserData(account, ACCOUNT_TITLE);
        acc.fullname = mAccountManager.getUserData(account, ACCOUNT_USER_FULLNAME);
        acc.tenantId = mAccountManager.getUserData(account, ACCOUNT_TENANT_ID);
        acc.authType = mAccountManager.getUserData(account, ACCOUNT_AUTH_TYPE);
        acc.authState = mAccountManager.getUserData(account, ACCOUNT_AUTH_STATE);
        acc.authConfig = mAccountManager.getUserData(account, ACCOUNT_AUTH_CONFIG);

        // Backwards compatibility: assume all accounts without authType use basic auth
        if (acc.authType == null) {
            acc.authType = AuthType.BASIC.getValue();
        }
        return acc;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS
    // ///////////////////////////////////////////////////////////////////////////
    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public AuthType getAuthType()
    {
        if (authType.equals(AuthType.TOKEN.getValue())) {
            return AuthType.TOKEN;
        }
        return AuthType.BASIC;
    }

    public AuthConfig getAuthConfig() {
        return new Gson().fromJson(authConfig, AuthConfig.class);
    }

    public String getAuthState() {
        return authState;
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public long getId()
    {
        return id;
    }

    public String getLabel()
    {
        return label;
    }

    public String getServerType()
    {
        return serverType;
    }

    public String getServerEdition()
    {
        return serverEdition;
    }

    public String getServerVersion()
    {
        return serverVersion;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserFullname()
    {
        return fullname;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin)
    {
        this.isAdmin = isAdmin;
    }
}

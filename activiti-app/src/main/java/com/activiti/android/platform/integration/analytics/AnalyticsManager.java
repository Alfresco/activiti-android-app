/*
 *  Copyright (C) 2005-2016 Alfresco Software Limited.
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
package com.activiti.android.platform.integration.analytics;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;

import com.activiti.android.platform.Manager;
import com.activiti.android.platform.account.ActivitiAccount;

public abstract class AnalyticsManager extends Manager
{

    ///////////////////////////////////////////////////////////
    // EVENTS : CATEGORIES
    ///////////////////////////////////////////////////////////
    public static final String CATEGORY_ACCOUNT = "Account";

    public static final String CATEGORY_SESSION = "Session";

    public static final String CATEGORY_DOCUMENT_MANAGEMENT = "DM";

    public static final String CATEGORY_PROCESS = "Process";

    public static final String CATEGORY_TASK = "Task";

    public static final String CATEGORY_SETTINGS = "Settings";

    public static final String CATEGORY_FILTERS = "User Filters";

    ///////////////////////////////////////////////////////////
    // EVENTS : ACTIONS
    ///////////////////////////////////////////////////////////
    public static final String ACTION_INFO = "Info";

    public static final String ACTION_SEARCH = "Search";

    public static final String ACTION_SWITCH = "Switch";

    public static final String ACTION_CREATE = "Create";

    public static final String ACTION_COMMENT = "Comment";

    public static final String ACTION_VIEW = "View";

    public static final String ACTION_OPEN = "Open";

    public static final String ACTION_OPEN_BROWSER = "Open Browser";

    public static final String ACTION_OPEN_APP = "Open App";

    public static final String ACTION_DOWNLOAD = "Download";

    public static final String ACTION_EDIT = "Edit";

    public static final String ACTION_UPDATE = "Update";

    public static final String ACTION_DELETE = "Delete";

    public static final String ACTION_ANALYTICS = "Analytics";

    public static final String ACTION_SEND = "Send";

    public static final String ACTION_SHARE = "Share";

    public static final String ACTION_REASSIGN = "Reassign";

    public static final String ACTION_COMPLETE_TASK = "Complete";

    public static final String ACTION_FORM = "Form";

    public static final String ACTION_ADD = "Add";

    public static final String ACTION_REMOVE = "Remove";

    public static final String ACTION_USER_INVOLVED = "User Involved";

    public static final String ACTION_CLAIM = "Claim";

    public static final String ACTION_ADD_CONTENT = "Add Content";

    public static final String ACTION_LINK_CONTENT = "Link Content";

    public static final String ACTION_ALFRESCO_INTEGRATION = "Alfresco Integration";

    ///////////////////////////////////////////////////////////
    // EVENTS : LABELS
    ///////////////////////////////////////////////////////////
    public static final String LABEL_WITH_FORM = "With Form";

    public static final String LABEL_WITHOUT_FORM = "Without Form";

    public static final String LABEL_TASK = "Task";

    public static final String LABEL_PROCESS = "Process";

    public static final String LABEL_SUBTASK = "SubTask";

    public static final String LABEL_USER = "User";

    public static final String LABEL_SAVE = "Save";

    public static final String LABEL_FAILED = "Failed";

    public static final String LABEL_DISABLE = "Disable";

    public static final String LABEL_ENABLE = "Enable";

    public static final String LABEL_LINK = "Link";

    public static final String LABEL_APPS = "Apps";

    public static final String LABEL_LINK_MOBILE = "Link Mobile";

    public static final String LABEL_LINK_WEB = "Link Web";

    public static final String LABEL_REMOVE = "Remove";

    public static final String LABEL_AS_LIST = "List";

    public static final String LABEL_AS_GRID = "Grid";

    ///////////////////////////////////////////////////////////
    // CUSTOM DIMENSIONS
    ///////////////////////////////////////////////////////////
    // Beware to have the same index as defined in GAnalytics
    public static final int INDEX_SERVER_TYPE = 1;

    public static final int INDEX_SERVER_VERSION = 2;

    public static final int INDEX_SERVER_EDITION = 3;

    ///////////////////////////////////////////////////////////
    // CUSTOM METRICS
    ///////////////////////////////////////////////////////////
    // Beware to have the same index as defined in GAnalytics
    public static final int INDEX_ACCOUNT_NUMBER = 1;

    public static final int INDEX_SESSION_CREATION = 2;

    public static final int INDEX_ALFRESCO_NUMBER = 3;

    public static final int INDEX_APPS_NUMBER = 4;

    public static final int INDEX_PROCESS_DEFINITION_NUMBER = 5;

    ///////////////////////////////////////////////////////////
    // SCREEN NAME
    ///////////////////////////////////////////////////////////

    // TODO

    // ////////////////////////////////////////////////////
    // SETTINGS
    // ////////////////////////////////////////////////////
    protected static final String ANALYTICS_PREFIX = "Analytics-";

    public static final int STATUS_BLOCKED = -1;

    public static final int STATUS_DISABLE = 0;

    public static final int STATUS_ENABLE = 1;

    protected static final Object LOCK = new Object();

    protected static Manager mInstance;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public static AnalyticsManager getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = Manager.getInstance(context, AnalyticsManager.class.getSimpleName());
            }

            return (AnalyticsManager) mInstance;
        }
    }

    protected AnalyticsManager(Context applicationContext)
    {
        super(applicationContext);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public abstract void cleanOptInfo(Context context, ActivitiAccount account);

    /**
     * OptIn check must be done prior to call this method.
     * 
     * @param activity
     */
    public abstract void optIn(Activity activity);

    /**
     * optOut check must be done prior to call this method.
     * 
     * @param activity
     */
    public abstract void optOut(Activity activity);

    public abstract boolean isEnable();

    public abstract boolean isEnable(ActivitiAccount account);

    // ///////////////////////////////////////////////////////////////////////////
    // ABSTRACT METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public abstract void startReport(Activity activity);

    public abstract void reportScreen(String name);

    public abstract void reportEvent(String category, String action, String label, int value);

    public abstract void reportEvent(String category, String action, String label, int value, int customMetricId,
            Long customMetricValue);

    public abstract void reportEvent(String category, String action, String label, int eventValue,
            SparseArray<String> dimensions, SparseArray<Long> metrics);

    public abstract void reportInfo(String label, SparseArray<String> dimensions, SparseArray<Long> metrics);

    public abstract void reportError(boolean isFatal, String description);

    // ///////////////////////////////////////////////////////////////////////////
    // Fragment Interface
    // ///////////////////////////////////////////////////////////////////////////
    public interface FragmentAnalyzed
    {
        String getScreenName();

        boolean reportAtCreationEnable();
    }

}

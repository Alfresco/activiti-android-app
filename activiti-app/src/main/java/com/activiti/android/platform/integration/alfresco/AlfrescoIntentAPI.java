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

package com.activiti.android.platform.integration.alfresco;

/**
 * @since 1.5
 * @author Jean Marie Pascal
 */
public interface AlfrescoIntentAPI
{
    String SCHEME = "alfresco";

    // ///////////////////////////////////////////////////////////////////////////
    // PREFIX
    // ///////////////////////////////////////////////////////////////////////////
    String PREFIX_ACTION = "com.alfresco.android.intent.action";

    String PREFIX_EXTRA = "com.alfresco.android.intent.extra";

    // TYPE
    // ///////////////////////////////////////////////////////////////////////////
    String AUTHORITY_FOLDER = "folder";

    String AUTHORITY_DOCUMENT = "document";

    String AUTHORITY_FILE = "file";

    // EXTRA
    // ///////////////////////////////////////////////////////////////////////////
    String EXTRA_ACCOUNT_ID = PREFIX_EXTRA.concat(".ACCOUNT_ID");

    String EXTRA_FOLDER_ID = PREFIX_EXTRA.concat(".FOLDER_ID");

    String EXTRA_DOCUMENT_ID = PREFIX_EXTRA.concat(".DOCUMENT_ID");

    // CREATION
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * <h3>Text Editor</h3>
     * <ul>
     * <li>text/plain : Start Alfresco Text Editor
     * </ul>
     */
    String ACTION_CREATE = PREFIX_ACTION.concat(".CREATE");

    String EXTRA_SPEECH2TEXT = PREFIX_EXTRA.concat(".SPEECH2TEXT");

    String ACTION_SEND = PREFIX_ACTION.concat(".SEND");

    // READ
    // ///////////////////////////////////////////////////////////////////////////
    String ACTION_VIEW = PREFIX_ACTION.concat(".VIEW");

    // EDIT/UPDATE
    // ///////////////////////////////////////////////////////////////////////////
    String ACTION_RENAME = PREFIX_ACTION.concat(".RENAME");

    // ///////////////////////////////////////////////////////////////////////////
    // ACCOUNT
    // ///////////////////////////////////////////////////////////////////////////
    String ACTION_CREATE_ACCOUNT = PREFIX_ACTION.concat(".CREATE_ACCOUNT");

    // ///////////////////////////////////////////////////////////////////////////
    // MDM
    // ///////////////////////////////////////////////////////////////////////////
    String EXTRA_ALFRESCO_USERNAME = "AlfrescoUserName";

    String EXTRA_ALFRESCO_DISPLAY_NAME = "AlfrescoDisplayName";

    String EXTRA_ALFRESCO_REPOSITORY_URL = "AlfrescoRepositoryURL";

    String EXTRA_ALFRESCO_SHARE_URL = "AlfrescoShareURL";
}

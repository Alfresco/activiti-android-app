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

package com.activiti.android.platform.storage;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.activiti.android.platform.Manager;

public class AlfrescoStorageManager extends Manager
{
    // ///////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // ///////////////////////////////////////////////////////////////////////////
    private static final String TAG = AlfrescoStorageManager.class.getName();

    private static final String DIRECTORY_TEMP = "Tmp";

    // ///////////////////////////////////////////////////////////////////////////
    // MEMBERS
    // ///////////////////////////////////////////////////////////////////////////
    protected static final Object LOCK = new Object();

    protected static Manager mInstance;

    protected AlfrescoStorageManager(Context applicationContext)
    {
        super(applicationContext);
    }

    public static AlfrescoStorageManager getInstance(Context appContext)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new AlfrescoStorageManager(appContext);
            }
            return (AlfrescoStorageManager) mInstance;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    public void shutdown()
    {
        mInstance = null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // STATUS SDCARD
    // ///////////////////////////////////////////////////////////////////////////
    private static boolean isExternalStorageAccessible()
    {
        return (Environment.getExternalStorageState().compareTo(Environment.MEDIA_MOUNTED) == 0);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // SHORTCUT
    // ///////////////////////////////////////////////////////////////////////////
    public File getTempFolder(Long acccountId)
    {
        return getPrivateFolder(DIRECTORY_TEMP, acccountId);
    }

    public File getPrivateFolder(String requestedFolder, Long acccountId)
    {
        File folder = null;
        try
        {
            // NOTE: We must have access to external storage in order to get a
            // private folder for this Android logged in user.
            if (isExternalStorageAccessible())
            {
                folder = appContext.getExternalFilesDir(null);

                if (acccountId != null)
                {
                    folder = IOUtils.createFolder(folder, acccountId + File.separator + requestedFolder);
                }
                else
                {
                    folder = IOUtils.createFolder(folder, requestedFolder);
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return folder;
    }
}

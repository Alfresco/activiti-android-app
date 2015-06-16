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

package com.activiti.android.sdk.model.runtime;

import java.io.Serializable;

import com.activiti.client.api.model.runtime.AppVersionRepresentation;

/**
 * Created by jpascal on 12/12/2014.
 */
public class AppVersion implements Serializable
{
    public final String bpmSuite;

    public final String type;

    public final String majorVersion;

    public final String minorVersion;

    public final String revisionVersion;

    public final String edition;

    public AppVersion(AppVersionRepresentation appVersionRepresentation)
    {
        this.bpmSuite = appVersionRepresentation.getBpmSuite();
        this.type = appVersionRepresentation.getType();
        this.majorVersion = appVersionRepresentation.getMajorVersion();
        this.minorVersion = appVersionRepresentation.getMinorVersion();
        this.revisionVersion = appVersionRepresentation.getRevisionVersion();
        this.edition = appVersionRepresentation.getEdition();
    }

    public AppVersion(String fullVersion)
    {
        String[] splittedVersion = fullVersion.split("\\.");
        this.majorVersion = splittedVersion[0];
        this.minorVersion = splittedVersion[1];
        this.revisionVersion = splittedVersion[2];
        this.edition = null;
        this.bpmSuite = null;
        this.type = null;
    }

    public String getFullVersion()
    {
        return majorVersion.concat(".").concat(minorVersion).concat(".").concat(revisionVersion);
    }

    public int getFullVersionNumber()
    {
        return Integer.parseInt(majorVersion.concat(minorVersion).concat(revisionVersion));
    }

    public boolean is120OrAbove()
    {
        if (getFullVersionNumber() >= 120) { return true; }
        return false;
    }
}

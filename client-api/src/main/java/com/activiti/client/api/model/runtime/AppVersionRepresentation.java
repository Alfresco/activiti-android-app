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

package com.activiti.client.api.model.runtime;

import java.io.Serializable;

import com.activiti.client.api.model.common.AbstractRepresentation;

/**
 * Created by jpascal on 12/12/2014.
 */
public class AppVersionRepresentation extends AbstractRepresentation implements Serializable
{
    protected String bpmSuite;

    protected String type;

    protected String majorVersion;

    protected String minorVersion;

    protected String revisionVersion;

    protected String edition;

    public AppVersionRepresentation()
    {
    }

    public String getBpmSuite()
    {
        return bpmSuite;
    }

    public void setBpmSuite(String bpmSuite)
    {
        this.bpmSuite = bpmSuite;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getMajorVersion()
    {
        return majorVersion;
    }

    public void setMajorVersion(String majorVersion)
    {
        this.majorVersion = majorVersion;
    }

    public String getMinorVersion()
    {
        return minorVersion;
    }

    public void setMinorVersion(String minorVersion)
    {
        this.minorVersion = minorVersion;
    }

    public String getRevisionVersion()
    {
        return revisionVersion;
    }

    public void setRevisionVersion(String revisionVersion)
    {
        this.revisionVersion = revisionVersion;
    }

    public String getEdition()
    {
        return edition;
    }

    public void setEdition(String edition)
    {
        this.edition = edition;
    }

    public String getFullVersion()
    {
        return getMajorVersion().concat(".").concat(getMinorVersion()).concat(".").concat(getRevisionVersion());
    }
}

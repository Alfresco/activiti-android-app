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

package com.activiti.android.platform.provider.mimetype;

import java.io.Serializable;

import android.content.Context;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public class MimeType implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String TYPE_APPLICATION = "application";

    public static final String TYPE_AUDIO = "audio";

    public static final String TYPE_IMAGE = "image";

    public static final String TYPE_MESSAGE = "message";

    public static final String TYPE_MODEL = "model";

    public static final String TYPE_MULTIPART = "multipart";

    public static final String TYPE_TEXT = "text";

    public static final String TYPE_VIDEO = "video";

    private static final String PREFIX = "R.drawable.";

    private static final String DRAWABLE = "drawable";

    private long id;

    private String extension;

    private String type;

    private String subType;

    private String description;

    private String smallIcon;

    private String largeIcon;

    public MimeType(String type, String subType)
    {
        super();
        this.type = type;
        this.subType = subType;
    }

    public MimeType(String extension, String type, String subType)
    {
        super();
        this.extension = extension;
        this.type = type;
        this.subType = subType;
    }

    public MimeType(long id, String extension, String type, String subType, String description, String smallIcon,
            String largeIcon)
    {
        super();
        this.id = id;
        this.extension = extension;
        this.type = type;
        this.subType = subType;
        this.description = description;
        this.smallIcon = smallIcon;
        this.largeIcon = largeIcon;
    }

    public long getId()
    {
        return id;
    }

    public String getExtension()
    {
        return extension;
    }

    public String getType()
    {
        return type;
    }

    public String getSubType()
    {
        return subType;
    }

    public String getDescription()
    {
        return description;
    }

    public String getSmallIcon()
    {
        return smallIcon;
    }

    public String getLargeIcon()
    {
        return largeIcon;
    }

    public String getMimeType()
    {
        return type + "/" + subType;
    }

    // /////////////////////////////////////////////////////////////////
    // EXTRAS
    // /////////////////////////////////////////////////////////////////

    public Integer getSmallIconId(Context context)
    {
        return context.getResources().getIdentifier(smallIcon.substring(PREFIX.length()), DRAWABLE,
                context.getApplicationContext().getPackageName());
    }

    public Integer getLargeIconId(Context context)
    {
        return context.getResources().getIdentifier(largeIcon.substring(PREFIX.length()), DRAWABLE,
                context.getApplicationContext().getPackageName());
    }

    public static Integer getRessourceId(Context context, String rLabel)
    {
        return context.getResources().getIdentifier(rLabel.substring(PREFIX.length()), DRAWABLE,
                context.getApplicationContext().getPackageName());
    }

    // /////////////////////////////////////////////////////////////////
    // FLAG
    // /////////////////////////////////////////////////////////////////
    public boolean isType(String typeRequested)
    {
        return type != null && type.contains(typeRequested);
    }

}

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

package com.activiti.android.platform.event;

public abstract class AbstractOperationEvent<T> extends OperationEvent
{
    public final String requestId;

    public final T data;

    public AbstractOperationEvent(String requestId, T data, Exception exception)
    {
        super(exception);
        this.requestId = requestId;
        this.data = data;
    }

    public AbstractOperationEvent(String requestId, Exception exception)
    {
        super(exception);
        this.requestId = requestId;
        this.data = null;
    }

    public AbstractOperationEvent(String requestId, T data)
    {
        super(null);
        this.requestId = requestId;
        this.data = data;
    }
}

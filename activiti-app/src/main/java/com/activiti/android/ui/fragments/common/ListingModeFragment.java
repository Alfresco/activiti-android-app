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

package com.activiti.android.ui.fragments.common;

public interface ListingModeFragment
{
    String ARGUMENT_MODE = "org.alfresco.mobile.android.application.param.mode";

    String ARGUMENT_FRAGMENT_TAG = "org.alfresco.mobile.android.application.param.fragment.tag";

    String ARGUMENT_SINGLE_CHOICE = "org.alfresco.mobile.android.application.param.fragment.singleChoice";

    /** Normal case where user can interact with everything. */
    int MODE_LISTING = 1;

    /** Select one or multiple document. */
    int MODE_PICK = 2;

    /** Select a folder */
    int MODE_IMPORT = 4;

    /** Display progress. */
    int MODE_PROGRESS = 4;

    int getMode();

}

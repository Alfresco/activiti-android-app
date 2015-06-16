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

package com.activiti.android.ui.form;

import com.activiti.client.api.model.editor.form.FormFieldTypes;

/**
 * Created by jpascal on 28/03/2015.
 */
public enum FieldTypeRegistry
{
    SINGLE_LINE_TEXT(FormFieldTypes.SINGLE_LINE_TEXT), MULTI_LINE_TEXT(FormFieldTypes.MULTI_LINE_TEXT), INTEGER(
            FormFieldTypes.INTEGER), DATE(FormFieldTypes.DATE), BOOLEAN(FormFieldTypes.BOOLEAN), RADIO_BUTTONS(
            FormFieldTypes.RADIO_BUTTONS), DROPDOWN(FormFieldTypes.DROPDOWN), TYPEAHEAD(FormFieldTypes.TYPEAHEAD), UPLOAD(
            FormFieldTypes.UPLOAD), GROUP(FormFieldTypes.GROUP), READONLY(FormFieldTypes.READONLY), READONLY_TEXT(
            FormFieldTypes.READONLY_TEXT), PEOPLE(FormFieldTypes.PEOPLE), FUNCTIONAL_GROUP(
            FormFieldTypes.FUNCTIONAL_GROUP), DYNAMIC_TABLE(FormFieldTypes.DYNAMIC_TABLE), HYPERLINK(
            FormFieldTypes.HYPERLINK), CONTAINER(FormFieldTypes.CONTAINER), AMOUNT(FormFieldTypes.AMOUNT);

    private final String value;

    FieldTypeRegistry(String v)
    {
        value = v;
    }

    public static FieldTypeRegistry fromValue(String v)
    {
        for (FieldTypeRegistry c : FieldTypeRegistry.values())
        {
            if (c.value.equals(v)) { return c; }
        }
        throw new IllegalArgumentException(v);
    }

    public String value()
    {
        return value;
    }

}

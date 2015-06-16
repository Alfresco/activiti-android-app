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

import android.content.Context;

import com.activiti.android.ui.form.fields.AmountField;
import com.activiti.android.ui.form.fields.BaseField;
import com.activiti.android.ui.form.fields.CheckBoxField;
import com.activiti.android.ui.form.fields.DateField;
import com.activiti.android.ui.form.fields.DropDownField;
import com.activiti.android.ui.form.fields.DynamicTableField;
import com.activiti.android.ui.form.fields.HeaderField;
import com.activiti.android.ui.form.fields.HyperlinkField;
import com.activiti.android.ui.form.fields.MultiLineTextField;
import com.activiti.android.ui.form.fields.NumberField;
import com.activiti.android.ui.form.fields.RadioButtonsField;
import com.activiti.android.ui.form.fields.ReadOnlyTextField;
import com.activiti.android.ui.form.fields.TextField;
import com.activiti.android.ui.form.fields.TypeAhead;
import com.activiti.android.ui.form.fields.UploadPickerField;
import com.activiti.android.ui.form.fields.UserGroupPickerField;
import com.activiti.android.ui.form.fields.UserPickerField;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;

/**
 * Created by jpascal on 28/03/2015.
 */
public class FieldTypeFactory
{
    public static BaseField createField(Context context, FormManager manager, String dataType,
            FormFieldRepresentation data, boolean isReadMode)
    {
        FieldTypeRegistry type = null;
        try
        {
            type = FieldTypeRegistry.fromValue(dataType);
        }
        catch (IllegalArgumentException e)
        {
            // Unsupported field
        }
        switch (type)
        {
            case SINGLE_LINE_TEXT:
                return new TextField(context, manager, data, isReadMode);
            case MULTI_LINE_TEXT:
                return new MultiLineTextField(context, manager, data, isReadMode);
            case INTEGER:
                return new NumberField(context, manager, data, isReadMode);
            case DATE:
                return new DateField(context, manager, data, isReadMode);
            case BOOLEAN:
                return new CheckBoxField(context, manager, data, isReadMode);
            case RADIO_BUTTONS:
                return new RadioButtonsField(context, manager, data, isReadMode);
            case DROPDOWN:
                return new DropDownField(context, manager, data, isReadMode);
            case TYPEAHEAD:
                return new TypeAhead(context, manager, data, isReadMode);
            case UPLOAD:
                return new UploadPickerField(context, manager, data, isReadMode);
            case GROUP:
                return new HeaderField(context, manager, data, isReadMode);
            case READONLY:
                return new TextField(context, manager, data, isReadMode);
            case READONLY_TEXT:
                return new ReadOnlyTextField(context, manager, data, isReadMode);
            case PEOPLE:
                return new UserPickerField(context, manager, data, isReadMode);
            case FUNCTIONAL_GROUP:
                return new UserGroupPickerField(context, manager, data, isReadMode);
            case DYNAMIC_TABLE:
                return new DynamicTableField(context, manager, data, isReadMode);
            case HYPERLINK:
                return new HyperlinkField(context, manager, data, isReadMode);
            case CONTAINER:
                return new TextField(context, manager, data, isReadMode);
            case AMOUNT:
                return new AmountField(context, manager, data, isReadMode);
            default:
                return new TextField(context, manager, data, isReadMode);
        }

    }
}

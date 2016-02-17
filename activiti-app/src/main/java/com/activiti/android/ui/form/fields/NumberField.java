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

package com.activiti.android.ui.form.fields;

import java.text.DecimalFormat;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by jpascal on 28/03/2015.
 */
public class NumberField extends BaseField
{
    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public NumberField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VALUES
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object getEditionValue()
    {
        if (editionView instanceof MaterialEditText)
        {
            String value = ((MaterialEditText) editionView).getText().toString();
            if (TextUtils.isEmpty(value))
            {
                return null;
            }
            else if ("-".equals(value))
            {
                return null;
            }
            else
            {
                return Double.parseDouble(value);
            }
        }
        return null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // READ
    // ///////////////////////////////////////////////////////////////////////////
    public String getHumanReadableReadValue()
    {
        if (originalValue == null) { return getString(R.string.form_message_empty); }
        if (editionValue instanceof Double)
        {
            DecimalFormat df = new DecimalFormat("0.#");
            return df.format((Double) editionValue);
        }
        return originalValue.toString();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VALUE
    // ///////////////////////////////////////////////////////////////////////////
    public String getHumanReadableEditionValue()
    {
        if (editionValue == null) { return null; }
        if (editionValue instanceof Double)
        {
            DecimalFormat df = new DecimalFormat("0.#");
            return df.format((Double) editionValue);
        }
        return editionValue.toString();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public View setupEditionView(Object value)
    {
        MaterialEditText edit = (MaterialEditText) super.setupEditionView(value);
        edit.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_SIGNED);
        return edit;
    }
}

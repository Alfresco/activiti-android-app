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

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;

/**
 * Created by jpascal on 28/03/2015.
 */
public class CheckBoxField extends BaseField
{
    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public CheckBoxField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VALUES
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object getEditionValue()
    {
        return ((CheckBox) editionView).isChecked();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // READ
    // ///////////////////////////////////////////////////////////////////////////
    public String getHumanReadableReadValue()
    {
        return originalValue.toString();
    }

    @Override
    public View setupdReadView()
    {
        if (data.getValue() == null)
        {
            originalValue = false;
        }
        else if (data.getValue() instanceof Boolean)
        {
            originalValue = data.getValue();
        }
        else if (data.getValue() instanceof String)
        {
            originalValue = Boolean.parseBoolean((String) data.getValue());
        }

        CheckBox vr = (CheckBox) inflater.inflate(R.layout.form_checkbox, null);
        vr.setText(data.getName());
        vr.setChecked((Boolean) originalValue);
        vr.setEnabled(false);

        readView = vr;

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    protected void updateEditionView()
    {
        ((CheckBox) editionView).setChecked((Boolean) editionValue);
    }

    public View setupEditionView(Object value)
    {
        editionValue = value != null ? (Boolean) value : false;

        View vr = inflater.inflate(R.layout.form_checkbox, null);
        ((CheckBox) vr).setChecked((Boolean) editionValue);

        // Asterix if required
        ((CheckBox) vr).setText(getLabelText(data.getName()));

        ((CheckBox) vr).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                buttonView.setError(null);
                getFormManager().evaluateViews();
            }
        });

        editionView = vr;

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ERROR
    // ///////////////////////////////////////////////////////////////////////////
    public boolean isValid()
    {
        if (!data.isRequired()) { return true; }
        if (data.isRequired() && (getEditionValue() != null && getEditionValue() == true)) { return true; }
        return false;
    }

    public void showError()
    {
        if (isValid()) { return; }
        ((CheckBox) editionView)
                .setError(String.format(getString(R.string.form_error_message_required), data.getName()));
    }
}

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

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by jpascal on 28/03/2015.
 */
public class ReadOnlyTextField extends MultiLineTextField
{
    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public ReadOnlyTextField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);
        hideLabel = true;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VALUES
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object getEditionValue()
    {
        return null;
    }

    @Override
    public View setupEditionView(Object value)
    {
        View vr = super.setupEditionView(value);
        vr.setEnabled(false);
        ((MaterialEditText) vr).setText(getHumanReadableEditionValue());
        ((MaterialEditText) vr).setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
        ((MaterialEditText) vr).setMetTextColor(getContext().getResources().getColorStateList(
                R.color.form_readonly_text));

        return vr;
    }
}

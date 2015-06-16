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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by jpascal on 28/03/2015.
 */
public class TextField extends BaseField
{
    protected int editLayoutId;

    protected int readLayoutId;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public TextField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);
        editLayoutId = R.layout.form_edit_text;
        readLayoutId = R.layout.form_read_row;
    }

    public View setupdReadView()
    {
        View vr = inflater.inflate(readLayoutId, null);
        HolderUtils.configure(vr, data.getName(), getHumanReadableReadValue(), -1);
        vr.setFocusable(false);

        readView = vr;

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VALUES
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object getEditionValue()
    {
        if (editionView instanceof MaterialEditText)
        {
            editionValue = ((MaterialEditText) editionView).getText().toString();
            return TextUtils.isEmpty((String) editionValue) ? null : editionValue;
        }
        return null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VIEW GENERATOR
    // ///////////////////////////////////////////////////////////////////////////
    public View setupEditionView(Object value)
    {
        editionValue = value;

        View vr = inflater.inflate(editLayoutId, null);
        ((MaterialEditText) vr).setText(getHumanReadableEditionValue());

        // Asterix if required
        ((MaterialEditText) vr).setFloatingLabelText(getLabelText(data.getName()));
        ((MaterialEditText) vr).setHint(getLabelText(data.getName()));

        if (!TextUtils.isEmpty(data.getPlaceholder()))
        {
            ((MaterialEditText) vr).setHint(data.getPlaceholder());
            ((MaterialEditText) vr).setFloatingLabelAlwaysShown(true);
        }

        ((MaterialEditText) vr).addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                getFormManager().evaluateViews();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
        });

        editionView = vr;

        return vr;
    }

}

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
import android.text.InputType;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;

/**
 * Created by jpascal on 28/03/2015.
 */
public class MultiLineTextField extends TextField
{
    protected boolean hideLabel = false;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public MultiLineTextField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);
        editLayoutId = R.layout.form_edit_multilines;
        readLayoutId = R.layout.form_edit_multilines;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VALUES
    // ///////////////////////////////////////////////////////////////////////////
    public View setupdReadView()
    {
        View vr = inflater.inflate(R.layout.form_read_row, null);
        TwoLinesViewHolder tvh = HolderUtils.configure(vr, data.getName(), getHumanReadableReadValue(), -1);
        tvh.bottomText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        tvh.bottomText.setSingleLine(false);
        if (hideLabel)
        {
            tvh.topText.setVisibility(View.GONE);
        }
        vr.setFocusable(false);

        readView = vr;

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VIEW GENERATOR
    // ///////////////////////////////////////////////////////////////////////////
}

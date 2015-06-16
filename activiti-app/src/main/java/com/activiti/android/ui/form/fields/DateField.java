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

import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.form.picker.DatePickerFragment;
import com.activiti.client.api.constant.ISO8601Utils;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by jpascal on 28/03/2015.
 */
public class DateField extends BaseField
{
    protected MaterialEditText editText;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public DateField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // READ
    // ///////////////////////////////////////////////////////////////////////////
    public String getHumanReadableReadValue()
    {
        if (originalValue == null) { return getString(R.string.form_message_empty); }
        String readValue = "";
        if (originalValue instanceof String)
        {
            originalValue = ISO8601Utils.parse((String) originalValue);
        }
        if (originalValue instanceof Date)
        {
            readValue = DateFormat.getMediumDateFormat(getContext()).format(originalValue);
        }
        return readValue;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VALUES
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public String getHumanReadableEditionValue()
    {
        if (editionValue == null) { return ""; }
        String readValue = "";
        if (editionValue instanceof String)
        {
            editionValue = ISO8601Utils.parse((String) editionValue);
        }
        if (editionValue instanceof Date)
        {
            readValue = DateFormat.getMediumDateFormat(getContext()).format(editionValue);
        }
        return readValue;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    protected void updateEditionView()
    {
        editText.setText(getHumanReadableEditionValue());
        getFormManager().evaluateViews();
    }

    @Override
    public View setupEditionView(Object value)
    {
        editionValue = value;
        if (value instanceof String)
        {
            editionValue = ISO8601Utils.parse((String) value);
        }

        View vr = inflater.inflate(R.layout.form_date, null);
        editText = (MaterialEditText) vr.findViewById(R.id.date_picker);
        editText.setText(getHumanReadableEditionValue());

        // Asterix if required
        editText.setFloatingLabelText(getLabelText(data.getName()));

        if (TextUtils.isEmpty(data.getPlaceholder()))
        {
            editText.setHint(getLabelText(data.getName()));
        }
        else
        {
            editText.setFloatingLabelAlwaysShown(true);
            editText.setHint(data.getPlaceholder());
        }
        editText.setFocusable(false);

        editText.setIconRight(R.drawable.ic_event_grey);

        editionView = vr;

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PICKERS
    // ///////////////////////////////////////////////////////////////////////////
    public boolean isPickerRequired()
    {
        return true;
    }

    public void setFragment(AlfrescoFragment fr)
    {
        super.setFragment(fr);
        if (getFragment() != null && editionView != null && !data.isReadOnly())
        {
            editionView.findViewById(R.id.button_container).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DatePickerFragment.newInstance(data.getId(), getFragment().getTag(),
                            (editionValue != null) ? ((Date) editionValue).getTime() : null, null, null, false).show(
                            getFragment().getFragmentManager(), DatePickerFragment.TAG);

                }
            });
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // OUTPUT VALUE
    // ///////////////////////////////////////////////////////////////////////////
    public Object getOutputValue()
    {
        if (editionValue == null) { return null; }
        if (editionValue instanceof Date) { return ISO8601Utils.format((Date) editionValue); }
        if (editionValue instanceof GregorianCalendar) { return ISO8601Utils.format(
                ((GregorianCalendar) editionValue).getTime(), true); }
        return null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ERROR
    // ///////////////////////////////////////////////////////////////////////////
    public void showError()
    {
        if (isValid()) { return; }
        editText.setError(String.format(getString(R.string.form_error_message_required), data.getName()));
    }
}

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

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.common.ListingModeFragment;
import com.activiti.android.ui.fragments.form.picker.UserPickerFragment;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.activiti.client.api.model.idm.LightUserRepresentation;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by jpascal on 28/03/2015.
 */
public class UserPickerField extends BaseField
{
    private MaterialEditText editText;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public UserPickerField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
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
        if (originalValue instanceof Map)
        {
            originalValue = LightUserRepresentation.parse(originalValue);
        }
        if (originalValue instanceof LightUserRepresentation)
        {
            readValue = ((LightUserRepresentation) originalValue).getFullname();
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
        if (editionValue instanceof Map)
        {
            editionValue = LightUserRepresentation.parse(editionValue);
        }
        if (editionValue instanceof LightUserRepresentation)
        {
            readValue = ((LightUserRepresentation) editionValue).getFullname();
        }
        else
        {
            readValue = "Unsupported";
        }
        return readValue;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    protected void updateEditionView()
    {
        if (editText != null) {
            editText.setText(getHumanReadableEditionValue());
        }
        getFormManager().evaluateViews();
    }

    protected void updateReadView() {
        if (editText != null) {
            editText.setText(getHumanReadableReadValue());
        }
        getFormManager().evaluateViews();
    }

    @Override
    public View setupEditionView(Object value)
    {
        if (value != null)
        {
            editionValue = value;
        }

        View vr = inflater.inflate(R.layout.form_user_picker, null);
        editText = (MaterialEditText) vr.findViewById(R.id.user_picker);
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

        editText.setIconRight(R.drawable.ic_account_circle_grey);

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
            final String groupId;
            if (data.getParams() != null && data.getParams().get("restrictWithGroup") != null)
            {
                // Is Active
                String status = (String) ((HashMap) data.getParams().get("restrictWithGroup")).get("status");
                if ("active".equals(status))
                {
                    groupId = (String) ((HashMap) data.getParams().get("restrictWithGroup")).get("id");
                }
                else
                {
                    groupId = null;
                }
            }
            else
            {
                groupId = null;
            }

            // Retrieve params if available
            editionView.findViewById(R.id.button_container).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    editText.setError(null);
                    UserPickerFragment.with(getFragment().getActivity()).fragmentTag(getFragment().getTag())
                            .fieldId(data.getId()).restrictGroup(groupId).singleChoice(true)
                            .mode(ListingModeFragment.MODE_PICK).display();
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
        return editionValue;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VALIDATION & ERROR
    // ///////////////////////////////////////////////////////////////////////////
    public void showError()
    {
        if (isValid()) { return; }
        editText.setError(String.format(getString(R.string.form_error_message_required), data.getName()));
    }
}

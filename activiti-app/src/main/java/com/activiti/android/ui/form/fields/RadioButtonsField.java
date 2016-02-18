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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.activiti.client.api.model.editor.form.OptionRepresentation;
import com.activiti.client.api.model.editor.form.RestFieldRepresentation;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by jpascal on 28/03/2015.
 */
public class RadioButtonsField extends BaseField
{
    private MaterialEditText tv;

    protected RadioGroup radioGroup;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public RadioButtonsField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // READ
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * Used this time to only manage values. It returns no values
     */
    public String getHumanReadableReadValue()
    {
        if (originalValue == null) { return getString(R.string.form_message_empty); }
        return originalValue.toString();
    }

    public View setupdReadView()
    {
        View vr = inflater.inflate(R.layout.form_read_row, null);
        HolderUtils.configure(vr, data.getName(), getHumanReadableReadValue(), -1);
        vr.setFocusable(false);

        readView = vr;

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VALUES
    // ///////////////////////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    protected void updateEditionView()
    {
        // Retrieve info
        if (data instanceof RestFieldRepresentation && ((RestFieldRepresentation) data).getEndpoint() != null)
        {
            List<OptionRepresentation> options = new ArrayList<>(1);
            OptionRepresentation rep = new OptionRepresentation("-1", "Loading...");
            options.add(rep);
            refreshRadioButtons(options);
        }
        else
        {
            refreshRadioButtons(data.getOptions());
        }
    }

    @Override
    public View setupEditionView(Object value)
    {
        if (value != null)
        {
            editionValue = value;
        }

        ViewGroup vr = (ViewGroup) inflater.inflate(R.layout.form_radiobuttons, null);
        tv = (MaterialEditText) vr.findViewById(R.id.radio_header);
        tv.setFocusable(false);
        tv.setHint(getLabelText(data.getName()));

        radioGroup = (RadioGroup) vr.findViewById(R.id.form_radio_group);
        radioGroup.setId(data.getName().length());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                tv.setError(null);
                getFormManager().evaluateViews();
            }
        });

        // Retrieve info
        if (data instanceof RestFieldRepresentation && ((RestFieldRepresentation) data).getEndpoint() != null)
        {
            List<OptionRepresentation> options = new ArrayList<>(1);
            OptionRepresentation rep = new OptionRepresentation("-1", "Loading...");
            options.add(rep);
            refreshRadioButtons(options);
        }
        else
        {
            refreshRadioButtons(data.getOptions());
        }

        editionView = vr;

        return vr;
    }

    private void refreshRadioButtons(List<OptionRepresentation> options)
    {
        if (radioGroup == null) { return; }
        radioGroup.removeAllViews();

        for (OptionRepresentation item : options)
        {
            if (item == null)
            {
                continue;
            }
            RadioButton button = createRow(item);
            radioGroup.addView(button);
            String name = TextUtils.isEmpty(item.getName()) ? "" : item.getName();
            if (editionValue instanceof String)
            {
                button.setChecked(name.equals(editionValue));
            }
            else if (editionValue instanceof OptionRepresentation)
            {
                button.setChecked(name.equals(((OptionRepresentation) editionValue).getName()));
            }
        }
    }

    @Override
    public void setFragment(AlfrescoFragment fr)
    {
        if (data instanceof RestFieldRepresentation
                && ((RestFieldRepresentation) data).getEndpoint() == null) { return; }

        super.setFragment(fr);
        getFragment().getAPI().getTaskService().getFormFieldValues(getFormManager().getTaskId(), data.getId(),
                new Callback<List<OptionRepresentation>>()
                {
                    @Override
                    public void onResponse(Call<List<OptionRepresentation>> call,
                            Response<List<OptionRepresentation>> response)
                    {
                        if (!response.isSuccess())
                        {
                            onFailure(call, new Exception(response.message()));
                            return;
                        }
                        refreshRadioButtons(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<OptionRepresentation>> call, Throwable t)
                    {

                    }
                });
    }

    // ///////////////////////////////////////////////////////////////////////////
    // OUTPUT VALUE
    // ///////////////////////////////////////////////////////////////////////////
    public Object getOutputValue()
    {
        if (editionValue == null) { return null; }
        if (editionValue instanceof OptionRepresentation) { return ((OptionRepresentation) editionValue).getName(); }
        return editionValue;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIST MANAGEMENT
    // ///////////////////////////////////////////////////////////////////////////
    private RadioButton createRow(final OptionRepresentation option)
    {
        RadioButton radioButtonView = (RadioButton) inflater.inflate(R.layout.form_radio_button, null);
        radioButtonView.setText(option.getName());
        radioButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editionValue = option;
            }
        });

        return radioButtonView;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VALIDATION & ERROR
    // ///////////////////////////////////////////////////////////////////////////
    public void showError()
    {
        if (isValid()) { return; }
        tv.setError(String.format(getString(R.string.form_error_message_required), data.getName()));
    }
}

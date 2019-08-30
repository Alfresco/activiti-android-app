/*
 *  Copyright (C) 2005-2016 Alfresco Software Limited.
 *
 *  This file is part of Alfresco Activiti Mobile for Android.
 *
 *  Alfresco Activiti Mobile for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Alfresco Activiti Mobile for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package com.activiti.android.ui.form.fields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.activiti.client.api.model.editor.form.OptionRepresentation;
import com.activiti.client.api.model.editor.form.RestFieldRepresentation;

/**
 * Created by jpascal on 28/03/2015.
 */
public class DropDownField extends BaseField
{
    private Map<String, Integer> optionsIndex;

    protected int selectedPosition = 0;

    protected TextView title;

    protected DropDownAdapter adapter;

    protected Spinner spinner;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public DropDownField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // READ
    // ///////////////////////////////////////////////////////////////////////////
    public String getHumanReadableReadValue()
    {
        if (originalValue == null) { return getString(R.string.form_message_empty); }
        return originalValue.toString();
    }

    public View setupReadView()
    {
        View vr = inflater.inflate(R.layout.form_read_row, null);
        HolderUtils.configure(vr, data.getName(), getHumanReadableReadValue(), -1);

        readView = vr;

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    protected void updateEditionView()
    {
        if (editionValue != null && editionView != null)
        {
            if (editionValue instanceof OptionRepresentation)
            {
                ((Spinner) editionView.findViewById(R.id.spinner))
                        .setSelection(optionsIndex.get(((OptionRepresentation) editionValue).getName()));
            }
        }
    }

    protected void updateReadView() {
        if (originalValue != null && readView != null)
        {
            if (originalValue instanceof OptionRepresentation)
            {
                ((Spinner) readView.findViewById(R.id.spinner))
                        .setSelection(optionsIndex.get(((OptionRepresentation) originalValue).getName()));
            }
        }
    }

    public View setupEditionView(Object value)
    {
        editionValue = value;

        View root = inflater.inflate(R.layout.form_dropdown, null);
        title = (TextView) root.findViewById(R.id.spinner_title);
        title.setText(getLabelText(data.getName()));

        spinner = (Spinner) root.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View view, int position, long id)
            {
                editionValue = adapter.getItemAtPosition(position);
                selectedPosition = position;
                title.setError(null);
                getFormManager().evaluateViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // Retrieve info
        if (data instanceof RestFieldRepresentation && ((RestFieldRepresentation) data).getEndpoint() != null)
        {
            List<OptionRepresentation> options = new ArrayList<>(1);
            OptionRepresentation rep = new OptionRepresentation("-1", "Loading...");
            options.add(rep);
            refreshAdapter(options);
        }
        else
        {
            refreshAdapter(data.getOptions());
        }

        if (value instanceof String)
        {
            spinner.setSelection((value != null) ? (optionsIndex.get(value) != null) ? optionsIndex.get(value) : 0 : 0);
            selectedPosition = spinner.getSelectedItemPosition();
        }

        editionView = root;

        return root;
    }

    private void refreshAdapter(List<OptionRepresentation> options)
    {
        if (spinner == null) { return; }

        optionsIndex = new HashMap<>(options.size());
        for (int i = 0; i < options.size(); i++)
        {
            optionsIndex.put(options.get(i).getName(), i);
        }

        if (adapter == null)
        {
            adapter = new DropDownAdapter(getContext(), R.layout.row_single_line_dropdown, options);
            spinner.setAdapter(adapter);
        }
        else
        {
            adapter.clear();
            adapter.addAll(options);
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
                        if (!response.isSuccessful())
                        {
                            onFailure(call, new Exception(response.message()));
                            return;
                        }

                        if (TextUtils.isEmpty(data.getPlaceholder()))
                        {
                            response.body().add(0, new OptionRepresentation("-1", getString(R.string.dropdown_select)));
                        }
                        else
                        {
                            response.body().add(0, new OptionRepresentation("-1", data.getPlaceholder()));
                        }
                        refreshAdapter(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<OptionRepresentation>> call, Throwable error)
                    {
                    }
                });
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ERROR
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public boolean isValid()
    {
        if (selectedPosition == 0 && data.isRequired()) { return false; }
        return super.isValid();
    }

    public void showError()
    {
        if (isValid()) { return; }
        title.setError(getString(R.string.error_field_required));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // OUTPUT VALUE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object getOutputValue()
    {
        if (isReadMode) {
            return originalValue;
        }
        if (editionValue instanceof OptionRepresentation)
        {
            // Return null if the optionRepresentation is the empty value
            if (selectedPosition == 0)
            {
                return null;
            }
            else
            {
                return editionValue;
            }
        }
        return super.getOutputValue();
    }
}

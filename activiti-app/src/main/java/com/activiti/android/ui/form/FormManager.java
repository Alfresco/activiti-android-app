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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.activiti.android.app.R;
import com.activiti.android.sdk.model.runtime.AppVersion;
import com.activiti.android.ui.form.fields.BaseField;
import com.activiti.android.ui.form.fields.TabField;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.client.api.model.editor.form.ContainerRepresentation;
import com.activiti.client.api.model.editor.form.DynamicTableRepresentation;
import com.activiti.client.api.model.editor.form.FormDefinitionRepresentation;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.activiti.client.api.model.editor.form.FormFieldTypes;
import com.activiti.client.api.model.editor.form.FormOutcomeRepresentation;
import com.activiti.client.api.model.editor.form.FormTabRepresentation;
import com.activiti.client.api.model.editor.form.RestFieldRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by jpascal on 28/03/2015.
 */
public class FormManager
{
    protected ViewGroup vRoot;

    protected AlfrescoFragment fr;

    protected WeakReference<FragmentActivity> activity;

    private Map<String, BaseField> fieldsIndex;

    private Map<String, ViewGroup> tabHookViewIndex;

    private LinkedHashMap<String, TabField> formTabFieldIndex;

    private Map<String, FormFieldRepresentation> formFieldIndex = new HashMap<>();

    private ArrayList<BaseField> fieldsOrderIndex = new ArrayList<>();

    private ArrayList<BaseField> mandatoryFields = new ArrayList<>(0);

    private Map<String, View> outcomeIndex;

    private BaseField currentPickerField;

    private FormDefinitionRepresentation data;

    private AppVersion version;

    private TabLayout tabLayout = null;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public FormManager(AlfrescoFragment fr, ViewGroup vRoot, FormDefinitionRepresentation data, AppVersion version)
    {
        this.fr = fr;
        this.vRoot = vRoot;
        this.activity = new WeakReference<>(fr.getActivity());
        this.data = data;
        this.version = version;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public void prepare(FormDefinitionRepresentation data)
    {
        this.data = data;
        vRoot.removeAllViews();
        generateForm(LayoutInflater.from(getActivity()), false);
    }

    public void displayReadForm()
    {
        vRoot.removeAllViews();
        generateForm(LayoutInflater.from(getActivity()), false);
    }

    public void displayEditForm()
    {
        vRoot.removeAllViews();
        generateForm(LayoutInflater.from(getActivity()), true);
    }

    public Map<String, View> getOutComesView()
    {
        return (outcomeIndex == null) ? new HashMap<String, View>(0) : outcomeIndex;
    }

    public HashMap<String, Object> getValues()
    {
        HashMap<String, Object> props = new HashMap<>(fieldsIndex.size());
        for (Map.Entry<String, BaseField> entry : fieldsIndex.entrySet())
        {
            if (entry.getValue().hasEditionValueChanged())
            {
                props.put(entry.getKey(), entry.getValue().getOutputValue());
            }
        }
        return props;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    public boolean hasOutcome()
    {
        return data.getOutcomes() != null && data.getOutcomes().size() > 0;
    }

    public boolean checkValidation()
    {
        if (mandatoryFields.isEmpty()) { return true; }
        boolean isValid = true;
        boolean requiredFocus = false;
        for (BaseField field : mandatoryFields)
        {
            if (!field.isValid())
            {
                field.showError();
                isValid = false;

                // Focus the first view that is invalid
                if (!requiredFocus)
                {
                    field.getEditionView().requestFocus();
                    requiredFocus = true;
                }
            }
        }
        return isValid;
    }

    public void setPropertyValue(String propertyId, Object object)
    {
        fieldsIndex.get(propertyId).setEditionValue(object);
    }

    public BaseField getField(String propertyId)
    {
        return fieldsIndex.get(propertyId);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // GENERATOR
    // ///////////////////////////////////////////////////////////////////////////
    protected void generateForm(LayoutInflater li, boolean isEdition)
    {
        boolean hasTab = false;

        if (!version.is120OrAbove())
        {
            generateForm11(li, isEdition);
            return;
        }

        ViewGroup rootView;

        // Generate Tabs
        if (data.getTabs() != null && !data.getTabs().isEmpty())
        {
            rootView = (ViewGroup) li.inflate(R.layout.form_root_tabs, null);

            hasTab = true;
            tabLayout = rootView.findViewById(R.id.task_form_tabs_container);
            tabLayout.setVisibility(View.VISIBLE);

            tabHookViewIndex = new HashMap<>(data.getTabs().size());
            formTabFieldIndex = new LinkedHashMap<>(data.getTabs().size());

            int i = 0;
            for (FormTabRepresentation tabRepresentation : data.getTabs())
            {
                TabLayout.Tab tab = tabLayout.newTab().setText(tabRepresentation.getTitle())
                        .setTag(tabRepresentation.getId());
                tabLayout.addTab(tab);
                ViewGroup tabHookView = (ViewGroup) li.inflate(R.layout.form_tab_children, null);
                tabHookViewIndex.put(tabRepresentation.getId(), tabHookView);
                tabHookView.setVisibility(View.GONE);
                rootView.addView(tabHookView);

                formTabFieldIndex.put(tabRepresentation.getId(), new TabField(tab, tabRepresentation, i, tabHookView));

                i++;
            }

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
            {
                @Override
                public void onTabSelected(TabLayout.Tab tab)
                {
                    formTabFieldIndex.get(tab.getTag()).getHookView().setVisibility(View.VISIBLE);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab)
                {
                    formTabFieldIndex.get(tab.getTag()).getHookView().setVisibility(View.GONE);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab)
                {
                    formTabFieldIndex.get(tab.getTag()).getHookView().setVisibility(View.VISIBLE);
                }
            });

            tabLayout.getTabAt(0).select();

        }
        else
        {
            rootView = (ViewGroup) li.inflate(R.layout.form_root, null);
        }

        ViewGroup hookView = rootView;
        fieldsIndex = new HashMap<>(data.getFields().size());

        // Generate Fields
        ViewGroup groupRoot = null;
        BaseField field;
        for (FormFieldRepresentation fieldData : data.getFields())
        {
            if (FormFieldTypes.GROUP.equals(fieldData.getType()))
            {
                if (fieldData instanceof ContainerRepresentation)
                {
                    // Create groupView
                    groupRoot = (ViewGroup) li.inflate(R.layout.form_fields_group, null);

                    if (hasTab && fieldData.getTab() != null)
                    {
                        hookView = tabHookViewIndex.get(fieldData.getTab())
                                .findViewById(R.id.tab_group_container);
                    }
                    else
                    {
                        hookView = groupRoot.findViewById(R.id.form_group_container);
                    }

                    BaseField baseField = generateField(fieldData, hookView, isEdition);
                    formFieldIndex.put(fieldData.getId(), fieldData);
                    fieldsOrderIndex.add(baseField);
                    fieldsIndex.put(fieldData.getId(), baseField);
                    Map<String, List<FormFieldRepresentation>> fields = ((ContainerRepresentation) fieldData).getFields();

                    for (Map.Entry<String, List<FormFieldRepresentation>> entry : fields.entrySet())
                    {
                        for (FormFieldRepresentation representation : entry.getValue())
                        {
                            formFieldIndex.put(representation.getId(), representation);
                            field = generateField(representation, (ViewGroup) (isEdition ? baseField.getEditionView() : baseField.getReadView()), isEdition);
                            fieldsOrderIndex.add(field);
                            fieldsIndex.put(representation.getId(), field);

                            // Mark All fields in edition mode
                            if (isEdition)
                            {
                                // Mark required Field
                                if (representation.isRequired())
                                {
                                    mandatoryFields.add(field);
                                }

                                if (representation instanceof RestFieldRepresentation)
                                {
                                    field.setFragment(fr);
                                }
                            }

                            // If requires fragment for pickers.
                            if (field.isPickerRequired())
                            {
                                field.setFragment(fr);
                            }
                        }
                    }

                    rootView.addView(groupRoot);
                    groupRoot = null;
                }
            }
            else if (FormFieldTypes.CONTAINER.equals(fieldData.getType()))
            {
                // Create groupView
                if (groupRoot == null)
                {
                    groupRoot = (ViewGroup) li.inflate(R.layout.form_fields_group, null);
                }

                if (hasTab && fieldData.getTab() != null)
                {
                    hookView = tabHookViewIndex.get(fieldData.getTab())
                            .findViewById(R.id.tab_group_container);
                }
                else
                {
                    hookView = groupRoot.findViewById(R.id.form_group_container);
                }

                Map<String, List<FormFieldRepresentation>> fields = ((ContainerRepresentation) fieldData).getFields();
                for (Map.Entry<String, List<FormFieldRepresentation>> entry : fields.entrySet())
                {
                    for (FormFieldRepresentation representation : entry.getValue())
                    {
                        formFieldIndex.put(representation.getId(), representation);
                        field = generateField(representation, hookView, isEdition);
                        fieldsOrderIndex.add(field);
                        fieldsIndex.put(representation.getId(), field);

                        // Mark All fields in edition mode
                        if (isEdition)
                        {
                            // Mark required Field
                            if (representation.isRequired())
                            {
                                mandatoryFields.add(field);
                            }

                            if (representation instanceof RestFieldRepresentation)
                            {
                                field.setFragment(fr);
                            }
                        }

                        // If requires fragment for pickers.
                        if (field.isPickerRequired())
                        {
                            field.setFragment(fr);
                        }
                    }
                }
            }
            else if (FormFieldTypes.DYNAMIC_TABLE.equals(fieldData.getType())
                    || (FormFieldTypes.READONLY.equals(fieldData.getType())
                            && fieldData.getParams().get("field") != null && (FormFieldTypes.DYNAMIC_TABLE
                                    .equals(((HashMap) fieldData.getParams().get("field")).get("type")))))
            {
                // Create groupView
                if (groupRoot == null)
                {
                    groupRoot = (ViewGroup) li.inflate(R.layout.form_fields_group, null);
                }

                if (hasTab && fieldData.getTab() != null)
                {
                    hookView = tabHookViewIndex.get(fieldData.getTab())
                            .findViewById(R.id.tab_group_container);
                }
                else
                {
                    hookView = groupRoot.findViewById(R.id.form_group_container);
                }

                if (fieldData instanceof DynamicTableRepresentation)
                {
                    field = generateField(fieldData, hookView, isEdition);
                    fieldsOrderIndex.add(field);
                    continue;
                }
            }
        }

        // Now time to evaluate everyone
        evaluateViews();

        // Add Container to root ?
        if (groupRoot != null)
        {
            rootView.addView(groupRoot);
        }

        vRoot.addView(rootView);

        // OUTCOME
        if (!isEdition) { return; }
        View vr;
        if (data.getOutcomes() == null || data.getOutcomes().size() == 0)
        {
            outcomeIndex = new HashMap<>(1);
            vr = generateOutcome(rootView, getActivity().getString(R.string.form_default_outcome_complete), li);
            outcomeIndex.put(getActivity().getString(R.string.form_default_outcome_complete), vr);
        }
        else
        {
            outcomeIndex = new HashMap<>(data.getOutcomes().size());
            for (FormOutcomeRepresentation outcomeData : data.getOutcomes())
            {
                vr = generateOutcome(rootView, outcomeData.getName(), li);
                outcomeIndex.put(outcomeData.getName(), vr);
            }
        }
    }

    /**
     * Works for 1.1 Form Definition
     *
     * @param li
     * @param isEdition
     */
    protected void generateForm11(LayoutInflater li, boolean isEdition)
    {
        ViewGroup rootView = (ViewGroup) li.inflate(R.layout.form_root, null);
        ViewGroup hookView = rootView;
        fieldsIndex = new HashMap<>(data.getFields().size());

        boolean createGroup = true;

        ViewGroup groupRoot = null;
        BaseField field;
        for (FormFieldRepresentation fieldData : data.getFields())
        {
            if (FormFieldTypes.GROUP.equals(fieldData.getType()))
            {
                if (groupRoot != null)
                {
                    rootView.addView(groupRoot);
                }
                // Header
                field = generateField(fieldData, rootView, isEdition);
                createGroup = true;
                groupRoot = null;
            }
            else
            {
                if (createGroup)
                {
                    // Create groupView
                    groupRoot = (ViewGroup) li.inflate(R.layout.form_fields_group, null);
                    hookView = groupRoot.findViewById(R.id.form_group_container);
                    createGroup = false;
                }

                // Normal Field
                field = generateField(fieldData, hookView, isEdition);
            }

            fieldsOrderIndex.add(field);
            fieldsIndex.put(fieldData.getId(), field);

            // Mark All fields in edition mode
            if (isEdition)
            {
                // Mark required Field
                if (fieldData.isRequired())
                {
                    mandatoryFields.add(field);
                }

                if (fieldData instanceof RestFieldRepresentation)
                {
                    field.setFragment(fr);
                }
            }

            // If requires fragment for pickers.
            if (field.isPickerRequired())
            {
                field.setFragment(fr);
            }
        }

        // Now time to evaluate everyone
        evaluateViews();

        // Add Container to root ?
        if (groupRoot != null)
        {
            rootView.addView(groupRoot);
        }

        vRoot.addView(rootView);

        // OUTCOME
        if (!isEdition) { return; }
        View vr;
        if (data.getOutcomes() == null || data.getOutcomes().size() == 0)
        {
            outcomeIndex = new HashMap<>(1);
            vr = generateOutcome(rootView, getActivity().getString(R.string.form_default_outcome_complete), li);
            outcomeIndex.put(getActivity().getString(R.string.form_default_outcome_complete), vr);
        }
        else
        {
            outcomeIndex = new HashMap<>(data.getOutcomes().size());
            for (FormOutcomeRepresentation outcomeData : data.getOutcomes())
            {
                vr = generateOutcome(rootView, outcomeData.getName(), li);
                outcomeIndex.put(outcomeData.getName(), vr);
            }
        }
    }

    private BaseField generateField(FormFieldRepresentation data, ViewGroup hookView, boolean isEdition)
    {
        boolean tmpIsEdition = isEdition;
        // First we prepare the field
        String dataType = (tmpIsEdition) ? data.getType() : data.getReadOnlyFieldType();

        // ReadOnly is a specific case
        // We don't use type but the type defined by params.
        // Works like a pointer to a specific field
        if (FormFieldTypes.READONLY.equals(dataType))
        {
            dataType = data.getReadOnlyFieldType();
            tmpIsEdition = false;
        }

        BaseField field = FieldTypeFactory.createField(getActivity(), this, dataType, data, !tmpIsEdition);

        // Then we create the view
        View fieldView = (tmpIsEdition) ? field.setupEditionView(data.getValue()) : field.setupReadView();

        // If a view has been generated we kept it.
        if (fieldView != null)
        {
            hookView.addView(fieldView);
        }

        return field;
    }

    private View generateOutcome(ViewGroup hookView, String name, LayoutInflater li)
    {
        View vr = li.inflate(R.layout.form_outcome, null);
        ((Button) vr.findViewById(R.id.outcome_button)).setText(name);
        hookView.addView(vr);
        return vr;
    }

    public void evaluateViews()
    {
        FormEvaluateExpression formEvaluateExpression = new FormEvaluateExpression(getValues(), formFieldIndex);

        for (BaseField field : fieldsOrderIndex)
        {
            if (field.getData().getVisibilityCondition() != null)
            {
                field.evaluateVisibility(
                        formEvaluateExpression.evaluateExpression(field.getData().getVisibilityCondition()));
            }
            else
            {
                field.evaluateVisibility();
            }
        }

        if (tabLayout == null) { return; }

        Map<String, TabLayout.Tab> tabLaIndex = new HashMap<>(tabLayout.getTabCount());

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            tabLaIndex.put((String) tabLayout.getTabAt(i).getTag(), tabLayout.getTabAt(i));
        }

        for (Map.Entry<String, TabField> tabField : formTabFieldIndex.entrySet())
        {
            if (tabField.getValue().getData().getVisibilityCondition() != null)
            {
                TabField tabInfo = tabField.getValue();
                boolean isVisible = formEvaluateExpression
                        .evaluateExpression(tabInfo.getData().getVisibilityCondition());
                if (isVisible && !tabLaIndex.containsKey(tabInfo.getId()))
                {
                    tabLayout.addTab(tabInfo.getTab(tabLayout), tabInfo.getOriginalIndex());
                }
                else if (!isVisible && tabLaIndex.containsKey(tabInfo.getId()))
                {
                    tabLayout.removeTabAt(tabLaIndex.get(tabInfo.getId()).getPosition());
                }
            }
        }
    }

    public void refreshViews()
    {
        for (BaseField field : fieldsOrderIndex)
        {
            field.refreshView();
        }
    }

    /**
     * Unsupported + Required field
     */
    public void abort()
    {
        getActivity().getSupportFragmentManager().popBackStack();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title(R.string.form_message_unsupported_title).cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dialog.dismiss();
                    }
                }).content(Html.fromHtml(getActivity().getString(R.string.form_message_unsupported_description)))
                .positiveText(R.string.ok).callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                    }
                });
        builder.show();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    public int getVersionNumber()
    {
        return fr.getVersionNumber();
    }

    public FragmentActivity getActivity()
    {
        return activity.get();
    }

    public void setCurrentPickerField(BaseField field)
    {
        currentPickerField = field;
    }

    public BaseField getCurrentPickerField()
    {
        return currentPickerField;
    }

    public String getTaskId()
    {
        return data.getTaskId();
    }

}

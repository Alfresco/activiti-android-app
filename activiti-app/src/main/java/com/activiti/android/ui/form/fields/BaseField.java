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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.client.api.model.editor.form.ConditionRepresentation;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.activiti.client.api.model.editor.form.OptionRepresentation;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by jpascal on 28/03/2015.
 */
public abstract class BaseField
{
    protected WeakReference<FormManager> formManagerRef;

    protected WeakReference<AlfrescoFragment> fragmentRef;

    protected WeakReference<Context> contextRef;

    protected LayoutInflater inflater;

    protected FormFieldRepresentation data;

    protected Object originalValue;

    protected Object editionValue;

    protected View readView;

    protected View editionView;

    protected boolean isReadMode;

    protected boolean isVisible = true;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public BaseField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        this.contextRef = new WeakReference<>(context);
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.originalValue = data.getValue();
        this.formManagerRef = new WeakReference<>(manager);
        this.isReadMode = isReadMode;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // READ
    // ///////////////////////////////////////////////////////////////////////////
    public FormFieldRepresentation getData()
    {
        return data;
    }

    public String getHumanReadableReadValue()
    {
        if (originalValue == null) { return getString(R.string.form_message_empty); }
        return originalValue.toString();
    }

    public Object getReadValue()
    {
        return originalValue;
    }

    public View setupdReadView()
    {
        readView = inflater.inflate(R.layout.form_read_row, null);
        HolderUtils.configure(readView, data.getName(), getHumanReadableReadValue(), -1);
        readView.setFocusable(false);

        return readView;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VALUE
    // ///////////////////////////////////////////////////////////////////////////
    public String getHumanReadableEditionValue()
    {
        if (editionValue == null) { return null; }
        return editionValue.toString();
    }

    public Object getEditionValue()
    {
        return editionValue;
    }

    public void setEditionValue(Object object)
    {
        editionValue = object;
        updateEditionView();
    }

    public boolean hasEditionValueChanged()
    {
        if (originalValue == null && getEditionValue() == null)
        {
            return false;
        }
        else if (originalValue != null && originalValue.equals(getEditionValue()))
        {
            // Value has not changed
            // Special case where previous value already present
            return true;
        }
        else
        {
            return true;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    public View getEditionView()
    {
        return editionView;
    }

    public View getReadView() {
        return readView;
    }

    protected void updateEditionView()
    {
        if (getHumanReadableEditionValue() != null)
        {
            ((MaterialEditText) editionView).setText(getHumanReadableEditionValue());
        }
    }

    public View setupEditionView(Object value)
    {
        editionValue = value;

        View vr = inflater.inflate(R.layout.form_edit_text, null);
        ((MaterialEditText) vr).setText(getHumanReadableEditionValue());

        // Asterix if required
        ((MaterialEditText) vr).setFloatingLabelText(getLabelText(data.getName()));

        if (TextUtils.isEmpty(data.getPlaceholder()))
        {
            ((MaterialEditText) vr).setHint(getLabelText(data.getName()));
        }
        else
        {
            ((MaterialEditText) vr).setFloatingLabelAlwaysShown(true);
            ((MaterialEditText) vr).setHint(data.getPlaceholder());
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

    // ///////////////////////////////////////////////////////////////////////////
    // VALIDATION & ERROR
    // ///////////////////////////////////////////////////////////////////////////
    public boolean isValid()
    {
        if (!data.isRequired()) { return true; }
        if (data.isRequired() && getEditionValue() != null) { return true; }
        return false;
    }

    public void showError()
    {
        if (isValid()) { return; }
        ((MaterialEditText) editionView).setError(String.format(getString(R.string.form_error_message_required),
                data.getName()));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PICKERS
    // ///////////////////////////////////////////////////////////////////////////
    public boolean isPickerRequired()
    {
        return false;
    }

    public void setFragment(AlfrescoFragment fr)
    {
        this.fragmentRef = new WeakReference<>(fr);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // OUTPUT VALUE
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Generally the String representation of the editionValue
     *
     * @return
     */
    public Object getOutputValue()
    {
        // if (isReadMode) { return getReadValue(); }
        return isVisible ? getEditionValue() : null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // REFRESH
    // ///////////////////////////////////////////////////////////////////////////
    public void refreshEditionView()
    {
        // We reaffect the same value to update the view with the latest edition
        // value
        setEditionValue(getEditionValue());
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VISIBILITY CONDITION
    // ///////////////////////////////////////////////////////////////////////////
    private boolean evaluateVisibility(ConditionRepresentation condition)
    {
        boolean isVisibility = false;

        // if condition is 'empty' enable visibility
        if (condition == null || TextUtils.isEmpty(condition.getLeftFormFieldId())) { return true; }

        // Collect lvalue and rvalue
        BaseField leftFormField = getFormManager().getField(condition.getLeftFormFieldId());
        if (leftFormField == null) { return false; }

        Object leftValue;

        if (leftFormField instanceof DropDownField)
        {
            if (leftFormField.getOutputValue() == null) { return false; }
            leftValue = leftFormField.getOutputValue();
        }
        else
        {
            leftValue = leftFormField.getOutputValue();
        }

        Object rightValue = condition.getRightValue();
        // Operator applies to form field
        if (rightValue == null || ((rightValue instanceof String) && TextUtils.isEmpty((String) rightValue)))
        {
            if (!TextUtils.isEmpty(condition.getRightFormFieldId()))
            {
                BaseField rightFormField = getFormManager().getField(condition.getRightFormFieldId());
                if (rightFormField == null) { return false; }
                rightValue = rightFormField.getOutputValue();
            }
            else
            {
                return false;
            }
        }

        // Actual evaluation based on the configured operator
        String operator = condition.getOperator();
        if (operator != null)
        {
            if (operator.equals("=="))
            {
                isVisibility = evaluateIsEqual(leftValue, rightValue);
            }
            else if (operator.equals("!="))
            {
                isVisibility = !evaluateIsEqual(leftValue, rightValue);
            }
            else
            {
                isVisibility = evaluateComparator(leftValue, rightValue, operator);
            }
        }
        return isVisibility;
    }

    private boolean evaluateIsEqual(Object leftValue, Object rightValue)
    {
        if (leftValue == null && rightValue == null) { return true; }
        if (leftValue == null && rightValue != null) { return false; }
        if (leftValue instanceof OptionRepresentation) { return ((OptionRepresentation) leftValue).getName().equals(
                rightValue); }
        return leftValue.equals(rightValue);
    }

    private boolean evaluateComparator(Object leftValue, Object rightValue, String operator)
    {
        if (TextUtils.isEmpty(operator)) { return false; }
        if (leftValue == null && rightValue != null) { return false; }
        if (leftValue != null && rightValue == null) { return false; }

        boolean isNumeric = leftValue instanceof Double || rightValue instanceof Double;
        boolean isDate = leftValue instanceof Date || leftValue instanceof GregorianCalendar;
        boolean isVisibility = true;

        if (operator.equals("<"))
        {
            if (isNumeric)
            {
                isVisibility = (Double) leftValue < (Double) rightValue;
            }
            else if (isDate)
            {
                isVisibility = ((Date) leftValue).before((Date) rightValue);
            }
        }
        else if (operator.equals("<="))
        {
            if (isNumeric)
            {
                isVisibility = (Double) leftValue <= (Double) rightValue;
            }
            else if (isDate)
            {
                isVisibility = ((Date) leftValue).before((Date) rightValue)
                        || ((Date) leftValue).getTime() == ((Date) rightValue).getTime();
                ;
            }
        }
        else if (operator.equals(">"))
        {
            if (isNumeric)
            {
                isVisibility = (Double) leftValue > (Double) rightValue;
            }
            else if (isDate)
            {
                isVisibility = ((Date) leftValue).after((Date) rightValue);
            }
        }
        else if (operator.equals(">="))
        {
            if (isNumeric)
            {
                isVisibility = (Double) leftValue >= (Double) rightValue;
            }
            else if (isDate)
            {
                isVisibility = ((Date) leftValue).after((Date) rightValue)
                        || ((Date) leftValue).getTime() == ((Date) rightValue).getTime();
            }
        }

        return isVisibility;
    }

    public void evaluateVisibility(boolean isVisible)
    {
        // Time to set Visibility
        if (isVisible)
        {
            if (isReadMode)
            {
                readView.setVisibility(View.VISIBLE);
            }
            else
            {
                editionView.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            if (isReadMode)
            {
                readView.setVisibility(View.GONE);
            }
            else
            {
                editionView.setVisibility(View.GONE);
            }
        }
    }

    public void evaluateVisibility()
    {
        if (data.getVisibilityCondition() != null)
        {
            // There are multiple conditions, chained to each other,
            // hence while loop until all are found and evaluated
            ArrayList<Boolean> conditionResults = new ArrayList<>();
            ArrayList<String> operators = new ArrayList<>();
            ConditionRepresentation condition = data.getVisibilityCondition();
            while (condition != null)
            {
                conditionResults.add(evaluateVisibility(condition));
                if (!TextUtils.isEmpty(condition.getNextConditionOperator()))
                {
                    operators.add(condition.getNextConditionOperator());
                }
                condition = condition.getNextCondition();
            }

            // Evaluation can now be done, using all the evaluated results
            Boolean evaluationresult = false;
            if (conditionResults.size() > 1)
            {
                for (int i = 0; i < operators.size(); i++)
                {
                    if (operators.get(i).equals("and"))
                    {
                        evaluationresult = (i > 0) ? evaluationresult : conditionResults.get(i)
                                && conditionResults.get(i + 1);
                    }
                    else if (operators.get(i).equals("and-not"))
                    {
                        evaluationresult = (i > 0) ? evaluationresult : conditionResults.get(i)
                                && !conditionResults.get(i + 1);
                    }
                    else if (operators.get(i).equals("or"))
                    {
                        evaluationresult = (i > 0) ? evaluationresult : conditionResults.get(i)
                                || conditionResults.get(i + 1);
                    }
                    else if (operators.get(i).equals("or-not"))
                    {
                        evaluationresult = (i > 0) ? evaluationresult : conditionResults.get(i)
                                || !conditionResults.get(i + 1);
                    }
                }
            }
            else
            {
                evaluationresult = conditionResults.get(0);
            }
            isVisible = evaluationresult;
        }
        else
        {
            isVisible = true;
        }

        // Time to set Visibility
        if (isVisible)
        {
            if (isReadMode)
            {
                readView.setVisibility(View.VISIBLE);
            }
            else
            {
                editionView.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            if (isReadMode)
            {
                readView.setVisibility(View.GONE);
            }
            else
            {
                editionView.setVisibility(View.GONE);
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    protected AlfrescoFragment getFragment()
    {
        return (fragmentRef != null) ? fragmentRef.get() : null;
    }

    protected Context getContext()
    {
        return (contextRef != null) ? contextRef.get() : null;
    }

    protected FormManager getFormManager()
    {
        return (formManagerRef != null) ? formManagerRef.get() : null;
    }

    protected String getString(int stringId)
    {
        return (getContext() != null) ? getContext().getString(stringId) : null;
    }

    protected String getLabelText(String value)
    {
        if (TextUtils.isEmpty(value)) { return value; }
        return data.isRequired() ? value + " *" : value;
    }
}

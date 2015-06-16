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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.client.api.model.editor.form.AmountFieldRepresentation;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by jpascal on 28/03/2015.
 */
public class AmountField extends BaseField
{
    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public AmountField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // READ
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public View setupdReadView()
    {
        readView = inflater.inflate(R.layout.form_read_row, null);
        HolderUtils.configure(readView, getCurrencyLabel(), getHumanReadableReadValue(), -1);
        readView.setFocusable(false);

        return readView;
    }

    public String getHumanReadableReadValue()
    {
        if (originalValue == null) { return getString(R.string.form_message_empty); }

        DecimalFormat df;
        if ((((AmountFieldRepresentation) data).isEnableFractions()))
        {
            df = new DecimalFormat("#.00", DecimalFormatSymbols.getInstance());
        }
        else
        {
            df = new DecimalFormat("#", DecimalFormatSymbols.getInstance());
        }
        df.setMaximumFractionDigits(25);

        if (originalValue instanceof Double) { return df.format((Double) originalValue); }
        if (originalValue instanceof String) { return df.format((Double) Double.parseDouble((String) originalValue)); }
        return originalValue.toString();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VALUE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object getEditionValue()
    {
        if (editionView instanceof MaterialEditText)
        {
            String value = ((MaterialEditText) editionView).getText().toString();
            return (TextUtils.isEmpty(value)) ? null : Double.parseDouble(value);
        }
        return null;
    }

    public String getHumanReadableEditionValue()
    {
        if (editionValue == null) { return null; }
        if (editionValue instanceof Double)
        {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format((Double) editionValue);
        }
        return editionValue.toString();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public View setupEditionView(Object value)
    {
        MaterialEditText edit = (MaterialEditText) super.setupEditionView(value);
        edit.addTextChangedListener(new DecimalTextWatcher(edit, 10, 2));
        edit.setFloatingLabelText(getLabelText(getCurrencyLabel()));

        if ((((AmountFieldRepresentation) data).isEnableFractions()))
        {
            edit.setInputType(EditorInfo.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                    | EditorInfo.TYPE_NUMBER_FLAG_SIGNED);

        }
        else
        {
            edit.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_SIGNED);
        }
        return edit;
    }

    protected String getCurrencyLabel()
    {
        String currency = ((AmountFieldRepresentation) data).getCurrency();
        StringBuilder b = new StringBuilder(data.getName());
        if (!TextUtils.isEmpty(currency))
        {
            b.append(" (");
            b.append(currency);
            b.append(")");
        }
        return b.toString();
    }

    public class DecimalTextWatcher implements TextWatcher
    {
        private NumberFormat numberFormat = NumberFormat.getNumberInstance();

        private EditText editText;

        private String temp = "";

        private int moveCaretTo;

        private int integerConstraint;

        private int fractionConstraint;

        private int maxLength;

        /**
         * Add a text watcher to Edit text for decimal formats
         *
         * @param editText EditText to add DecimalTextWatcher
         * @param before digits before decimal point
         * @param after digits after decimal point
         */
        public DecimalTextWatcher(EditText editText, int before, int after)
        {
            this.editText = editText;
            this.integerConstraint = before;
            this.fractionConstraint = after;
            this.maxLength = before + after + 1;
            numberFormat.setMaximumIntegerDigits(integerConstraint);
            numberFormat.setMaximumFractionDigits(fractionConstraint);
            numberFormat.setRoundingMode(RoundingMode.DOWN);
            numberFormat.setGroupingUsed(false);
        }

        private int countOccurrences(String str, char c)
        {
            int count = 0;
            for (int i = 0; i < str.length(); i++)
            {
                if (str.charAt(i) == c)
                {
                    count++;
                }
            }
            return count;
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            // remove to prevent StackOverFlowException
            editText.removeTextChangedListener(this);
            String ss = s.toString();
            int len = ss.length();
            int dots = countOccurrences(ss, '.');
            boolean shouldParse = dots <= 1 && (dots == 0 ? len != (integerConstraint + 1) : len < (maxLength + 1));
            boolean x = false;
            if (dots == 1)
            {
                int indexOf = ss.indexOf('.');
                try
                {
                    if (ss.charAt(indexOf + 1) == '0')
                    {
                        shouldParse = false;
                        x = true;
                        if (ss.substring(indexOf).length() > 2)
                        {
                            shouldParse = true;
                            x = false;
                        }
                    }
                }
                catch (Exception ex)
                {
                }
            }
            if (shouldParse)
            {
                if (len > 1 && ss.lastIndexOf(".") != len - 1)
                {
                    try
                    {
                        Double d = Double.parseDouble(ss);
                        if (d != null)
                        {
                            editText.setText(numberFormat.format(d));
                        }
                    }
                    catch (NumberFormatException e)
                    {
                    }
                }
            }
            else
            {
                if (x)
                {
                    editText.setText(ss);
                }
                else
                {
                    editText.setText(temp);
                }
            }
            editText.addTextChangedListener(this); // reset listener

            // tried to fix caret positioning after key type:
            if (editText.getText().toString().length() > 0)
            {
                if (dots == 0 && len >= integerConstraint && moveCaretTo > integerConstraint)
                {
                    moveCaretTo = integerConstraint;
                }
                else if (dots > 0 && len >= (maxLength) && moveCaretTo > (maxLength))
                {
                    moveCaretTo = maxLength;
                }
                try
                {
                    editText.setSelection(editText.getText().toString().length());
                    // et.setSelection(moveCaretTo); <- almost had it :))
                }
                catch (Exception e)
                {
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            moveCaretTo = editText.getSelectionEnd();
            temp = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            int length = editText.getText().toString().length();
            if (length > 0)
            {
                moveCaretTo = start + count - before;
            }
        }
    }
}

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.activiti.client.api.model.editor.form.OptionRepresentation;
import com.activiti.client.api.model.editor.form.RestFieldRepresentation;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView;

/**
 * Created by jpascal on 28/03/2015.
 */
public class TypeAhead extends BaseField
{
    protected int editLayoutId;

    protected int readLayoutId;

    private Map<String, OptionRepresentation> optionsIndex;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public TypeAhead(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);
        editLayoutId = R.layout.form_edit_autocompletetext;
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
        if (editionValue instanceof OptionRepresentation)
        {
            return editionValue;
        }
        else
        {
            editionValue = ((MaterialMultiAutoCompleteTextView) editionView).getText().toString();
            return TextUtils.isEmpty((String) editionValue) ? null : editionValue;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VIEW GENERATOR
    // ///////////////////////////////////////////////////////////////////////////
    public View setupEditionView(Object value)
    {
        editionValue = value;

        View vr = inflater.inflate(editLayoutId, null);
        ((MaterialMultiAutoCompleteTextView) vr).setText(getHumanReadableEditionValue());

        // Asterix if required
        ((MaterialMultiAutoCompleteTextView) vr).setFloatingLabelText(getLabelText(data.getName()));
        ((MaterialMultiAutoCompleteTextView) vr).setHint(getLabelText(data.getName()));

        if (!TextUtils.isEmpty(data.getPlaceholder()))
        {
            ((MaterialMultiAutoCompleteTextView) vr).setHint(data.getPlaceholder());
            ((MaterialMultiAutoCompleteTextView) vr).setFloatingLabelAlwaysShown(true);
        }

        ((MaterialMultiAutoCompleteTextView) vr).setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
            {
                String item = (String) adapter.getItemAtPosition(position);
                editionValue = optionsIndex.get(item);
                getFormManager().evaluateViews();
            }
        });

        ((MaterialMultiAutoCompleteTextView) vr).addTextChangedListener(new TextWatcher()
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

    @Override
    public Object getOutputValue()
    {
        if (editionValue instanceof OptionRepresentation)
        {
            return editionValue;
        }
        else
        {
            editionValue = ((MaterialMultiAutoCompleteTextView) editionView).getText().toString();
            return TextUtils.isEmpty((String) editionValue) ? null : editionValue;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ERROR
    // ///////////////////////////////////////////////////////////////////////////
    public void showError()
    {
        if (isValid()) { return; }
        ((MaterialMultiAutoCompleteTextView) editionView)
                .setError(String.format(getString(R.string.form_error_message_required), data.getName()));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // AUTOCOMPLETE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void setFragment(AlfrescoFragment fr) {
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

                        ArrayList<String> optionsValue = new ArrayList<String>(response.body().size());
                        optionsIndex = new LinkedHashMap<>(response.body().size());
                        for (OptionRepresentation item : response.body())
                        {
                            optionsValue.add(item.getName());
                            optionsIndex.put(item.getName(), item);
                        }

                        ArrayAdapter adapter = new TypeAheadAdapter(getFragment().getActivity(),
                                R.layout.row_single_line, optionsValue);
                        ((MaterialMultiAutoCompleteTextView) editionView).setAdapter(adapter);
                        ((MaterialMultiAutoCompleteTextView) editionView).setTokenizer(new SpaceTokenizer());
                        ((MaterialMultiAutoCompleteTextView) editionView).setThreshold(1);
                    }

                    @Override
                    public void onFailure(Call<List<OptionRepresentation>> call, Throwable error)
                    {
                    }
                });
    }

    public class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer
    {
        public int findTokenStart(CharSequence text, int cursor)
        {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != ' ')
            {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ')
            {
                i++;
            }

            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor)
        {
            int i = cursor;
            int len = text.length();

            while (i < len)
            {
                if (text.charAt(i) == ' ')
                {
                    return i;
                }
                else
                {
                    i++;
                }
            }

            return len;
        }

        public CharSequence terminateToken(CharSequence text)
        {
            int i = text.length();

            while (i > 0 && text.charAt(i - 1) == ' ')
            {
                i--;
            }

            if (i > 0 && text.charAt(i - 1) == ' ')
            {
                return text;
            }
            else
            {
                if (text instanceof Spanned)
                {
                    SpannableString sp = new SpannableString(text + " ");
                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
                    return sp;
                }
                else
                {
                    return text + " ";
                }
            }
        }
    }
}

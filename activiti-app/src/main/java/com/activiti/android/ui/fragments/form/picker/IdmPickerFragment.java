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

package com.activiti.android.ui.fragments.form.picker;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.activiti.android.app.R;
import com.activiti.android.app.activity.MainActivity;
import com.activiti.android.platform.intent.RequestCode;
import com.activiti.android.platform.utils.FeatureUtils;
import com.activiti.android.ui.fragments.base.BasePagingGridFragment;
import com.activiti.android.ui.fragments.common.ListingModeFragment;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.model.idm.LightUserRepresentation;

/**
 * @author Jean Marie Pascal
 */
public abstract class IdmPickerFragment extends BasePagingGridFragment implements ListingModeFragment
{
    protected static final String ARGUMENT_FIELD_ID = "fieldId";

    protected static final String ARGUMENT_KEYWORD = "keyword";

    protected static final String ARGUMENT_TASK_ID = "task";

    protected static final String ARGUMENT_PROCESS_ID = "processId";

    protected static final String ARGUMENT_GROUP_ID = "groupId";

    protected static final String ARGUMENT_TITLE = "queryDescription";

    protected int mode = MODE_LISTING;

    protected Fragment fragmentPick;

    protected Button validation;

    protected boolean singleChoice = true;

    protected String keywords;

    protected String fieldId;

    protected String groupId;

    protected EditText searchForm;

    protected boolean hasTextToSpeech = false;

    protected View searchView;

    protected ImageButton searchAction, speechToText;

    // //////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // //////////////////////////////////////////////////////////////////////
    public IdmPickerFragment()
    {
        retrieveDataOnCreation = false;
        setHasOptionsMenu(true);
    }

    @Override
    protected void onRetrieveParameters(Bundle bundle)
    {
        super.onRetrieveParameters(bundle);
        groupId = getArguments().getString(ARGUMENT_GROUP_ID);
        fieldId = getArguments().getString(ARGUMENT_FIELD_ID);
        keywords = getArguments().getString(ARGUMENT_KEYWORD);
        mTitle = getArguments().getString(ARGUMENT_TITLE);
        mode = getArguments().getInt(ARGUMENT_MODE);
        singleChoice = getArguments().getBoolean(ARGUMENT_SINGLE_CHOICE);
        String pickFragmentTag = getArguments().getString(ARGUMENT_FRAGMENT_TAG);
        fragmentPick = getFragmentManager().findFragmentByTag(pickFragmentTag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (getArguments() != null)
        {
            onRetrieveParameters(getArguments());
        }

        // Create View
        setRootView(inflater.inflate(R.layout.fr_idm_picker, container, false));

        // Init list
        init(getRootView(), emptyListMessageId);
        gv.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        setListShown(true);

        searchView = UIUtils.setActionBarCustomView(getActivity(), R.layout.person_picker_header, true);

        View searchBack = searchView.findViewById(R.id.search_back);
        searchBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        if (keywords != null && !keywords.isEmpty())
        {
            search(keywords);
        }
        else
        {
            // Speech to Text
            hasTextToSpeech = FeatureUtils.hasSpeechToText(getActivity());
            speechToText = (ImageButton) searchView.findViewById(R.id.search_microphone);
            if (hasTextToSpeech)
            {
                speechToText.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        speechToText();
                    }
                });
            }
            else
            {
                speechToText.setVisibility(View.GONE);
            }

            searchAction = (ImageButton) searchView.findViewById(R.id.search_start);
            searchAction.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (searchForm.getText().length() > 0)
                    {
                        keywords = searchForm.getText().toString();
                        search(keywords);
                    }
                    else
                    {
                        // TODO Snackbar
                    }
                }
            });
            searchAction.setVisibility(View.GONE);

            // Init form search
            searchForm = (EditText) searchView.findViewById(R.id.search_query);
            searchForm.requestFocus();
            UIUtils.showKeyboard(getActivity(), searchForm);
            searchForm.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            searchForm.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    if (s.length() == 0)
                    {
                        searchAction.setVisibility(View.GONE);
                        if (hasTextToSpeech)
                        {
                            speechToText.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        speechToText.setVisibility(View.GONE);
                        searchAction.setVisibility(View.VISIBLE);
                    }
                }
            });

            searchForm.setOnEditorActionListener(new OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                {
                    if (event != null
                            && (event.getAction() == KeyEvent.ACTION_DOWN)
                            && ((actionId == EditorInfo.IME_ACTION_SEARCH)
                                    || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || (actionId == EditorInfo.IME_ACTION_DONE)))
                    {
                        if (searchForm.getText().length() > 0)
                        {
                            keywords = searchForm.getText().toString();
                            search(keywords);
                        }
                        else
                        {
                            // TODO Snackbar
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        if (getMode() == MODE_PICK)
        {
            Button cancel = UIUtils.initCancel(getRootView(), R.string.general_action_cancel);
            cancel.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (getDialog() != null)
                    {
                        getDialog().dismiss();
                    }
                    else
                    {
                        getFragmentManager().popBackStack();
                    }
                }
            });
        }
        else
        {
            hide(R.id.validation_panel);
        }

        return getRootView();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onStart()
    {
        if (getDialog() != null)
        {
            // TODO another layout for dialog picker
        }
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getRootView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES)
        {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        else
        {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case RequestCode.TEXT_TO_SPEECH:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchForm.setText(text.get(0));
                    search(text.get(0));
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        UIUtils.hideKeyboard(getActivity(), getRootView());
        UIUtils.setActionBarDefault((MainActivity) getActivity());
    }

    // //////////////////////////////////////////////////////////////////////
    // Public Method
    // //////////////////////////////////////////////////////////////////////
    protected void search(String keywords)
    {
        this.keywords = keywords;
        performRequest();
    }

    private void speechToText()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());

        try
        {
            startActivityForResult(intent, RequestCode.TEXT_TO_SPEECH);
        }
        catch (ActivityNotFoundException a)
        {
            // Error

        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LISTENERS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public int getMode()
    {
        return mode;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LISTENERS
    // ///////////////////////////////////////////////////////////////////////////
    public void onSelect(Map<String, LightUserRepresentation> selectedItems)
    {
        if (fragmentPick == null) { return; }
        if (fieldId != null && fragmentPick instanceof onPickAuthorityFragment)
        {
            ((onPickAuthorityFragment) fragmentPick).onPersonSelected(fieldId, selectedItems);
        }
    }

    public void onClear()
    {
        if (fragmentPick == null) { return; }
        if (fieldId != null && fragmentPick instanceof onPickAuthorityFragment)
        {
            ((onPickAuthorityFragment) fragmentPick).onPersonClear(fieldId);
        }
    }

    public interface onPickAuthorityFragment
    {
        void onPersonSelected(String fieldId, Map<String, LightUserRepresentation> p);

        void onPersonClear(String fieldId);

        Map<String, LightUserRepresentation> getPersonSelected(String fieldId);
    }
}

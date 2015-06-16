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

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.activiti.android.app.R;
import com.activiti.android.platform.intent.RequestCode;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder;
import com.activiti.client.api.model.idm.LightUserRepresentation;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Jean Marie Pascal
 */
public class ActivitiUserPickerFragment extends AlfrescoFragment
{
    public static final String TAG = ActivitiUserPickerFragment.class.getName();

    protected static final String ARGUMENT_FRAGMENT_TAG = "org.alfresco.mobile.android.application.param.fragment.tag";

    protected static final String ARGUMENT_TASK_ID = "task";

    protected static final String ARGUMENT_PROCESS_ID = "processId";

    protected static final String ARGUMENT_FIELD_ID = "fieldId";

    private Map<String, LightUserRepresentation> selectedItems = new HashMap<>(1);

    protected String taskId, processId;

    protected EditText searchForm;

    protected ImageButton searchAction, searchContact;

    protected String email;

    protected String fieldId;

    protected Fragment fragmentPick;

    // //////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // //////////////////////////////////////////////////////////////////////
    public ActivitiUserPickerFragment()
    {
        setHasOptionsMenu(true);
    }

    // //////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    // //////////////////////////////////////////////////////////////////////
    protected static ActivitiUserPickerFragment newInstanceByTemplate(Bundle b)
    {
        ActivitiUserPickerFragment cbf = new ActivitiUserPickerFragment();
        cbf.setArguments(b);
        return cbf;
    }

    protected void onRetrieveParameters(Bundle bundle)
    {
        taskId = getArguments().getString(ARGUMENT_TASK_ID);
        processId = getArguments().getString(ARGUMENT_PROCESS_ID);
        fieldId = getArguments().getString(ARGUMENT_FIELD_ID);
        String pickFragmentTag = getArguments().getString(ARGUMENT_FRAGMENT_TAG);
        fragmentPick = getFragmentManager().findFragmentByTag(pickFragmentTag);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        if (getArguments() != null)
        {
            onRetrieveParameters(getArguments());
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.fr_activiti_idm_picker, false).positiveText(R.string.general_action_confirm)
                .cancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        dismiss();
                    }
                }).negativeText(R.string.general_action_cancel).callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        if (searchForm.getText().length() > 0)
                        {
                            email = searchForm.getText().toString();
                            ((UserEmailPickerCallback) fragmentPick).onUserEmailSelected(fieldId, email);
                            dialog.dismiss();
                        }
                        else
                        {
                            // TODO Snackbar
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog)
                    {
                        dialog.dismiss();
                    }
                });

        return builder.show();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        searchForm = ((EditText) getDialog().getWindow().findViewById(R.id.search_query));
        if (searchForm.getText().length() == 0)
        {
            ((MaterialDialog) getDialog()).getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
        searchForm.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() == 0 || !s.toString().contains("@"))
                {
                    ((MaterialDialog) getDialog()).getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }
                else
                {
                    ((MaterialDialog) getDialog()).getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
        searchContact = ((ImageButton) getDialog().getWindow().findViewById(R.id.search_contact));
        searchContact.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pickContact();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case RequestCode.PICK_CONTACT:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        Uri contactUri = data.getData();

                        // Cursor loader to query optional contact email
                        CursorLoader clEmail = new CursorLoader(getActivity());
                        clEmail.setProjection(new String[] { ContactsContract.CommonDataKinds.Email.ADDRESS });
                        clEmail.setUri(contactUri);
                        Cursor cursor = clEmail.loadInBackground();
                        cursor.moveToFirst();

                        // Retrieve the phone number from the NUMBER column
                        int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                        String email = cursor.getString(column);
                        searchForm.setText(email);
                        ((MaterialDialog) getDialog()).getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                    catch (Exception error)
                    {
                        Snackbar.make(getActivity().findViewById(R.id.left_panel), error.getMessage(),
                                Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    // //////////////////////////////////////////////////////////////////////
    // LOADERS
    // //////////////////////////////////////////////////////////////////////
    private void pickContact()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);

        try
        {
            startActivityForResult(intent, RequestCode.PICK_CONTACT);
        }
        catch (ActivityNotFoundException a)
        {
            // Error
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LISTENERS
    // ///////////////////////////////////////////////////////////////////////////
    public interface UserEmailPickerCallback
    {
        void onUserEmailSelected(String fieldId, String username);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity activity)
    {
        return new Builder(activity);
    }

    public static class Builder extends AlfrescoFragmentBuilder
    {
        // ///////////////////////////////////////////////////////////////////////////
        // CONSTRUCTORS
        // ///////////////////////////////////////////////////////////////////////////
        public Builder(FragmentActivity activity)
        {
            super(activity);
            this.extraConfiguration = new Bundle();
        }

        public Builder(FragmentActivity appActivity, Map<String, Object> configuration)
        {
            super(appActivity, configuration);
        }

        // ///////////////////////////////////////////////////////////////////////////
        // SETTERS
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        }

        // ///////////////////////////////////////////////////////////////////////////
        // SETTERS
        // ///////////////////////////////////////////////////////////////////////////
        public Builder fieldId(String fieldId)
        {
            extraConfiguration.putString(ARGUMENT_FIELD_ID, fieldId);
            return this;
        }

        public Builder taskId(String taskId)
        {
            extraConfiguration.putString(ARGUMENT_TASK_ID, taskId);
            return this;
        }

        public Builder processId(String processId)
        {
            extraConfiguration.putString(ARGUMENT_PROCESS_ID, processId);
            return this;
        }

        public Builder fragmentTag(String fragmentTag)
        {
            extraConfiguration.putString(ARGUMENT_FRAGMENT_TAG, fragmentTag);
            return this;
        }
    }
}

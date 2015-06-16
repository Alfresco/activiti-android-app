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

package com.activiti.android.ui.fragments.comment;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.platform.utils.BundleUtils;
import com.activiti.android.sdk.model.runtime.ParcelTask;
import com.activiti.android.ui.fragments.base.BasePagingGridFragment;
import com.activiti.android.ui.utils.DisplayUtils;
import com.activiti.android.ui.utils.UIUtils;
import com.activiti.client.api.constant.RequestConstant;
import com.activiti.client.api.model.runtime.CommentRepresentation;
import com.activiti.client.api.model.runtime.CommentsRepresentation;

public class CommentsFoundationFragment extends BasePagingGridFragment
{
    public static final String TAG = CommentsFoundationFragment.class.getName();

    protected static final String ARGUMENT_RO = "isReadOnly";

    protected static final String ARGUMENT_CREATE = "isCreate";

    protected static final String ARGUMENT_TASK_ID = "userId";

    protected static final String ARGUMENT_TASK = "task";

    protected static final String ARGUMENT_PROCESS_ID = RequestConstant.ARGUMENT_PROCESS_ID;

    protected String taskId;

    protected ParcelTask task;

    protected String processId;

    protected Boolean isCreate, isReadOnly;

    private EditText commentText;

    private ImageButton bAdd;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS & HELPERS
    // ///////////////////////////////////////////////////////////////////////////
    public CommentsFoundationFragment()
    {
        emptyListMessageId = R.string.empty_comment;
        retrieveDataOnCreation = true;
        setRetainInstance(true);
        enableTitle = false;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    protected void onRetrieveParameters(Bundle bundle)
    {
        task = bundle.getParcelable(ARGUMENT_TASK);
        taskId = (task != null) ? task.id : BundleUtils.getString(bundle, ARGUMENT_TASK_ID);
        processId = BundleUtils.getString(bundle, ARGUMENT_PROCESS_ID);
        isCreate = BundleUtils.getBoolean(bundle, ARGUMENT_CREATE);
        isReadOnly = BundleUtils.getBoolean(bundle, ARGUMENT_RO);
    }

    protected Callback<CommentsRepresentation> callBack = new Callback<CommentsRepresentation>()
    {
        @Override
        public void success(CommentsRepresentation response, Response response2)
        {
            displayData(response);
            gv.smoothScrollToPosition(response.getSize());
            updateFragmentIcon(response.getSize() > 0);
        }

        @Override
        public void failure(RetrofitError error)
        {
            displayError(error);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRootView(inflater.inflate(R.layout.fr_comments, container, false));

        init(getRootView(), R.string.task_help_add_comment);

        commentText = (EditText) viewById(R.id.comment_value);

        bAdd = (ImageButton) viewById(R.id.send_comment);
        bAdd.setEnabled(false);

        bAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addcomment();
            }
        });

        commentText.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                activateSend();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
        });

        commentText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        commentText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (event != null && (event.getAction() == KeyEvent.ACTION_DOWN)
                        && ((actionId == EditorInfo.IME_ACTION_SEND)))
                {
                    addcomment();
                    return true;
                }
                return false;
            }
        });

        gv.setSelector(android.R.color.transparent);
        gv.setCacheColorHint(android.R.color.transparent);

        return getRootView();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isReadOnly != null && isReadOnly)
        {
            hide(R.id.comment_container);
            if (gv.getAdapter() == null || gv.getAdapter().isEmpty())
            {
                ((TextView) viewById(R.id.empty_text)).setText(R.string.empty_comment);
            }
            return;
        }

        if (isCreate != null && isCreate)
        {
            commentText.requestFocus();
            UIUtils.showKeyboard(getActivity(), commentText);
            isCreate = null;
        }

        if (!TextUtils.isEmpty(taskId))
        {
            commentText.setHint(R.string.task_message_new_comment_placeholder);
        }
        else if (!TextUtils.isEmpty(processId))
        {
            commentText.setHint(R.string.process_message_new_comment_placeholder);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateFragmentIcon(adapter != null && !adapter.isEmpty());
    }

    @Override
    protected void performRequest()
    {
        if (!TextUtils.isEmpty(taskId))
        {
            getAPI().getTaskService().getComments(taskId, callBack);
        }
        else if (!TextUtils.isEmpty(processId))
        {
            getAPI().getProcessService().getComments(processId, callBack);
        }
    }

    @Override
    protected BaseAdapter onAdapterCreation()
    {
        return new CommentAdapter(this, R.layout.row_comment, new ArrayList<CommentRepresentation>(0));
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ACTIONS
    // ///////////////////////////////////////////////////////////////////////////
    private void updateFragmentIcon(boolean hasComment)
    {
        if (getActivity() == null) { return; }
        Fragment fr = getActivity().getSupportFragmentManager().findFragmentById(
                DisplayUtils.getLeftFragmentId(getActivity()));
        if (fr != null && fr instanceof FragmentWithComments)
        {
            ((FragmentWithComments) fr).hasComment(hasComment);
        }
    }

    private void activateSend()
    {
        if (commentText.getText().length() > 0)
        {
            bAdd.setEnabled(true);
        }
        else
        {
            bAdd.setEnabled(false);
        }
    }

    private void addcomment()
    {
        if (commentText.getText().length() > 0)
        {
            sendComment();
            onPrepareRefresh();
            commentText.setEnabled(false);
            bAdd.setEnabled(false);
        }
        else
        {
            commentText.setError("Empty");
        }
    }

    private void sendComment()
    {
        Callback<CommentRepresentation> callback = new Callback<CommentRepresentation>()
        {
            @Override
            public void success(CommentRepresentation commentResponse, Response response)
            {
                commentText.setEnabled(true);
                commentText.setText("");
                bAdd.setEnabled(false);
                refresh();
            }

            @Override
            public void failure(RetrofitError error)
            {

            }
        };

        if (!TextUtils.isEmpty(taskId))
        {
            getAPI().getTaskService().addComment(taskId, new CommentRepresentation(commentText.getText().toString()),
                    callback);
        }
        else if (!TextUtils.isEmpty(processId))
        {
            getAPI().getProcessService().addComment(processId,
                    new CommentRepresentation(commentText.getText().toString()), callback);
        }

    }
}

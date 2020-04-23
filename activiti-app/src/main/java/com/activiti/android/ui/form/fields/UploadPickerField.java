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
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activiti.android.app.ActivitiVersionNumber;
import com.activiti.android.app.R;
import com.activiti.android.platform.EventBusManager;
import com.activiti.android.platform.integration.alfresco.AlfrescoIntegrator;
import com.activiti.android.platform.provider.transfer.ContentTransferEvent;
import com.activiti.android.platform.provider.transfer.ContentTransferManager;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.content.ContentHelper;
import com.activiti.android.ui.holder.HolderUtils;
import com.activiti.android.ui.holder.TwoLinesViewHolder;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.activiti.client.api.model.runtime.RelatedContentRepresentation;
import com.activiti.client.api.model.runtime.request.AddContentRelatedRepresentation;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

/**
 * Created by jpascal on 28/03/2015.
 */
public class UploadPickerField extends BaseField implements MultiValueField
{
    private boolean isMultiple = false;

    private boolean isLink = false;

    private boolean linkSupported = false;

    private MaterialEditText tv;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public UploadPickerField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
    {
        super(context, manager, data, isReadMode);

        // Retrieve data information
        if (data.getParams() != null)
        {
            isMultiple = data.getParams().get("multiple") != null
                    && Boolean.parseBoolean((String) data.getParams().get("multiple"));
            isLink = data.getParams().get("link") != null
                    && Boolean.parseBoolean((String) data.getParams().get("link"));
            linkSupported = getFormManager().getVersionNumber() >= ActivitiVersionNumber.VERSION_1_2_2 && isLink;
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // READ
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * Used this time to only manage values. It returns no values
     */
    public String getHumanReadableReadValue()
    {
        if (originalValue == null) { return ""; }
        Object tempValues = originalValue;
        originalValue = new ArrayList<RelatedContentRepresentation>();
        if (tempValues instanceof List)
        {
            ((List) originalValue).clear();
            for (Object item : (List) tempValues)
            {
                if (item instanceof Map)
                {
                    ((List) originalValue).add(RelatedContentRepresentation.parse(item));
                }
                else if (item instanceof RelatedContentRepresentation)
                {
                    ((List) originalValue).add(item);
                }
            }
        }
        return "";
    }

    public View setupReadView()
    {
        ViewGroup vr = (ViewGroup) inflater.inflate(R.layout.form_user_upload, null);

        // Manage values & retrieve them
        getHumanReadableReadValue();

        // Remove icon & clickable, add title,
        tv = (MaterialEditText) vr.findViewById(R.id.content_header);
        tv.setHint(data.getName());
        tv.setIconRight((Drawable) null);
        vr.findViewById(R.id.button_container).setBackground(null);

        // Display empty
        if (!hasDisplayedEmpty(vr, originalValue, true))
        {
            // Create Item Rows
            for (Object item : (List) originalValue)
            {
                createRow((ViewGroup) vr.findViewById(R.id.contents_container), (RelatedContentRepresentation) item,
                        true);
            }
        }

        readView = vr;

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VALUES
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    /**
     * Used this time to only manage values. It returns no values
     */
    public String getHumanReadableEditionValue()
    {
        if (editionValue == null) { return ""; }
        Object tempValues = originalValue;
        editionValue = new ArrayList<RelatedContentRepresentation>();
        if (tempValues instanceof List)
        {
            for (Object item : (List) tempValues)
            {
                if (item instanceof Map)
                {
                    ((List) editionValue).add(RelatedContentRepresentation.parse(item));
                }
                else if (item instanceof RelatedContentRepresentation)
                {
                    ((List) editionValue).add(item);
                }
            }
        }
        return "";
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    protected void updateEditionView()
    {
        if (editionView != null) {
            // We update only rows.
            ViewGroup container = (ViewGroup) editionView.findViewById(R.id.contents_container);
            container.removeAllViews();

            displayAddIcon();
            displayUnsupported((ViewGroup) editionView);

            if (!hasDisplayedEmpty((ViewGroup) editionView, editionValue, false)) {
                // Create Item Rows
                for (Object item : (List) editionValue) {
                    createRow(container, (RelatedContentRepresentation) item, false);
                }
            }
        }
        
        getFormManager().evaluateViews();
    }

    protected void updateReadView() {

        // use the same values for now as on the updateEditionView method
        if (editionView != null) {
            // We update only rows.
            ViewGroup container = editionView.findViewById(R.id.contents_container);
            container.removeAllViews();

            displayAddIcon();
            displayUnsupported((ViewGroup) editionView);

            if (!hasDisplayedEmpty((ViewGroup) editionView, editionValue, false)) {
                // Create Item Rows
                for (Object item : (List) editionValue) {
                    createRow(container, (RelatedContentRepresentation) item, false);
                }
            }
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

        getHumanReadableEditionValue();

        ViewGroup vr = (ViewGroup) inflater.inflate(R.layout.form_user_upload, null);
        editionView = vr;

        tv = (MaterialEditText) vr.findViewById(R.id.content_header);
        tv.setFocusable(false);
        tv.setHint(getLabelText(data.getName()));

        displayAddIcon();
        displayUnsupported(vr);

        // Create Item Rows
        if (!hasDisplayedEmpty(vr, editionValue, false))
        {
            for (Object item : (List) editionValue)
            {
                createRow((ViewGroup) vr.findViewById(R.id.contents_container), (RelatedContentRepresentation) item,
                        false);
            }
        }

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PICKERS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void addValue(Object object)
    {
        if (editionValue == null)
        {
            editionValue = new ArrayList<>(1);
        }

        // Check is Correct Link
        Uri uri = Uri.parse(((RelatedContentRepresentation) object).getLinkUrl());
        // Detect if it comes from Alfresco ?
        AddContentRelatedRepresentation content = null;
        if (AlfrescoIntegrator.STORAGE_ACCESS_PROVIDER.equals(uri.getAuthority()))
        {
            content = ContentTransferManager.prepareLink(getFragment(), uri);
        }

        // Time to upload the content
        try
        {
            EventBusManager.getInstance().register(this);
        }
        catch (Exception e)
        {
            // Do nothing
        }

        // Link supported
        if (linkSupported)
        {
            if (content == null)
            {
                // Error Message !
                MaterialDialog.Builder builder = new MaterialDialog.Builder(getFragment().getActivity())
                        .title(R.string.form_message_link_error_title)
                        .cancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        })
                        .content(Html.fromHtml(getFragment().getActivity().getString(R.string.form_message_link_error)))
                        .positiveText(R.string.ok).callback(new MaterialDialog.ButtonCallback()
                        {
                            @Override
                            public void onPositive(MaterialDialog dialog)
                            {
                            }
                        });
                builder.show();

            }
            else
            {
                // Link is possible.
                ContentTransferManager.requestAlfrescoLink(content, getFragment(), null,
                        ContentTransferManager.TYPE_LINK_ID);
                ((List) editionValue).add(object);
            }
        }
        else if (!isLink)
        {
            // Upload the document
            ContentTransferManager.requestUpload(getFragment(),
                    Uri.parse(((RelatedContentRepresentation) object).getLinkUrl()), null, -1,
                    ((RelatedContentRepresentation) object).getMimeType());
            ((List) editionValue).add(object);
        }

        updateEditionView();
    }

    public boolean isPickerRequired()
    {
        return true;
    }

    public void setFragment(AlfrescoFragment fr)
    {
        super.setFragment(fr);
        if (getFragment() != null && editionView != null && !data.isReadOnly())
        {
            if (isLink && !linkSupported) { return; }
            editionView.findViewById(R.id.button_container).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    formManagerRef.get().setCurrentPickerField(UploadPickerField.this);
                    ContentTransferManager.requestGetContentFromProvider(getFragment());
                }
            });

            displayAddIcon();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // OUTPUT VALUE
    // ///////////////////////////////////////////////////////////////////////////
    public Object getOutputValue()
    {
        if (editionValue == null) { return null; }
        StringBuilder values = new StringBuilder();
        for (Object item : (List) editionValue)
        {
            if (values.length() > 0)
            {
                values.append(",");
            }
            values.append(((RelatedContentRepresentation) item).getId());
        }

        return values.toString();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIST MANAGEMENT
    // ///////////////////////////////////////////////////////////////////////////
    private boolean isEmpty(Object listOfValues)
    {
        if (listOfValues == null) { return true; }
        if (listOfValues instanceof List) { return ((List) listOfValues).isEmpty(); }
        return true;
    }

    private void enablePicker(boolean enable)
    {
        if (editionView != null)
        {
            editionView.findViewById(R.id.button_container).setClickable(enable);
        }
    }

    private void displayAddIcon()
    {
        tv.setError(null);

        // Link is unsupported until 1.2.2
        if (isLink && getFormManager().getVersionNumber() < ActivitiVersionNumber.VERSION_1_2_2)
        {
            tv.setIconRight((Drawable) null);
            enablePicker(false);
        }
        else if (isMultiple)
        {
            tv.setIconRight(R.drawable.ic_content_add);
            enablePicker(true);
        }
        else if (isEmpty(editionValue))
        {
            tv.setIconRight(R.drawable.ic_content_add);
            enablePicker(true);
        }
        else
        {
            tv.setIconRight((Drawable) null);
            enablePicker(false);
        }
    }

    private boolean hasDisplayedEmpty(ViewGroup root, Object contents, boolean isReadOnly)
    {

        if (contents == null || (contents instanceof List && ((List) contents).isEmpty()))
        {
            // Display empty message
            root.findViewById(R.id.content_empty_message).setVisibility(View.VISIBLE);
            if (isReadOnly)
            {
                ((TextView) root.findViewById(R.id.content_empty_message)).setText(R.string.form_message_empty);
            }
            return true;
        }

        // Hide empty message
        root.findViewById(R.id.content_empty_message).setVisibility(View.GONE);
        return false;
    }

    private void displayUnsupported(ViewGroup root)
    {
        if (!linkSupported)
        {
            if (isLink && data.isRequired())
            {
                getFormManager().abort();
                return;
            }

            // Display empty message
            if (isLink)
            {
                root.findViewById(R.id.content_empty_message).setVisibility(View.VISIBLE);
                ((TextView) root.findViewById(R.id.content_empty_message))
                        .setText(R.string.form_message_unsupported_description);
            }
        }
    }

    private void createRow(ViewGroup contentContainer, RelatedContentRepresentation content, boolean isReadOnly)
    {
        if (content == null) { return; }

        // Enable/Disable + icon
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View swipeView = inflater.inflate(R.layout.row_two_lines_swipe, contentContainer, false);
        TwoLinesViewHolder tlv = HolderUtils.configure(swipeView, content.getName(),
                (content.getId() == -1) ? getString(R.string.upload_in_progress) : null, -1);
        ContentHelper.updateIcon(getContext(), tlv, content, isReadOnly);

        SwipeLayout swipeLayout = (SwipeLayout) swipeView.findViewById(R.id.swipe_layout);
        if (isReadOnly)
        {
            swipeLayout.setTag(content);
            swipeLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ContentHelper.openin(getContext(), ((RelatedContentRepresentation) v.getTag()));
                    getFragment().displayWaiting(R.string.content_message_content_pending);
                }
            });
        }
        else
        {
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);

            LinearLayout actions = (LinearLayout) swipeLayout.findViewById(R.id.bottom_wrapper);
            ImageButton action = (ImageButton) inflater.inflate(R.layout.form_swipe_action_,
                    (LinearLayout) swipeLayout.findViewById(R.id.bottom_wrapper), false);
            action.setImageResource(R.drawable.ic_remove_circle_outline_white);
            action.setTag(content);
            action.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ((List) editionValue).remove(((RelatedContentRepresentation) v.getTag()));
                    updateEditionView();
                }
            });
            actions.addView(action);
        }

        contentContainer.addView(swipeView);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VALIDATION & ERROR
    // ///////////////////////////////////////////////////////////////////////////

    public void showError()
    {
        if (isValid()) { return; }

        if (isLink && data.isRequired())
        {
            tv.setError(String.format(getString(R.string.form_error_message_required), data.getName()));
        }
        else
        {
            tv.setError(String.format(getString(R.string.form_error_message_required), data.getName()));
        }
    }

    @Override
    public boolean isValid()
    {
        // Link is unsupported and we block !
        if (!linkSupported && isLink && data.isRequired()) { return false; }

        if (editionValue != null)
        {
            // Upload in progress we can't validate
            for (Object item : (List) editionValue)
            {
                if (((RelatedContentRepresentation) item).getId() == -1L) { return false; }
            }
        }

        if (!data.isRequired()) { return true; }
        if (data.isRequired() && !isEmpty(editionValue)) { return true; }
        return false;
    }

    // //////////////////////////////////////////////////////////////////////////////////////
    // EVENTS
    // //////////////////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void onContentTransferEvent(ContentTransferEvent transferEvent)
    {
        if (tv == null) { return; }
        if (transferEvent.hasException)
        {
            Object found = null;
            for (Object item : (List) editionValue)
            {
                RelatedContentRepresentation content = (RelatedContentRepresentation) item;
                if (transferEvent.identifier != null && transferEvent.identifier.equals(content.getLinkUrl()))
                {
                    found = item;
                    break;
                }
            }

            if (found != null)
            {
                ((List) editionValue).remove(found);
                updateEditionView();
            }

            tv.setError("Error: " + transferEvent.exception.getMessage());
        }
        else
        {
            Object selectedItem = null;
            for (Object item : (List) editionValue)
            {
                if (((RelatedContentRepresentation) item).getId() == -1L
                        && ((RelatedContentRepresentation) item).getName().equals(transferEvent.response.getName()))
                {
                    selectedItem = item;
                    break;
                }
            }

            if (selectedItem != null)
            {
                int index = ((List) editionValue).indexOf(selectedItem);
                ((List) editionValue).remove(selectedItem);
                ((List) editionValue).add(index, transferEvent.response);
                updateEditionView();
            }
        }

        try
        {
            EventBusManager.getInstance().unregister(this);
        }
        catch (Exception e)
        {
            // DO Nothing
        }
    }
}

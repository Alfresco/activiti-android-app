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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.activiti.client.api.model.editor.form.HyperlinkRepresentation;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by jpascal on 28/03/2015.
 */
public class HyperlinkField extends BaseField
{
    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public HyperlinkField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode)
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

    public View setupdReadView()
    {
        if (!(data instanceof HyperlinkRepresentation)) { return null; }
        View vr = inflater.inflate(R.layout.form_hyperlink, null);
        vr.findViewById(R.id.button_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setData(Uri.parse((((HyperlinkRepresentation) data).getHyperlinkUrl())));
                getFragment().getActivity().startActivity(i);
            }
        });

        MaterialEditText editText = (MaterialEditText) vr.findViewById(R.id.hyperlink);
        editText.setText((((HyperlinkRepresentation) data).getDisplayText()));
        editText.setHideUnderline(true);
        editText.setHelperTextAlwaysShown(true);
        try
        {
            editText.setHelperText(Uri.parse(((HyperlinkRepresentation) data).getHyperlinkUrl()).getHost());
        }
        catch (Exception e)
        {

        }

        // Asterix if required
        editText.setFloatingLabelText(data.getName());
        editText.setFloatingLabelAlwaysShown(true);
        editText.setIconRight(R.drawable.ic_link_grey);

        editionView = vr;
        readView = vr;

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VALUES
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object getEditionValue()
    {
        return null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public View setupEditionView(Object value)
    {
        editionValue = value;

        View vr = inflater.inflate(R.layout.form_hyperlink, null);
        vr.findViewById(R.id.button_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                String url = ((HyperlinkRepresentation) data).getHyperlinkUrl();

                if (!url.startsWith("https://") && !url.startsWith("http://")){
                    url = "http://" + url;
                }

                i.setData(Uri.parse(url));
                getFragment().getActivity().startActivity(i);
            }
        });

        MaterialEditText editText = (MaterialEditText) vr.findViewById(R.id.hyperlink);
        editText.setText(((HyperlinkRepresentation) data).getDisplayText());
        StringBuilder builder = new StringBuilder(data.getName());
        editText.setHideUnderline(true);
        try
        {
            builder.append(" (");
            builder.append(Uri.parse(((HyperlinkRepresentation) data).getHyperlinkUrl()).getAuthority()
                    .replaceFirst("^(http://|http://www\\.|www\\.)", ""));
            builder.append(")");
        }
        catch (Exception e)
        {

        }
        editText.setFloatingLabelText(builder.toString());

        editText.setFloatingLabelAlwaysShown(true);
        editText.setIconRight(R.drawable.ic_link_grey);

        editionView = vr;
        return vr;
    }

    protected void updateEditionView()
    {
        if (getHumanReadableEditionValue() != null && editionView != null)
        {
            ((MaterialEditText) editionView.findViewById(R.id.date_picker)).setText(getHumanReadableEditionValue());
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PICKERS
    // ///////////////////////////////////////////////////////////////////////////
    public boolean isPickerRequired()
    {
        return true;
    }
}

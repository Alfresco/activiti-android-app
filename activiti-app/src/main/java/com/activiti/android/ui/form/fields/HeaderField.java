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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;

/**
 * Created by jpascal on 28/03/2015.
 */
public class HeaderField extends BaseField {
    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public HeaderField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode) {
        super(context, manager, data, isReadMode);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VALUES
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public Object getEditionValue() {
        return null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // VIEW GENERATOR
    // ///////////////////////////////////////////////////////////////////////////
    public View setupReadView() {
        LinearLayout vr = (LinearLayout) inflater.inflate(R.layout.form_header, null);
        ((TextView) vr.findViewById(R.id.header_title)).setText(data.getName());

        readView = vr;

        return vr;
    }

    @Override
    public View setupEditionView(Object value) {
        editionView = setupReadView();

        return editionView;
    }

    @Override
    protected void updateReadView() {
        ((TextView) readView.findViewById(R.id.header_title)).setText(data.getName());
    }

    @Override
    protected void updateEditionView() {
        ((TextView) editionView.findViewById(R.id.header_title)).setText(data.getName());
    }
}

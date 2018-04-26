package com.activiti.android.ui.form.fields;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;

import com.activiti.android.app.R;
import com.activiti.android.ui.form.FormManager;
import com.activiti.android.ui.fragments.AlfrescoFragment;
import com.activiti.android.ui.fragments.form.picker.DateTimePickerFragment;
import com.activiti.client.api.model.editor.form.FormFieldRepresentation;
import com.alfresco.client.utils.ISO8601Utils;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Bogdan Roatis on 4/16/2018.
 */
public class DateTimeField extends BaseField {
    protected MaterialEditText editText;

    private static String DEFAULT_DATE_FORMAT = "dd-MM-yyyy HH : mm";

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public DateTimeField(Context context, FormManager manager, FormFieldRepresentation data, boolean isReadMode) {
        super(context, manager, data, isReadMode);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // READ
    // ///////////////////////////////////////////////////////////////////////////
    public String getHumanReadableReadValue() {
        if (originalValue == null) {
            return getString(R.string.form_message_empty);
        }
        String readValue = "";
        if (originalValue instanceof String) {
            originalValue = ISO8601Utils.parse((String) originalValue);
        }
        if (originalValue instanceof Date) {
            if (!TextUtils.isEmpty(data.getDateDisplayFormat())) {
                try {
                    readValue = DateFormat.format(data.getDateDisplayFormat(), (Date) originalValue).toString();
                } catch (Exception e) {
                    readValue = DateFormat.format(DEFAULT_DATE_FORMAT, (Date) originalValue).toString();
                }
            } else {
                readValue = DateFormat.format(DEFAULT_DATE_FORMAT, (Date) originalValue).toString();
            }
        }
        return readValue;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VALUES
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public String getHumanReadableEditionValue() {
        if (editionValue == null) {
            return "";
        }
        String readValue = "";
        if (editionValue instanceof String) {
            editionValue = ISO8601Utils.parse((String) editionValue);
        }
        if (editionValue instanceof Date) {
            if (!TextUtils.isEmpty(data.getDateDisplayFormat())) {
                try {
                    readValue = DateFormat.format(data.getDateDisplayFormat(), (Date) editionValue).toString();
                } catch (Exception e) {
                    readValue = DateFormat.format(DEFAULT_DATE_FORMAT, (Date) editionValue).toString();
                }
            } else {
                readValue = DateFormat.format(DEFAULT_DATE_FORMAT, (Date) editionValue).toString();
            }
        }
        return readValue;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // EDITION VIEW
    // ///////////////////////////////////////////////////////////////////////////
    protected void updateEditionView() {
        editText.setText(getHumanReadableEditionValue());
        getFormManager().evaluateViews();
    }

    @Override
    public View setupEditionView(Object value) {
        editionValue = value;
        if (value instanceof String) {
            editionValue = ISO8601Utils.parse((String) value);
        }

        View vr = inflater.inflate(R.layout.form_date, null);
        editText = (MaterialEditText) vr.findViewById(R.id.date_picker);
        editText.setText(getHumanReadableEditionValue());

        // Asterix if required
        editText.setFloatingLabelText(getLabelText(data.getName()));

        if (TextUtils.isEmpty(data.getPlaceholder())) {
            editText.setHint(getLabelText(data.getName()));
        } else {
            editText.setFloatingLabelAlwaysShown(true);
            editText.setHint(data.getPlaceholder());
        }
        editText.setFocusable(false);

        editText.setIconRight(R.drawable.ic_schedule_grey);

        editionView = vr;

        return vr;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PICKERS
    // ///////////////////////////////////////////////////////////////////////////
    public boolean isPickerRequired() {
        return true;
    }

    public void setFragment(AlfrescoFragment fr) {
        super.setFragment(fr);
        if (getFragment() != null && editionView != null && !data.isReadOnly()) {
            editionView.findViewById(R.id.button_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateTimePickerFragment.newInstance(
                            data.getId(),
                            getFragment().getTag(),
                            (editionValue != null) ? ((Date) editionValue).getTime() : null,
                            null,
                            null).show(getFragment().getFragmentManager(), DateTimePickerFragment.TAG);

                }
            });
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // OUTPUT VALUE
    // ///////////////////////////////////////////////////////////////////////////
    public Object getOutputValue() {
        if (editionValue == null) {
            return null;
        }
        if (editionValue instanceof Date) {
            return ISO8601Utils.format((Date) editionValue);
        }
        if (editionValue instanceof GregorianCalendar) {
            return ISO8601Utils.format(
                    ((GregorianCalendar) editionValue).getTime(), true);
        }
        return null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // ERROR
    // ///////////////////////////////////////////////////////////////////////////
    public void showError() {
        if (isValid()) {
            return;
        }
        editText.setError(String.format(getString(R.string.form_error_message_required), data.getName()));
    }
}
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

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.TimePicker;

import com.activiti.android.app.R;
import com.activiti.android.ui.fragments.form.picker.DatePickerFragment.OnPickDateFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author jpascal
 */
public class TimePickerFragment extends DialogFragment implements OnTimeSetListener {
    public static final String TAG = TimePickerFragment.class.getName();

    private static final String ARGUMENT_FRAGMENT_TAG = "fragmentTag";

    private static final String ARGUMENT_DATE_ID = "dateId";

    private static final String ARGUMENT_DATE = "date";

    private OnTimeSetListener mListener;

    private boolean isCancelled = false, clearValue = false;

    private GregorianCalendar calendar;

    private TimePickerDialog mTimePickerDialog;

    // //////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // //////////////////////////////////////////////////////////////////////
    public static TimePickerFragment newInstance(String dateId, String fragmentTag) {
        TimePickerFragment bf = new TimePickerFragment();
        Bundle b = new Bundle();
        b.putString(ARGUMENT_DATE_ID, dateId);
        b.putString(ARGUMENT_FRAGMENT_TAG, fragmentTag);
        bf.setArguments(b);
        return bf;
    }

    public static TimePickerFragment newInstance(String dateId, String fragmentTag, Calendar calendar) {
        TimePickerFragment bf = new TimePickerFragment();
        Bundle b = new Bundle();
        b.putString(ARGUMENT_DATE_ID, dateId);
        b.putString(ARGUMENT_FRAGMENT_TAG, fragmentTag);
        b.putSerializable(ARGUMENT_DATE, calendar);
        bf.setArguments(b);
        return bf;
    }

    // //////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    // //////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mListener = this;
    }

    @Override
    public void onDetach() {
        this.mListener = null;
        super.onDetach();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        if (getArguments() != null && getArguments().containsKey(ARGUMENT_DATE)) {
            calendar = (GregorianCalendar) getArguments().get(ARGUMENT_DATE);
        }

        mTimePickerDialog = new TimePickerDialog(getActivity(), mListener, hourOfDay, minute, true);

        mTimePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTimePickerDialog.onClick(dialog, which);
                        dismiss();
                    }
                });
        mTimePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getActivity().getString(R.string.clear),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearValue = true;
                        dismiss();
                    }
                });
        mTimePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getActivity().getString(R.string.general_action_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isCancelled = true;
                        dismiss();
                    }
                });
        return mTimePickerDialog;

    }

    // //////////////////////////////////////////////////////////////////////
    // INTERNALS
    // //////////////////////////////////////////////////////////////////////
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (getArguments() == null || !getArguments().containsKey(ARGUMENT_FRAGMENT_TAG)) {
            return;
        }

        String pickFragmentTag = getArguments().getString(ARGUMENT_FRAGMENT_TAG);
        String dateId = getArguments().getString(ARGUMENT_DATE_ID);
        OnPickDateFragment fragmentPick = ((OnPickDateFragment) getFragmentManager().findFragmentByTag(pickFragmentTag));
        if (fragmentPick == null || isCancelled) {
            return;
        }

        if (clearValue) {
            fragmentPick.onDateClear(dateId);
        } else {
            if (calendar != null) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                fragmentPick.onDatePicked(dateId, calendar);
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////
    // INTERFACE
    // //////////////////////////////////////////////////////////////////////
    public interface OnPickTimeListener {
        void onTimePicked(String dateId, int hourOfDay, int minute);

        void onTimeClear(String dateId);
    }
}

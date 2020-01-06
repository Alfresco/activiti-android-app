package com.activiti.android.ui.fragments.form.picker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.activiti.android.app.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Bogdan Roatis on 4/12/2018.
 */
public class DateTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    public static final String TAG = DateTimePickerFragment.class.getName();

    private static final String ARGUMENT_FRAGMENT_TAG = "fragmentTag";

    private static final String ARGUMENT_DATE_ID = "dateId";

    private static final String ARGUMENT_START_DATE = "startDate";

    private static final String ARGUMENT_MIN_DATE = "minDate";

    private static final String ARGUMENT_MAX_DATE = "maxDate";

    private boolean isCancelled = false, clearValue = false;

    private Long minDate = null, maxDate = null, startDate = null;

    private Calendar mCalendar;

    private DatePickerDialog datePicker;

    private DatePickerDialog.OnDateSetListener mDateListener;

    // //////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // //////////////////////////////////////////////////////////////////////
    public static DateTimePickerFragment newInstance(String dateId, String fragmentTag) {
        DateTimePickerFragment bf = new DateTimePickerFragment();
        Bundle b = new Bundle();
        b.putString(ARGUMENT_DATE_ID, dateId);
        b.putString(ARGUMENT_FRAGMENT_TAG, fragmentTag);
        bf.setArguments(b);
        return bf;
    }

    public static DateTimePickerFragment newInstance(String dateId, String fragmentTag, Long date, Long minDate, Long maxDate) {
        DateTimePickerFragment bf = newInstance(dateId, fragmentTag);
        Bundle b = bf.getArguments();
        if (date != null) {
            b.putLong(ARGUMENT_START_DATE, date);
        }
        if (minDate != null) {
            b.putLong(ARGUMENT_MIN_DATE, minDate);
        }
        if (maxDate != null) {
            b.putLong(ARGUMENT_MAX_DATE, maxDate);
        }
        bf.setArguments(b);
        return bf;
    }

    // //////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    // //////////////////////////////////////////////////////////////////////
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mDateListener = this;
    }

    @Override
    public void onDetach() {
        this.mDateListener = null;
        super.onDetach();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            if (getArguments().containsKey(ARGUMENT_MIN_DATE)) {
                minDate = getArguments().getLong(ARGUMENT_MIN_DATE);
            }
            if (getArguments().containsKey(ARGUMENT_MAX_DATE)) {
                maxDate = getArguments().getLong(ARGUMENT_MAX_DATE);
            }
            if (getArguments().containsKey(ARGUMENT_START_DATE)) {
                startDate = getArguments().getLong(ARGUMENT_START_DATE);
            }
        }

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        if (startDate != null) {
            c.setTime(new Date(startDate));
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        datePicker = new DatePickerDialog(getActivity(), mDateListener, year, month, day);

        if (maxDate != null) {
            datePicker.getDatePicker().setMaxDate(maxDate);
        }

        if (minDate != null) {
            datePicker.getDatePicker().setMinDate(minDate);
        }

        datePicker.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        datePicker.onClick(dialog, which);
                        dismiss();
                    }
                });
        datePicker.setButton(DialogInterface.BUTTON_NEUTRAL, getActivity().getString(R.string.clear),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearValue = true;
                        onDateSet(datePicker.getDatePicker(), datePicker.getDatePicker().getYear(), datePicker.getDatePicker()
                                .getMonth(), datePicker.getDatePicker().getDayOfMonth());
                        dismiss();
                    }
                });
        datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, getActivity().getString(R.string.general_action_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isCancelled = true;
                        onDateSet(datePicker.getDatePicker(), datePicker.getDatePicker().getYear(), datePicker.getDatePicker()
                                .getMonth(), datePicker.getDatePicker().getDayOfMonth());
                        dismiss();
                    }
                });
        return datePicker;

    }

    // //////////////////////////////////////////////////////////////////////
    // INTERNALS
    // //////////////////////////////////////////////////////////////////////

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (getArguments() == null || !getArguments().containsKey(ARGUMENT_FRAGMENT_TAG)) {
            return;
        }

        String pickFragmentTag = getArguments().getString(ARGUMENT_FRAGMENT_TAG);
        String dateId = getArguments().getString(ARGUMENT_DATE_ID);
        DatePickerFragment.OnPickDateFragment fragmentPick = ((DatePickerFragment.OnPickDateFragment) getFragmentManager().findFragmentByTag(pickFragmentTag));
        if (fragmentPick == null || isCancelled) {
            return;
        }

        if (clearValue) {
            fragmentPick.onDateClear(dateId);
        } else {
            mCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, 0, 0, 0);
            TimePickerFragment.newInstance(dateId, pickFragmentTag, mCalendar).show(getFragmentManager(), TimePickerFragment.TAG);
//            } else {
//                fragmentPick.onDatePicked(dateId, new GregorianCalendar(year, monthOfYear, dayOfMonth, 0, 0, 0));
//            }
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (getArguments() == null || !getArguments().containsKey(ARGUMENT_FRAGMENT_TAG)) {
            return;
        }

        String pickFragmentTag = getArguments().getString(ARGUMENT_FRAGMENT_TAG);
        String dateId = getArguments().getString(ARGUMENT_DATE_ID);
        DatePickerFragment.OnPickDateFragment fragmentPick = ((DatePickerFragment.OnPickDateFragment) getFragmentManager().findFragmentByTag(pickFragmentTag));
        if (fragmentPick == null || isCancelled) {
            return;
        }

        if (clearValue) {
            fragmentPick.onDateClear(dateId);
        } else {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            fragmentPick.onDatePicked(dateId, mCalendar);
        }
    }
}

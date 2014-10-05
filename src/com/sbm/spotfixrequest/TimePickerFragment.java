package com.sbm.spotfixrequest;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import com.sbm.model.Spotfix;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private TheListener listener;

    public interface TheListener{
        public void returnTime(String time);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        listener = (TheListener) getActivity();

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
//        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
//                DateFormat.is24HourFormat(getActivity()));
//        return timePickerDialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        int defaultYear = c.get(Calendar.YEAR);
        int defaultDay = c.get(Calendar.DAY_OF_MONTH);
        c.set(defaultYear, Calendar.JANUARY, defaultDay, hourOfDay, minute, 0);

        SimpleDateFormat sdf = new SimpleDateFormat(Spotfix.DATE_FORMAT, Locale.US);
        String formattedTime = sdf.format(c.getTime());
        if (listener != null) {
            listener.returnTime(formattedTime);
        }
    }
}
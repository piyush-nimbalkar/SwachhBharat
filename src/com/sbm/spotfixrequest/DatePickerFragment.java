package com.sbm.spotfixrequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import com.sbm.model.Spotfix;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	private TheListener listener;

	public interface TheListener{
		public void returnDate(String date);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		listener = (TheListener) getActivity(); 
	
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat(Spotfix.DATE_FORMAT);
        String formattedDate = sdf.format(c.getTime());
		if (listener != null) {
			listener.returnDate(formattedDate); 
		}
	}
}
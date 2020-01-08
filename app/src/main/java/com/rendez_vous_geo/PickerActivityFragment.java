package com.rendez_vous_geo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

import androidx.fragment.app.DialogFragment;

/**
 * Classe utile pour l'activité de nouveau RDV
 * Permet de gérer les comportement du date et time picker
 *
 * @see NewRendezvousActivity
 */
public class PickerActivityFragment {

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Button time = getActivity().findViewById(R.id.time_picker);

            String formattedTime = String.format(Locale.FRANCE, "%02d:%02d", hourOfDay, minute);
            time.setText(formattedTime);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month + 1, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Button date = getActivity().findViewById(R.id.date_picker);

            String formattedDate = String.format(Locale.FRANCE, "%02d/%02d/%d", day, month, year);
            date.setText(formattedDate);
        }
    }

}

package com.mycompany.exchangerateapp.modal;

import com.github.mikephil.charting.formatter.ValueFormatter;

import org.threeten.bp.LocalDate;

public class DateValueFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        int epochDay = Math.round(value);
        LocalDate date = LocalDate.ofEpochDay(epochDay);
        return date.toString();
    }

}

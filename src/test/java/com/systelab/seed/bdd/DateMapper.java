package com.systelab.seed.bdd;

import cucumber.api.Transformer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateMapper extends Transformer<LocalDate> {

    @Override
    public LocalDate transform(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.US);
        return LocalDate.parse(date, formatter);
    }
}
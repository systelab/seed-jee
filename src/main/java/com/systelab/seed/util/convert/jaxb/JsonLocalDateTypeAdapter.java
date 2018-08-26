package com.systelab.seed.util.convert.jaxb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JsonLocalDateTypeAdapter extends XmlAdapter<String, LocalDate> {

    public static final String JSON_ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";


    @Override
    public LocalDate unmarshal(final String value) {
        return Optional.ofNullable(value)
                .map(v -> LocalDateTime.parse(v, DateTimeFormatter.ofPattern(JSON_ISO_DATE_FORMAT)).toLocalDate())
                .orElse(null);
    }

    @Override
    public String marshal(final LocalDate date) {
        return Optional.ofNullable(date)
                .map(dt -> date.atStartOfDay().format(DateTimeFormatter.ofPattern(JSON_ISO_DATE_FORMAT)))
                .orElse(null);
    }
}
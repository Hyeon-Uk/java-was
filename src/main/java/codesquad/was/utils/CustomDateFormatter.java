package codesquad.was.utils;

import codesquad.framework.coffee.annotation.Coffee;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Coffee
public class CustomDateFormatter {
    private final DateFormat dateFormat;
    public CustomDateFormatter() {
        dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    public String format(Date date) {
        return dateFormat.format(date);
    }
}

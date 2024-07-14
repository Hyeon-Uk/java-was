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
public class CustomDateFormatter extends SimpleDateFormat {
    private final DateFormat dateFormat;
    public CustomDateFormatter() {
        dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return this.dateFormat.format(date,toAppendTo,fieldPosition);
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        return dateFormat.parse(source,pos);
    }
}

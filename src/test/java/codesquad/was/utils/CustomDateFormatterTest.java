package codesquad.was.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomDateFormatterTest {

    private CustomDateFormatter customDateFormatter;
    private SimpleDateFormat referenceFormat;

    @BeforeEach
    public void setUp() {
        customDateFormatter = new CustomDateFormatter();
        referenceFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        referenceFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Test
    public void testFormat() throws ParseException {
        // given
        String dateString = "Fri, 25 Dec 2020 10:00:00 GMT";
        Date date = referenceFormat.parse(dateString);

        // when
        String formattedDate = customDateFormatter.format(date);

        // then
        assertEquals(dateString, formattedDate);
    }

    @Test
    public void testFormatWithDifferentDate() throws ParseException {
        // given
        String dateString = "Thu, 01 Jan 1970 00:00:00 GMT";
        Date date = referenceFormat.parse(dateString);

        // when
        String formattedDate = customDateFormatter.format(date);

        // then
        assertEquals(dateString, formattedDate);
    }

    @Test
    public void testFormatWithNullDate() {
        // given
        Date date = null;

        // when & then
        assertThrows(NullPointerException.class, () -> {
            customDateFormatter.format(date);
        });
    }
}

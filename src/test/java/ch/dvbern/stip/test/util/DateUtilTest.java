package ch.dvbern.stip.test.util;

import ch.dvbern.stip.api.common.util.DateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


public class DateUtilTest {

    private static final String MONTH_YEAR = "10.2023";
    DateUtil dateUtil = new DateUtil();

    @Test
    public void testDateToMonthYear() {
        LocalDate dateToTest = LocalDate.of(2023,10,1);
        String monthYear = dateUtil.DateToMonthYear(dateToTest);
        Assertions.assertEquals( MONTH_YEAR, monthYear);
    }

    @Test
    public void testMonthYearToBeginOfMonth() {
        LocalDate dateToTest = dateUtil.MonthYearToBeginOfMonth(MONTH_YEAR);
        Assertions.assertEquals(LocalDate.of(2023,10,1), dateToTest);
    }

    @Test
    public void testMonthYearToEndOfMonth() {
        LocalDate dateToTest = dateUtil.MonthYearToEndOfMonth(MONTH_YEAR);
        Assertions.assertEquals(LocalDate.of(2023,10,31), dateToTest);
    }
}

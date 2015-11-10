package gov.nysenate.ess.core.model.transaction;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.annotation.ProperTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Category(ProperTest.class)
public class TransactionRecordTests {

    private static final String testCol = "TESTCOL";
    private static final String testValue = "TESTVAL";
    private static final ImmutableMap<String, String> testValMap = ImmutableMap.of(testCol, testValue);

    @Test
    public void hasValuesTest() {
        TransactionRecord rec = new TransactionRecord();
        assertFalse(rec.hasValues());
        rec.setValueMap(ImmutableMap.of());
        assertFalse(rec.hasValues());
        rec.setValueMap(testValMap);
        assertTrue(rec.hasValues());
    }

    @Test
    public void hasNonNullValueTest() {
        TransactionRecord rec = new TransactionRecord();
        rec.setValueMap(ImmutableMap.of("not" + testCol, "not" + testValue));
        assertFalse(rec.hasNonNullValue(testCol));

        HashMap<String, String> nullValMap = new HashMap<>();
        nullValMap.put(testCol, null);
        rec.setValueMap(nullValMap);
        assertFalse(rec.hasNonNullValue(testCol));

        rec.setValueMap(testValMap);
        assertTrue(rec.hasNonNullValue(testCol));
    }

    @Test(expected = IllegalStateException.class)
    public void hasNonNullValueNoValMapTest() {
        TransactionRecord rec = new TransactionRecord();
        rec.hasNonNullValue(testCol);
    }

    @Test
    public void getValueTest() {
        TransactionRecord rec = new TransactionRecord();
        rec.setValueMap(testValMap);
        assertEquals(testValue, rec.getValue(testCol));
    }

    @Test(expected = IllegalStateException.class)
    public void getValueNoValMapTest() {
        TransactionRecord rec = new TransactionRecord();
        rec.getValue(testCol);
    }

    @Test
    public void getLocalDateValueTest() {
        LocalDate date = LocalDate.now();
        String dateVal = date.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")).toUpperCase();
        TransactionRecord rec = new TransactionRecord();
        Map<String, String> dateValMap = ImmutableMap.of(testCol, dateVal);
        rec.setValueMap(dateValMap);

        assertEquals(date, rec.getLocalDateValue(testCol));
    }

    @Test(expected = DateTimeParseException.class)
    public void getLocalDateValueTest_unparseable() {
        TransactionRecord rec = new TransactionRecord();
        Map<String, String> dateValMap = ImmutableMap.of(testCol, "not a date");
        rec.setValueMap(dateValMap);
        rec.getLocalDateValue(testCol);
    }

    @Test
    public void getBigDecimalValueTest() {
        BigDecimal value = BigDecimal.valueOf(3.14);
        Map<String, String> valMap = ImmutableMap.of(testCol, value.toString());
        TransactionRecord rec = new TransactionRecord();
        rec.setValueMap(valMap);
        assertEquals(value, rec.getBigDecimalValue(testCol));
    }
    
    @Test
    public void getBigDecimalValueTest_nullValue() {
        HashMap<String, String> valMap = new HashMap<>();
        valMap.put(testCol, null);
        TransactionRecord rec = new TransactionRecord();
        rec.setValueMap(valMap);

        assertEquals(BigDecimal.ZERO, rec.getBigDecimalValue(testCol));
        assertEquals(BigDecimal.ZERO, rec.getBigDecimalValue(testCol, true));
        assertNull(rec.getBigDecimalValue(testCol, false));
    }

    @Test(expected = NumberFormatException.class)
    public void getBigDecimalValueTest_unparseable() {
        HashMap<String, String> valMap = new HashMap<>();
        valMap.put(testCol, "NaN");
        TransactionRecord rec = new TransactionRecord();
        rec.setValueMap(valMap);
        rec.getBigDecimalValue(testCol);
    }

    @Test
    public void getValuesForCodeTest() {
        ImmutableMap<String, String> valMap = ImmutableMap.copyOf(
                TransactionCode.getAllDbColumnsList().stream()
                        .collect(Collectors.toMap(Function.identity(), colName -> colName + "-value")));

        for (TransactionCode code : TransactionCode.values()) {
            TransactionRecord rec = new TransactionRecord();
            rec.setTransCode(code);
            rec.setValueMap(valMap);
            Map<String, String> subMap = rec.getValuesForCode();
            Set<String> codeCols = code.getDbColumnList();
            assertEquals(codeCols, subMap.keySet());
            codeCols.forEach(colName -> assertEquals(colName + "-value", subMap.get(colName)));
        }
    }

    @Test
    public void getValuesForColsTest() {
        ImmutableMap<String, String> valMap = ImmutableMap.copyOf(
                TransactionCode.getAllDbColumnsList().stream()
                        .collect(Collectors.toMap(Function.identity(), colName -> colName + "-value")));

        for (TransactionCode code : TransactionCode.values()) {
            TransactionRecord rec = new TransactionRecord();
            rec.setTransCode(code);
            rec.setValueMap(valMap);
            Set<String> codeCols = code.getDbColumnList();
            Map<String, String> subMap = rec.getValuesForCols(codeCols);
            assertEquals(codeCols, subMap.keySet());
            codeCols.forEach(colName -> assertEquals(colName + "-value", subMap.get(colName)));
        }
    }

}

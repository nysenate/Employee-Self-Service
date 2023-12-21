package gov.nysenate.ess.core.util;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class OrderByTest
{
    @Test
    public void testGetSortColumns() {
        // Empty order by
        assertTrue(new OrderBy().getSortColumns().isEmpty());
        // Map Constructor
        ImmutableMap<String, SortOrder> map = ImmutableMap.of("col1", SortOrder.DESC, "col2", SortOrder.ASC);
        assertEquals(map, new OrderBy(map).getSortColumns());
        // Arg constructors
        assertEquals(map, new OrderBy("col1", SortOrder.DESC, "col2", SortOrder.ASC).getSortColumns());
    }
}
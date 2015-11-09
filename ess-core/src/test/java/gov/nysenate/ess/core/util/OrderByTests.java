package gov.nysenate.ess.core.util;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.annotation.ProperTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(ProperTest.class)
public class OrderByTests
{
    @Test
    public void testGetSortColumns() throws Exception {
        // Empty order by
        assertTrue(new OrderBy().getSortColumns().isEmpty());
        // Map Constructor
        ImmutableMap<String, SortOrder> map = ImmutableMap.of("col1", SortOrder.DESC, "col2", SortOrder.ASC);
        assertEquals(map, new OrderBy(map).getSortColumns());
        // Arg constructors
        assertEquals(map, new OrderBy("col1", SortOrder.DESC, "col2", SortOrder.ASC).getSortColumns());
    }
}
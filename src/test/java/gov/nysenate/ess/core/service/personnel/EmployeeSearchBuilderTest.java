package gov.nysenate.ess.core.service.personnel;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder.tokenizeSearchTerm;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class EmployeeSearchBuilderTest
{
    @Test
    public void tokenize_uppercasesAndSplitsOnWhitespace() {
        assertEquals(List.of("JOHN", "SMITH"), tokenizeSearchTerm("  John   Smith "));
    }

    @Test
    public void tokenize_removesHyphenWithoutSplitting() {
        assertEquals(List.of("ANDREA", "STEWARTCOUSINS"), tokenizeSearchTerm("Andrea Stewart-Cousins"));
    }

    @Test
    public void tokenize_removesApostropheWithoutSplitting() {
        // Replacing the apostrophe with a space would yield a lone "O" token matching nearly everyone.
        assertEquals(List.of("JOHN", "OBRIEN"), tokenizeSearchTerm("John O'Brien"));
    }

    @Test
    public void tokenize_removesPeriods() {
        assertEquals(List.of("JOHN", "R", "SMITH", "JR"), tokenizeSearchTerm("John R. Smith Jr."));
    }

    @Test
    public void tokenize_keepsDigitsAndFlattensEmail() {
        assertEquals(List.of("JSMITH2NYSENATEGOV"), tokenizeSearchTerm("jsmith2@nysenate.gov"));
    }

    @Test
    public void tokenize_punctuationOnlyTokensAreDropped() {
        assertEquals(List.of("A", "B"), tokenizeSearchTerm("a - b"));
    }

    @Test
    public void tokenize_blankOrNullTermHasNoTokens() {
        assertTrue(tokenizeSearchTerm(null).isEmpty());
        assertTrue(tokenizeSearchTerm("   ").isEmpty());
        assertTrue(tokenizeSearchTerm("'-.").isEmpty());
    }
}

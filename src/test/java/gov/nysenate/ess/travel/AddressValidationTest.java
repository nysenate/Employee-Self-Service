package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.maps.AddressValidationCtrl;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(UnitTest.class)
public class AddressValidationTest {

    private AddressValidationCtrl addressValidationCtrl = new AddressValidationCtrl();

    @Test
    public void completeValidAddress_true() {
        assert(addressValidationCtrl.returnValidationResult("100 South Swan St", "Albany", "NY"));
    }

    @Test
    public void completeInvalidAddress_false() {
        assert(!addressValidationCtrl.returnValidationResult("100000000 east nowhere", "timbuktu", "albania"));
    }

    @Test
    public void completeMisspelledAddress_false() {
        assert(!addressValidationCtrl.returnValidationResult("100 Sout Swain St", "Albanyy", "New York"));
    }

    @Test
    public void incompleteAddress_false() {
        assert(!addressValidationCtrl.returnValidationResult("515 Loudon Road", "", ""));
    }

    @Test
    public void missingAddress_false() {
        assert(!addressValidationCtrl.returnValidationResult("", "", ""));
    }
}
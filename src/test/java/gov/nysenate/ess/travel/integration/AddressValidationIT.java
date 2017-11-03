package gov.nysenate.ess.travel.integration;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.travel.addressvalidation.AddressValidationService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;


@Category(IntegrationTest.class)
public class AddressValidationIT extends BaseTest{

    @Autowired
    AddressValidationService addressValidationService;

    @Test
    public void completeValidAddress_SUCCESS() {
        assertEquals(addressValidationService.validateAddress("100 South Swan St", "Albany", "NY").getStatus(), "SUCCESS");
    }

    @Test
    public void completeInvalidAddress_INVALID_ADDRESS() {
        assertEquals(addressValidationService.validateAddress("100000000 east nowhere", "timbuktu", "albania").getStatus(), "NO_ADDRESS_VALIDATE_RESULT");
    }

    @Test
    public void completeMisspelledAddress_INVALID_ADDRESS() {
        assertEquals(addressValidationService.validateAddress("100 Sout Swain St", "Albanyy", "New York").getStatus(), "NO_ADDRESS_VALIDATE_RESULT");
    }

    @Test
    public void incompleteAddress_INSUFFICIENT_ADDRESS() {
        assertEquals(addressValidationService.validateAddress("515 Loudon Road", "", "").getStatus(), "INSUFFICIENT_ADDRESS");
    }

    @Test
    public void missingAddress_MISSING_ADDRESS() {
        assertEquals(addressValidationService.validateAddress("", "", "").getStatus(), "MISSING_ADDRESS");
    }
}
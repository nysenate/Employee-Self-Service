package gov.nysenate.ess.core.dao.emergency_notification;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfo;
import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfoNotFound;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
@Transactional(value = DatabaseConfig.localTxManager)
public class EmergencyNotificationInfoDaoIT extends BaseTest {

    @Autowired private EmergencyNotificationInfoDao emergencyNotificationInfoDao;

    private static final int impossibleEmpId = -37;
    private static final int improbableEmpId = 999999931;

    @Test(expected = EmergencyNotificationInfoNotFound.class)
    public void getNonExistentEmergencyNotificationInfo() throws Exception {
        emergencyNotificationInfoDao.getEmergencyNotificationInfo(impossibleEmpId);
    }

    @Test
    @Transactional(value = DatabaseConfig.localTxManager)
    public void getAllEmergencyNotificationInfo() throws Exception {
        List<EmergencyNotificationInfo> initialENIList = emergencyNotificationInfoDao.getAllEmergencyNotificationInfo();

        Set<EmergencyNotificationInfo> initialENISet = new HashSet<>(initialENIList);
        Set<Integer> initialIdSet = initialENISet.stream().map(EmergencyNotificationInfo::getEmpId).collect(Collectors.toSet());

        assertEquals("Returned emergency notification info are unique", initialENIList.size(), initialENISet.size());
        assertEquals("Returned ENI have unique emp ids", initialIdSet.size(), initialENISet.size());

        Set<EmergencyNotificationInfo> newENIs = Sets.newHashSet(
                makeRandomENI(impossibleEmpId),
                makeRandomENI(improbableEmpId));

        Set<Integer> newEmpIdSet = newENIs.stream().map(EmergencyNotificationInfo::getEmpId).collect(Collectors.toSet());

        assertEquals("New ENI have unique ids", newENIs.size(), newEmpIdSet.size());
        assertTrue("New ENI are new", Sets.intersection(initialIdSet, newEmpIdSet).isEmpty());

        newENIs.forEach(emergencyNotificationInfoDao::updateEmergencyNotificationInfo);

        List<EmergencyNotificationInfo> updatedENIList = emergencyNotificationInfoDao.getAllEmergencyNotificationInfo();
        Set<EmergencyNotificationInfo> updatedENISet = new HashSet<>(updatedENIList);

        assertEquals("Updated ENI list is unique", newENIs.size(), newEmpIdSet.size());

        int expectedSize = initialENISet.size() + newENIs.size();

        assertEquals("Updated ENI set is of expected size", expectedSize, updatedENISet.size());

        assertEquals(Sets.intersection(updatedENISet, newENIs), newENIs);
    }

    @Test
    @Transactional(value = DatabaseConfig.localTxManager)
    public void updateEmergencyNotificationInfo() throws Exception {
        int empId = improbableEmpId;
        EmergencyNotificationInfo firstENI = makeRandomENI(empId);
        EmergencyNotificationInfo secondENI = makeRandomENI(empId);

        assertNotEquals(firstENI, secondENI);
        assertEquals(firstENI.getEmpId(), secondENI.getEmpId());

        emergencyNotificationInfoDao.updateEmergencyNotificationInfo(firstENI);
        EmergencyNotificationInfo eni = emergencyNotificationInfoDao.getEmergencyNotificationInfo(empId);

        assertEquals(eni, firstENI);

        emergencyNotificationInfoDao.updateEmergencyNotificationInfo(secondENI);
        eni = emergencyNotificationInfoDao.getEmergencyNotificationInfo(empId);

        assertEquals(eni, secondENI);
    }

    /* --- Internal Methods --- */

    private EmergencyNotificationInfo makeRandomENI(int empId) {
        Random random = new Random();
        Supplier<String> numberGenerator = () -> Integer.toString(random.nextInt());
        return EmergencyNotificationInfo.builder()
                .setEmpId(empId)
                .setHomePhone(numberGenerator.get())
                .setMobilePhone(numberGenerator.get())
                .setAlternatePhone(numberGenerator.get())
                .setSmsSubscribed(random.nextBoolean())
                .setPersonalEmail(numberGenerator.get())
                .setAlternateEmail(numberGenerator.get())
                .build();
    }


}
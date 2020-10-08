package gov.nysenate.ess.core.dao.alert;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.AlertInfoNotFound;
import gov.nysenate.ess.core.model.alert.ContactOptions;
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
public class AlertInfoDaoIT extends BaseTest {

    @Autowired private AlertInfoDao alertInfoDao;

    private static final int impossibleEmpId = -37;
    private static final int improbableEmpId = 999999931;

    @Test(expected = AlertInfoNotFound.class)
    public void getNonExistentAlertInfo() {
        alertInfoDao.getAlertInfo(impossibleEmpId);
    }

    @Test
    @Transactional(value = DatabaseConfig.localTxManager)
    public void getAllAlertInfo() {
        List<AlertInfo> initialAlertInfoList = alertInfoDao.getAllAlertInfo();
        Set<AlertInfo> initialAlertInfoSet = new HashSet<>(initialAlertInfoList);
        Set<Integer> initialIdSet = initialAlertInfoSet.stream().map(AlertInfo::getEmpId).collect(Collectors.toSet());
        assertEquals("Returned alert info are unique", initialAlertInfoList.size(), initialAlertInfoSet.size());
        assertEquals("Returned AlertInfo have unique emp ids", initialIdSet.size(), initialAlertInfoSet.size());

        Set<AlertInfo> newAlertInfos = Sets.newHashSet(
                makeRandomAlertInfo(impossibleEmpId),
                makeRandomAlertInfo(improbableEmpId));
        Set<Integer> newEmpIdSet = newAlertInfos.stream().map(AlertInfo::getEmpId).collect(Collectors.toSet());
        assertEquals("New AlertInfo have unique ids", newAlertInfos.size(), newEmpIdSet.size());
        assertTrue("New AlertInfo are new", Sets.intersection(initialIdSet, newEmpIdSet).isEmpty());
        newAlertInfos.forEach(alertInfoDao::updateAlertInfo);

        List<AlertInfo> updatedAlertInfoList = alertInfoDao.getAllAlertInfo();
        Set<AlertInfo> updatedAlertInfoSet = new HashSet<>(updatedAlertInfoList);
        assertEquals("Updated AlertInfo list is unique", newAlertInfos.size(), newEmpIdSet.size());
        int expectedSize = initialAlertInfoSet.size() + newAlertInfos.size();
        assertEquals("Updated AlertInfo set is of expected size", expectedSize, updatedAlertInfoSet.size());
        assertEquals(Sets.intersection(updatedAlertInfoSet, newAlertInfos), newAlertInfos);
    }

    @Test
    @Transactional(value = DatabaseConfig.localTxManager)
    public void updateAlertInfo() {
        int empId = improbableEmpId;
        AlertInfo firstAlertInfo = makeRandomAlertInfo(empId);
        AlertInfo secondAlertInfo = makeRandomAlertInfo(empId);
        assertNotEquals(firstAlertInfo, secondAlertInfo);
        assertEquals(firstAlertInfo.getEmpId(), secondAlertInfo.getEmpId());
        alertInfoDao.updateAlertInfo(firstAlertInfo);
        AlertInfo alertInfo = alertInfoDao.getAlertInfo(empId);
        assertEquals(alertInfo, firstAlertInfo);
        alertInfoDao.updateAlertInfo(secondAlertInfo);
        alertInfo = alertInfoDao.getAlertInfo(empId);
        assertEquals(alertInfo, secondAlertInfo);
    }

    /* --- Internal Methods --- */

    private AlertInfo makeRandomAlertInfo(int empId) {
        Random random = new Random();
        Supplier<String> numberGenerator = () -> Integer.toString(random.nextInt());
        return AlertInfo.builder()
                .setEmpId(empId)
                .setHomePhone(numberGenerator.get())
                .setMobilePhone(numberGenerator.get())
                .setAlternatePhone(numberGenerator.get())
                .setMobileOptions(ContactOptions.values()[random.nextInt(ContactOptions.values().length)])
                .setAlternateOptions(ContactOptions.values()[random.nextInt(ContactOptions.values().length)])
                .setPersonalEmail(numberGenerator.get())
                .setAlternateEmail(numberGenerator.get())
                .build();
    }
}

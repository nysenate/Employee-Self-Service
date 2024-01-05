package gov.nysenate.ess.time.model.payroll;

import gov.nysenate.ess.core.util.OutputUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Miscellaneous leave time must be accompanied by a code representing the type of leave.
 * This is an enumeration of all those various codes with condensed and full descriptions.
 */
public enum MiscLeaveType
{
    ADMINISTRATIVE_LEAVE("A", "Administrative Leave", "Administrative Leave", true, new BigInteger("307455737086830401923262932905351643162")),
    BEREAVEMENT_LEAVE("B","Bereavement Leave", "Bereavement Leave", false, new BigInteger("191601132010624858613098950383796367496")),
    BLOOD_DONATION("D", "Blood Donation", "Blood Donation Leave", false, new BigInteger("191601132010628485390557794271320486024")),
    BRST_PROST_CANCER_SCREENING("C", "Cancer Screening", "Breast and Prostate Cancer Screening Leave", false, new BigInteger("191601132010620022909820491867097542792")),
    EXTENDED_SICK_LEAVE("X", "Extended Sick Leave", "Extended Sick Leave", false, new BigInteger("191601132010617605058181262608748130440")),
    EXTRAORDINARY_LEAVE("E", "Extraordinary Leave", "Leave for Extraordinary Circumstances", false, new BigInteger("191601132010623649687279335754621661320")),
    FLOATING_HOLIDAY("F", "Floating Holiday", "Leave for Floating Holiday", false, new BigInteger("253666602241845525420608059722204643652")),
    JURY_LEAVE("J", "Jury Leave", "Jury Leave", false, new BigInteger("191601132010621231835640106496272248968")),
    LEAVE_DONATION("L", "Leave Donation", "Leave Donation", true, new BigInteger("253666602241845525420608059722204643693")),
    MILITARY_LEAVE("M", "Military Leave", "Military Leave", false, new BigInteger("191601132010626067538918565012971073672")),
    OTHER_LEAVE("O", "Other - 1", "Other Leave - 1", false, new BigInteger("191601132010627276464738179642145779848")),
    PARENTAL_LEAVE("P", "Parental Leave", "Parental Leave", false, new BigInteger("191601132010616396132361647979573424264")),
    SICK_LEAVE_HALF_PAY("S", "Sick Leave with Half Pay", "Sick Leave with Half Pay", false, new BigInteger("191601132010622440761459721125446955144")),
    VOL_FIRE_EMERG_MED_ACTIVITY("V", "Volunteer Fire/EMT", "Volunteer Fire Fighting and Emergency Medical Activities Leave", false, new BigInteger("191601132010629694316377408900495192200")),
    VOTING_LEAVE("T", "Voting", "Voting Leave", false, new BigInteger("186214696577130514392638601662177149240")),
    WITNESS_LEAVE("W", "Witness Leave", "Witness Leave", false, new BigInteger("191601132010618813984000877237922836616"));

    private final String code;
    private final String shortName;
    private final String fullName;
    /** If true, this leave type requires permission to use */
    private final boolean restricted;
    private final BigInteger miscLeaveId;

    MiscLeaveType(String code, String shortName, String fullName, boolean restricted, BigInteger miscLeaveId) {
        this.code = code;
        this.shortName = shortName;
        this.fullName = fullName;
        this.restricted = restricted;
        this.miscLeaveId = miscLeaveId;
    }

    public static MiscLeaveType valueOfCode(String code) {
        for (MiscLeaveType leaveType : MiscLeaveType.values()) {
            if (leaveType.code.equals(code)) {
                return leaveType;
            }
        }
        return null;
    }

    public static MiscLeaveType valueOfId(BigInteger miscLeaveId){
        for (MiscLeaveType leaveType : MiscLeaveType.values()) {
            if (leaveType.miscLeaveId.equals(miscLeaveId)) {
                return leaveType;
            }
        }
        return null;
    }

    private static class MiscLeaveTypeView
    {
        private MiscLeaveType type;
        private String shortName;
        private String fullName;
        private boolean restricted;

        MiscLeaveTypeView(MiscLeaveType type) {
            this.type = type;
            this.shortName = type.getShortName();
            this.fullName = type.getFullName();
            this.restricted = type.isRestricted();
        }

        public MiscLeaveType getType() {
            return type;
        }

        public String getShortName() {
            return shortName;
        }

        public String getFullName() {
            return fullName;
        }

        public boolean isRestricted() {
            return restricted;
        }
    }

    public static String getJsonLabels() {
        return OutputUtils.toJson(
                Arrays.stream(MiscLeaveType.values())
                        .map(MiscLeaveTypeView::new)
                        .collect(Collectors.toList()));
    }

    public String getCode() {
        return code;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public BigInteger getMiscLeaveId() {
        return miscLeaveId;
    }
}

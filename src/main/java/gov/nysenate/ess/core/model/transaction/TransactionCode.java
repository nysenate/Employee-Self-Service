package gov.nysenate.ess.core.model.transaction;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The TransactionCode refers to a specific change made to an employee's record.
 * The dbColumns stored for each code refer to the set of columns in the SFMS audit
 * table where the relevant changes are stored.
 */
public enum TransactionCode
{
    ACC(TransactionType.PER, "CDACCRUE", "ACCRUE"),
    ACT(TransactionType.PER, "DTAPPOINTFRM, DTAPPOINTTO", "ACCRUAL DATES"),
    ADT(TransactionType.PER, "DTPEREMPFRM, DTPEREMPTO", "EMPLOYMENT DATES"),
    AGY(TransactionType.PAY, "CDAGENCY", "AGENCY CODE"),
    ALL(TransactionType.PAY, "", "ACTIVE NON-TEMP EMPLOYEES"),
    APP(TransactionType.PER, "", "APPOINTMENT"),
    CAT(TransactionType.PAY, "MOADDCITYTAX", "ADDITIONAL NYC WITHHELD"),
    CHK(TransactionType.PAY, "CDCERTADD, ADSTREET1C, ADSTREET2C, ADSTREET3C, ADSTATEC, ADCITYC, ADZIPCODEC", "CHECK MAILING ADDRESS"),
    CMS(TransactionType.PAY, "CDMARITALNYC", "NYC MARITAL STATUS"),
    CNU(TransactionType.PER, "CDNEGUNIT", "NEGOTIATING UNIT"),
    CUA(TransactionType.PAY, "MOCREDUN", "CREDIT UNION AMOUNT"),
    CUC(TransactionType.PAY, "CDCREDUN", "CREDIT UNION CODE"),
    CWT(TransactionType.PAY, "NUCITYTAXEX", "NYC WITHHOLDING EXEMP"),
    DDF(TransactionType.PAY, "CDDIRECTDEPF", "DIRECT DEPOSIT FULL"),
    DDP(TransactionType.PAY, "CDDIRECTDEPP", "DIRECT DEPOSIT PARTIAL"),
    DEF(TransactionType.PER, "CDDEFLAG, DTDEFLAG", "DEFERRED LAG"),
    DIS(TransactionType.PAY, "CDCHKDIST", "CHECK DISTRIBUTION CODE"),
    DOB(TransactionType.PER, "DTBIRTH", "DATE OF BIRTH"),
    DUA(TransactionType.PER, "CDDUALEMP", "DUAL EMPLOYEE"),
    EEV(TransactionType.PER, "DTI9REC", "I9 RECEIVED DATE"),
    EMP(TransactionType.PER, "CDEMPSTATUS", "TERMINATION"),
    ENC(TransactionType.PAY, "DTLEAVEEND", "ENCUMBERING LEAVE"),
    EXC(TransactionType.PAY, "MOAMTEXCEED", "NOT TO EXCEED AMOUNT"),
    FAT(TransactionType.PAY, "MOADDFEDTAX", "ADDITION FEDERAL WITHHELD"),
    FMS(TransactionType.PAY, "CDMARITALFED", "FEDERAL MARITAL STATUS"),
    FWT(TransactionType.PAY, "NUFEDTAXEX", "FEDERAL WITHHOLDING EXEMP"),
    HWT(TransactionType.PAY, "DTENDTE, NUHRHRSPD, MOHRHRSPD, DEHRDAYSPD", "HOURLY WORK PAID"),
    IDC(TransactionType.PER, "CDIDCARD", "ID CARD"),
    LEG(TransactionType.PER, "ADSTREET1, ADSTREET2, ADCITY, ADSTATE, ADZIPCODE, ADCOUNTY", "LEGAL ADDRESS"),
    LIM(TransactionType.PER, "DELIMITATION", "EMPLOYMENT LIMITATION"),
    LIN(TransactionType.PAY, "NULINE", "LINE NUMBER"),
    LOC(TransactionType.PER, "CDLOCAT", "WORK LOCATION"),
    LTD(TransactionType.PAY, "NUDAYSLOST, MOLOSTTIME", "LOST TIME DAYS"),
    MAR(TransactionType.PER, "CDMARITAL", "MARITAL STATUS"),
    MIN(TransactionType.PER, "NUMINTOTEND, NUMINTOTHRS", "MINIMUM TOTAL HOURS"),
    NAM(TransactionType.PER, "NALAST, NAFIRST, NAMIDINIT", "NAME"),
    NAT(TransactionType.PER, "NATITLE", "NAME"),
    OED(TransactionType.PER, "DTORGEMPLOY", "ORIGINAL EMPLOYMENT DATE"),
    PHO(TransactionType.PER, "ADPHONENUM, ADPHONENUMW", "PHONE"),
    PLY(TransactionType.PAY, "", "EMPLOYER"),
    POS(TransactionType.PAY, "NUPOSITION", "POSITION NUM"),
    PRA(TransactionType.PER, "DTAGENCYTERM, CDAGENCYPR, DTTRANMEMREC", "PREVIOUS AGENCY"),
    PRS(TransactionType.PER, "", "PRESS LIST"),
    PYA(TransactionType.PAY, "MOPRIORYRTE", "TE PRIOR YEAR AMOUNT"),
    RHD(TransactionType.PER, "DTCONTSERV", "REHIRE DATE"),
    RSH(TransactionType.PAY, "CDRESPCTRHD, CDRESPCTR", "RESP. CENTER HEAD CODE"),   /** TODO: Fix this.. */
    RTP(TransactionType.PER, "", "RE-APPOINTMENT"),
    SAL(TransactionType.PAY, "MOSALBIWKLY", "BIWEEKLY/HOURLY RATE"),
    SAT(TransactionType.PAY, "MOADDSTATTAX", "ADDITIONAL STATE WITHHELD"),
    SEX(TransactionType.PER, "CDSEX", "SEX CODE"),
    SMS(TransactionType.PAY, "CDMARITALST", "STATE MARITAL STATUS"),
    SPE(TransactionType.PER, "CDSTATPER", "PERSONNEL STATUS"),
    SUP(TransactionType.PER, "NAFIRSTSUP, NALASTSUP, NUXREFSV", "ATTENDANCE SUPERVISOR"),
    SWT(TransactionType.PAY, "NUSTATTAXEX", "STATE WITHHOLDING EXEMP"),
    TAX(TransactionType.PAY, "", "TAX"),
    THP(TransactionType.PAY, "MOTOTHRSPD", "TOTAL HOURS PAID"),
    TLE(TransactionType.PER, "CDEMPTITLE", "EMPLOYEE TITLE CODE"),
    TLT(TransactionType.PAY, "DETIMELOST, DEDAYSLOST", "LOST TIME"),
    TXN(TransactionType.PAY, "DETXNNOTEPAY", "PAYROLL TRANSACTION NOTE"),
    TYP(TransactionType.PAY, "CDPAYTYPE", "PAYROLL TYPE"),
    VHL(TransactionType.PER, "NUVACHRSLF", "VACATION HRS PD LIFE TIME"),
    VHP(TransactionType.PER, "NUVACHRSPD", "VACATION HOURS PAID"),
    VMP(TransactionType.PAY, "MOVACHRSPD", "VACATION MONEY PAID"),
    WDT(TransactionType.PER, "DTW4SIGNED", "W4 SIGN DATE"),
    YAT(TransactionType.PAY, "MOADDYONTAX", "ADDITIONAL YON WITHHELD"),
    YMS(TransactionType.PAY, "CDMARITALYON", "YONKERS MARITAL STATUS"),
    YWT(TransactionType.PAY, "NUYONTAXEX", "YONKERS WITHHOLDING EXEMP");

    /** The type of transaction, e.g. Payroll, Personnel */
    private TransactionType type;

    /** The SFMS database columns that this transaction affects. */
    private String dbColumns;

    /** Description of the transaction. */
    private String desc;

    /** --- Constructors --- */

    private TransactionCode(TransactionType type, String dbColumns, String desc) {
        this.type = type;
        this.dbColumns = dbColumns;
        this.desc = desc;
    }

    public static Set<TransactionCode> getAll() {
        return EnumSet.allOf(TransactionCode.class);
    }

    /** --- Methods --- */

    public boolean isAppointType() {
        return this.equals(APP) || this.equals(RTP);
    }

    public boolean usesAllColumns() {
        return this.isAppointType();
    }

    /** --- Functional Getters/Setters --- */


    public Set<String> getDbColumnList() {
        return (this.isAppointType())
                ? getTypeDbColumnsList(type)
                : Arrays.stream(StringUtils.split(dbColumns, ","))
                        .map(String::trim)
                        .collect(Collectors.toSet());
    }

    public static Set<String> getAllDbColumnsList() {
        Set<String> columns = new HashSet<>();
        for (TransactionCode t : TransactionCode.values()) {
            if (!t.usesAllColumns()) {
                columns.addAll(t.getDbColumnList());
            }
        }
        return columns;
    }

    public static Set<String> getTypeDbColumnsList(TransactionType type) {
        return getTransactionsOfType(type).stream()
                .filter(code -> !code.usesAllColumns())
                .flatMap(code -> code.getDbColumnList().stream())
                .collect(Collectors.toSet());
    }

    public static Set<TransactionCode> getTransactionsOfType(TransactionType type) {
        return Arrays.stream(TransactionCode.values())
                .filter(code -> code.type == type)
                .collect(Collectors.toSet());
    }

    /** --- Basic Getters/Setters --- */

    public TransactionType getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
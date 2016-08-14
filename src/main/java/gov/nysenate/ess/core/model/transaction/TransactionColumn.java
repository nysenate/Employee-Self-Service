package gov.nysenate.ess.core.model.transaction;

public enum TransactionColumn
{
    ADCITY("City"),
    ADCITYC("City Code"),
    ADCOUNTY("County"),
    ADPHONENUM("Phone Number"),
    ADPHONENUMW("Work Phone Number"),
    ADSTATE("State"),
    ADSTATEC("State Code"),
    ADSTREET1("Address Line 1"),
    ADSTREET1C(""),
    ADSTREET2("Address Line 2"),
    ADSTREET2C(""),
    ADSTREET3C("Adress Line 3"),
    ADZIPCODE("Zip Code"),
    ADZIPCODEC(""),
    CDACCRUE("Accruals active"),
    CDAGENCY("Agency Code"),
    CDAGENCYPR(""),
    CDCERTADD(""),
    CDCHKDIST(""),
    CDCREDUN(""),
    CDDEFLAG(""),
    CDDIRECTDEPF("Full Direct Deposit"),
    CDDIRECTDEPP("Partial Direct Deposit"),
    CDDUALEMP("Dual Employment Status"),
    CDEMPSTATUS("Employment Status"),
    CDEMPTITLE("Employee Title"),
    CDIDCARD("ID Card"),
    CDLOCAT("Location Code"),
    CDMARITAL("Marital Status"),
    CDMARITALFED("Federal Marital Status"),
    CDMARITALNYC("Federal Marital NYC Status"),
    CDMARITALST("Marital Status"),
    CDMARITALYON(""),
    CDNEGUNIT("Negotiation Unit"),
    CDPAYTYPE("Pay Type"),
    CDRESPCTR("Responsibility Center Code"),
    CDRESPCTRHD("Responsibility Head Code"),
    CDSEX("Sex"),
    CDSTATPER("Personnel Status"),
    DEDAYSLOST(""),
    DEHRDAYSPD(""),
    DELIMITATION("Limitation"),
    DETIMELOST(""),
    DETXNNOTEPAY("Payroll Notice"),
    DTAGENCYTERM("Date Terminated from Agency"),
    DTAPPOINTFRM("Appointed From"),
    DTAPPOINTTO("Appointed To"),
    DTBIRTH("Date of Birth"),
    DTCONTSERV("Continuous Service Since"),
    DTDEFLAG(""),
    DTENDTE(""),
    DTI9REC(""),
    DTLEAVEEND(""),
    DTORGEMPLOY(""),
    DTPEREMPFRM(""),
    DTPEREMPTO(""),
    DTTRANMEMREC(""),
    DTW4SIGNED("W4 Signed"),
    MOADDCITYTAX(""),
    MOADDFEDTAX(""),
    MOADDSTATTAX(""),
    MOADDYONTAX(""),
    MOAMTEXCEED(""),
    MOCREDUN(""),
    MOHRHRSPD(""),
    MOLOSTTIME(""),
    MOPRIORYRTE("TE Pay for Previous Year"),
    MOSALBIWKLY("Bi-weekly Salary"),
    MOTOTHRSPD("Total Money Paid"),
    MOVACHRSPD("Vacation Money Paid"),
    NAFIRST("First Name"),
    NAFIRSTSUP("Supervisor's First Name"),
    NALAST("Last Name"),
    NALASTSUP("Supervisor's Last Name"),
    NAMIDINIT("Middle Initial"),
    NATITLE("Title"),
    NUCITYTAXEX(""),
    NUDAYSLOST(""),
    NUFEDTAXEX("Federal Tax Exemptions"),
    NUHRHRSPD(""),
    NULINE("Line Number"),
    NUMINTOTEND("Minimum Hours Remaining"),
    NUMINTOTHRS("Minimum Hours Total"),
    NUPOSITION("Position Number"),
    NUSTATTAXEX("State Exemptions"),
    NUVACHRSLF(""),
    NUVACHRSPD("Vacation Hours Paid"),
    NUXREFSV("Supervisor Id"),
    NUYONTAXEX("");
    
    private String desc;

    TransactionColumn(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isValidColumn(String column) {
        try {
            TransactionColumn.valueOf(column);
            return true;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }
}

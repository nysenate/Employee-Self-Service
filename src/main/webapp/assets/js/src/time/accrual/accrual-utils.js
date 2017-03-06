angular.module('essTime')
    .service('AccrualUtils', accrualUtils);

function accrualUtils () {
    
    return {
        isFirstRecordOfYear: isFirstRecordOfYear,
        getAccrualReportURL: getAccrualReportURL

    };

    /**
     * Returns true iff the given record is the first record of its year
     * @param record
     * @returns {boolean}
     */
    function isFirstRecordOfYear(record) {
        var beginDate = moment(record.payPeriod.startDate);
        return beginDate.month() === 0 && beginDate.date() === 1;
    }

    /**
     * Returns a given url to run the Biweely Accrual Report.
     * @param record
     * @returns {string}
     */
    function getAccrualReportURL (record) {

        var url = "http://nysasprd.senate.state.ny.us:7778/reports/rwservlet?report=PRBSTS23&cmdkey=tsuser"
                +"&p_nuxrefem=" + record.empId
                +"&p_dtend=" + moment(record.payPeriod.endDate).format ('DD-MMM-YYYY')
                ;

        if (record.computed) {
            url = url +
                "&p_dtbegin=" + moment(record.payPeriod.startDate).format('DD-MMM-YYYY') +
                "&p_datafrom=AUTO" +
                "&p_nubiwsicrate=" + record.sickRate +
                "&p_nubiwvacrate=" + record.vacationRate +
                "&p_nuemphrs=" + (record.biweekSickEmpUsed || 0) +
                "&p_nuemphrsacc=" + record.sickAccruedYtd +
                "&p_nuemphrsbsd=" + record.sickBanked +
                "&p_nuemphrsuse=" + record.sickEmpUsed +
                "&p_nufamhrs=" + (record.biweekSickFamUsed || 0) +
                "&p_nufamhrsuse=" + record.sickFamUsed +
                "&p_nuholhrs=" + record.biweekHolidayUsed +
                "&p_nuhrsexpect=" + record.serviceYtdExpected +
                "&p_nuperhrs=" + (record.biweekPersonalUsed || 0) +
                "&p_nuperhrsacc=" + record.personalAccruedYtd +
                "&p_nuperhrsuse=" + record.personalUsed +
                "&p_nutotalhrs=" + record.totalHoursYtd +
                // Total Hours YTD was passed instead, set to 0 since these hours are added in the report
                "&p_nutothrslast=" + 0 +
                "&p_nutrvhrs=" + record.biweekTravelUsed +
                "&p_nuvachrs=" + (record.biweekVacationUsed || 0) +
                "&p_nuvachrsacc=" + record.vacationAccruedYtd +
                "&p_nuvachrsbsd=" + record.vacationBanked +
                "&p_nuvachrsuse=" + record.vacationUsed +
                // If Report cannot find work hours from saved attendance or timesheet record,
                // then work hours should be 0
                "&p_nuworkhrs=" + "00" +
                (record.submitted ? "" : "&p_proj=Y")
            ;
        }

        return url;
    }

}
angular.module('essTime')
    .service('AccrualUtils', accrualUtils);

function accrualUtils () {
    
    return {
        computeDeltas: computeDeltas,
        isFirstRecordOfYear: isFirstRecordOfYear,
        getAccrualReportURL: getAccrualReportURL

    };

    /**
     * Compute the hours used during the given pay periods based on the change in the YTD usage.
     * @param accruals
     */
    function computeDeltas (accruals) {
        for (var i = 0; i < accruals.length; i++) {
            var currSummary = accruals[i];
            if (i == 0 || isFirstRecordOfYear(currSummary)) {
                currSummary.vacationUsedDelta = currSummary.vacationUsed;
                currSummary.personalUsedDelta = currSummary.personalUsed;
                currSummary.sickEmpUsedDelta = currSummary.sickEmpUsed;
                currSummary.sickFamUsedDelta = currSummary.sickFamUsed;
            }
            else {
                var prevSummary = accruals[i - 1];
                currSummary.vacationUsedDelta = currSummary.vacationUsed - prevSummary.vacationUsed;
                currSummary.personalUsedDelta = currSummary.personalUsed - prevSummary.personalUsed;
                currSummary.sickEmpUsedDelta = currSummary.sickEmpUsed - prevSummary.sickEmpUsed;
                currSummary.sickFamUsedDelta = currSummary.sickFamUsed - prevSummary.sickFamUsed;
            }
        }
    }

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
    function getAccrualReportURL (record, projection) {
        var projectionParam  = "";

        var sickRate, vacationRate, empHrs, empHrsacc, nuemphrsbsd, nuemphrsuse, nufamhrs, nufamhrsuse, nuholhrs;

        if (projection && projection=='P') {
            projectionParam = "&p_proj=Y";
        }

        var url = "http://nysasprd.senate.state.ny.us:7778/reports/rwservlet?report=PRBSTS23&cmdkey=tsuser"
                +"&p_dtend=" + moment(record.payPeriod.endDate).format ('DD-MMM-YYYY')
                +"&p_dtbegin=" + moment(record.payPeriod.startDate).format ('DD-MMM-YYYY')
                +"&p_nuxrefem=" + record.empId
                +"&p_datafrom=AUTO"
                +"&p_nubiwsicrate=" + record.sickRate
                +"&p_nubiwvacrate=" + record.vacationRate
                +"&p_nuemphrs=" + record.biweekSickEmpUsed
                +"&p_nuemphrsacc=" + record.sickAccruedYtd
                +"&p_nuemphrsbsd=" + record.sickBanked
                +"&p_nuemphrsuse=" + record.sickEmpUsed
                +"&p_nufamhrs=" + record.biweekSickFamUsed
                +"&p_nufamhrsuse="  + record.sickFamUsed
                +"&p_nuholhrs="  + record.biweekHolidayUsed
                +"&p_nuhrsexpect=" + record.serviceYtdExpected
                +"&p_nuperhrs="  + record.biweekPersonalUsed
                +"&p_nuperhrsacc=" + record.personalAccruedYtd
                +"&p_nuperhrsuse=" + record.personalUsed
                +"&p_nutotalhrs=" + record.totalHoursYtd
                +"&p_nutothrslast=" +  record.zeroHours /** --  Total Hours YTD was passed instead, set to 0 since these hours are added in the report  **/
                +"&p_nutrvhrs=" +  record.biweekTravelUsed
                +"&p_nuvachrs=" + record.biweekVacationUsed
                +"&p_nuvachrsacc=" + record.vacationAccruedYtd
                +"&p_nuvachrsbsd=" + record.vacationBanked
                +"&p_nuvachrsuse=" + record.vacationUsed
                +"&p_nuworkhrs=" + "00"  /** If Report cannot find work hours from saved attendance or timesheet record, then work hours should be 0 **/
                + projectionParam
                ;

        return url;
    }

}
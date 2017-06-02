var essTime = angular.module('essTime');

essTime.directive('accrualProjections', ['$timeout', '$rootScope', 'appProps',
                                        'AccrualHistoryApi', 'EmpInfoApi',
                                        'modals', 'AccrualUtils',
                                        accrualProjectionDirective]);

function accrualProjectionDirective($timeout, $rootScope, appProps, AccrualHistoryApi, EmpInfoApi, modals, accrualUtils) {
    return {
        scope: {
            /**
             *  An optional employee sup info
             *  If this is present, then accruals will be displayed for the corresponding employee
             *    for the appropriate dates.
             *  Otherwise, accruals will be displayed for the logged in user
             */
            empSupInfo: '=?'
        },
        templateUrl: appProps.ctxPath + '/template/time/accrual/projections-directive',
        link: function ($scope, $elem, $attrs) {

            var maxVacationBanked = 210,
                maxSickBanked = 1400;

            $scope.projections = [];
            $scope.accSummaries = [];
            $scope.selectedYear = null;
            $scope.empInfo = null;
            $scope.isTe = false;

            $scope.error = null;
            $scope.request = {
                empInfo: false,
                accSummaries: false
            };

            $scope.floatTheadOpts = {
                scrollingTop: 47,
                useAbsolutePositioning: false
            };

            $scope.floatTheadEnabled = true;

            $scope.hideTitle = $attrs.hideTitle === 'true';

            /* --- Watches --- */

            /** Watch the bound employee sup info and set empId when it changes */
            $scope.$watchCollection('empSupInfo', setEmpId);

            /** When a new empId is selected, refresh employee info and accrual summaries */
            $scope.$watch('empId', getEmpInfo);
            $scope.$watch('empId', getAccSummaries);

            $rootScope.$on('reflowEvent', reflowTable);

            /** Disable the floating table header for printing */
            $scope.$on('beforePrint', disableFloatThead);
            /** Reenable the floating table header after printing */
            $scope.$on('afterPrint', enableFloatThead);

            /* --- Request Methods --- */

            function getAccSummaries () {
                if (!$scope.empId) {
                    return;
                }

                var year = moment().year();
                var fromDate = moment([year, 0, 1]).subtract(6, 'months');
                var toDate = moment([year + 1, 0, 1]);

                // Restrict by start and end dates if applicable
                if (!$scope.isUser()) {
                    var startDateMoment = moment($scope.empSupInfo.effectiveStartDate || 0);
                    var endDateMoment = moment($scope.empSupInfo.supEndDate || '3000-01-01');

                    fromDate = moment.max(fromDate, startDateMoment);
                    toDate = moment.min(toDate, endDateMoment);
                }

                var params = {
                    empId: $scope.empId,
                    fromDate: fromDate.format('YYYY-MM-DD'),
                    toDate: toDate.format('YYYY-MM-DD')
                };
                $scope.error = null;
                $scope.request.accSummaries = true;
                AccrualHistoryApi.get(params,
                    function onSuccess (resp) {
                        // Store summaries for submitted records in reverse chron. order
                        $scope.accSummaries = resp.result.filter(function(acc) {
                            return !acc.computed || acc.submitted;
                        }).reverse();
                        // Set and initialize projected records
                        $scope.projections = resp.result
                            .filter(isValidProjection)
                            .map(initializeProjection);
                    }, function onFail (resp) {
                        modals.open('500', {details: resp});
                        console.error(resp);
                        $scope.error = {
                            title: "Could not retrieve accrual information.",
                            message: "If you are eligible for accruals please try again later."
                        }
                    }
                ).$promise.finally(function () {
                    $scope.request.accSummaries = false;
                });
            }

            /**
             * Retrieves employee info from the api to determine if the employee is a temporary employee
             */
            function getEmpInfo() {
                // Cancel emp info retrieval if empId is null or viewing non-user employee
                if (!($scope.empId && $scope.isUser())) {
                    return;
                }

                var params = {empId: $scope.empId, detail: true};
                $scope.request.empInfo = true;
                EmpInfoApi.get(params,
                    function onSuccess(response) {
                        var empInfo = response.employee;
                        $scope.empInfo = empInfo;
                        $scope.isTe = empInfo.payType === 'TE';
                    },
                    function onFail(errorResponse) {
                        modals.open('500', errorResponse);
                    }
                ).$promise.finally(function () {
                    $scope.request.empInfo = false;
                });
            }

            /* --- Display Methods --- */

            /**
             * @returns {boolean} true iff the user's accruals are being displayed
             */
            $scope.isUser = function () {
                return $scope.empId === appProps.user.employeeId;
            };

            /**
             * @returns {boolean} true iff any requests are currently loading
             */
            $scope.isLoading = function () {
                for (var dataType in $scope.request) {
                    if (!$scope.request.hasOwnProperty(dataType)) {
                        continue;
                    }
                    if ($scope.request[dataType]) {
                        return true;
                    }
                }
                return false;
            };

            $scope.onAccUsageChange = function (accrualRecord, type) {
                recalculateProjectionTotals();
                setChangedFlags(accrualRecord, type);
            };

            /**
             * Open the accrual detail modal
             * @param accrualRecord
             */
            $scope.viewDetails = function (accrualRecord) {
                if (!accrualRecord.valid) {
                    console.debug('record is invalid');
                    return;
                }
                modals.open('accrual-details', {accruals: accrualRecord}, true);
            };

            // Expose accrual value validation functions
            $scope.isPerValid = isPerValid;
            $scope.isVacValid = isVacValid;
            $scope.isSickEmpValid = isSickEmpValid;
            $scope.isSickFamValid = isSickFamValid;

            /* --- Internal Methods --- */

            /**
             * Set the employee id from the passed in employee sup info if it exists
             * Otherwise set it to the user's empId
             */
            function setEmpId() {
                if ($scope.empSupInfo && $scope.empSupInfo.empId) {
                    $scope.empId = $scope.empSupInfo.empId;
                }
                else {
                    $scope.empId = appProps.user.employeeId;
                    console.log('No empId provided.  Using user\'s empId:', $scope.empId);
                }
            }

            /**
             * @param acc Accrual record
             * @returns {*|boolean} - True iff the record is a computed projection
             *                          and the employee is able to accrue/use accruals
             */
            function isValidProjection(acc) {
                return acc.computed && !acc.submitted && acc.empState.payType !== 'TE' && acc.empState.employeeActive;
            }

            /** Indicates delta fields that are used for input, used to init projection */
            var deltaFields = ['biweekPersonalUsed', 'biweekVacationUsed', 'biweekSickEmpUsed', 'biweekSickFamUsed'];

            /**
             * Initialize the given projection for display
             * @param projection - Accrual projection record
             */
            function initializeProjection(projection) {
                // Set all 0 fields as null to facilitate initial entry
                deltaFields.forEach(function (fieldName) {
                    if (projection[fieldName] === 0) {
                        projection[fieldName] = null;
                    }
                });
                // Add a changed field for storing change flags
                projection.changed = {};
                // Calculate a maximum usage of 12 hours / day for the pay period
                projection.maxHours = projection.payPeriod.numDays * 12;
                // Set initial validation status
                projection.validation = getCleanValidation();
                projection.valid = true;

                return projection;
            }

            /**
             * When a user enters in hours in the projections table, the totals need to be re-computed for
             * the projected accrual records.
             */
            function recalculateProjectionTotals () {
                var accSummaries = $scope.accSummaries;
                var projections = $scope.projections;
                var baseRec = accSummaries.length > 0 ? accSummaries[0] : null;
                var multiYear = false;

                var accState = getInitialAccState(baseRec);

                for (var i = 0; i < projections.length; i++) {
                    var rec = projections[i];

                    var lastRec = i === 0 ? baseRec : projections[i - 1];

                    // If multiple years are present, banked hours will be dynamic and need to be reset
                    if (multiYear) {
                        rec.vacationBanked = lastRec.vacationBanked;
                        rec.sickBanked = lastRec.sickBanked;
                    }

                    // Apply rollover if record is the first of the year and a preceding record is available
                    if (lastRec && accrualUtils.isFirstRecordOfYear(rec)) {
                        multiYear = true;
                        applyRollover(rec, lastRec, accState);
                    }

                    updateAccrualState(rec, accState);
                    setRecordUsedHours(rec, accState);
                    calculateAvailableHours(rec);
                    validateRecord(rec, accState);
                }
            }

            /**
             * Get a new validation object where everything is valid
             * @returns {{per: boolean, vac: boolean, sick: boolean}}
             */
            function getCleanValidation() {
                return {
                    per: true,
                    vac: true,
                    sick: true
                };
            }

            /**
             * Get the initial accrual state based on the base record,
             * or set everything to 0 if no base record exists
             * @param baseRec
             * @returns {{per: (number), vac: (number), sickEmp: (number), sickFam: (number)}}
             */
            function getInitialAccState (baseRec) {
                baseRec = baseRec || {};
                return {
                    per: baseRec.personalUsed || 0,
                    vac: baseRec.vacationUsed || 0,
                    sickEmp: baseRec.sickEmpUsed || 0,
                    sickFam: baseRec.sickFamUsed || 0,
                    validation: getCleanValidation()
                }
            }

            /**
             * Apply an annual rollover from 'lastRecord' to 'record'.
             * Truncate sick and vacation banked hours if they exceed maximums.
             * Reset accrual state to 0 used hours.
             *
             * @param record
             * @param lastRecord
             * @param accState
             */
            function applyRollover (record, lastRecord, accState) {
                record.vacationBanked = Math.min(lastRecord.vacationAvailable, maxVacationBanked);
                record.sickBanked = Math.min(lastRecord.sickAvailable, maxSickBanked);

                accState.per = accState.vac = accState.sickEmp = accState.sickFam = 0;
            }

            /**
             * Update the give accrual state with the biweek used values from the given record
             * @param rec
             * @param accState
             */
            function updateAccrualState(rec, accState) {
                accState.per += rec.biweekPersonalUsed || 0;
                accState.vac += rec.biweekVacationUsed || 0;
                accState.sickEmp += rec.biweekSickEmpUsed || 0;
                accState.sickFam += rec.biweekSickFamUsed || 0;
            }

            /**
             * Set annual usage totals on the given record with the values from the given accrual state
             * @param rec
             * @param accState
             */
            function setRecordUsedHours (rec, accState) {
                rec.personalUsed =  accState.per;
                rec.vacationUsed =  accState.vac;
                rec.sickEmpUsed = accState.sickEmp;
                rec.sickFamUsed = accState.sickFam;
                rec.holidayUsed = rec.holidayUsed || 0;
            }

            /**
             * Calculate the available hours for the given record
             * @param rec
             */
            function calculateAvailableHours (rec) {
                rec.personalAvailable = rec.personalAccruedYtd - rec.personalUsed;
                rec.vacationAvailable = rec.vacationAccruedYtd + rec.vacationBanked - rec.vacationUsed;
                rec.sickAvailable = rec.sickAccruedYtd + rec.sickBanked - rec.sickEmpUsed - rec.sickFamUsed;
            }

            /**
             * Validate the record based on the accrual values present and the validation status of previous records
             * Set the validation results to the running validation on the accrual state
             * If a value is invalid for one record, then the same value type is invalid for all remaining records
             * @param record
             * @param accState
             */
            function validateRecord(record, accState) {
                var validation = accState.validation;

                validation.per = validation.per && isPerValid(record);
                validation.vac = validation.vac && isVacValid(record);
                validation.sick = validation.sick && isSickEmpValid(record) && isSickFamValid(record);

                // Store a snapshot of the running validation to this record
                record.validation = angular.copy(validation);

                // Mark the full record as valid iff all fields are valid
                record.valid = validation.per && validation.vac && validation.sick;
            }

            // Validation functions for each accrual usage type

            function isPerValid(record) {
                return isValidValue(record.biweekPersonalUsed, record.personalAvailable);
            }

            function isVacValid(record) {
                return isValidValue(record.biweekVacationUsed, record.vacationAvailable);
            }

            function isSickEmpValid(record) {
                return isValidValue(record.biweekSickEmpUsed, record.sickAvailable);
            }

            function isSickFamValid(record) {
                return isValidValue(record.biweekSickFamUsed, record.sickAvailable);
            }

            /**
             * Generic validation function for an accrual value
             *
             * Ensure that the value is..
             * null or numeric
             * divisible by 0.5
             * not using more hours than available
             * @param value
             * @param available
             * @returns {boolean}
             */
            function isValidValue(value, available) {
                return value === null ||
                    value !== undefined && available >= 0 && value % 0.5 === 0;
            }

            function setChangedFlags (record, type) {
                var projections = $scope.projections;
                var startIndex = projections.indexOf(record);

                for (var i = startIndex; i < projections.length; i++) {
                    projections[i].changed[type] = true;
                }

                $timeout(resetChangedFlags);
            }

            function resetChangedFlags () {
                angular.forEach($scope.projections, function (record) {
                    record.changed = {};
                });
            }

            /* --- Angular Floating THead Hacks --- */

            /**
             * Attempt to reflow the accrual table 20 times with one attempt every 5ms
             * @param count
             */
            function reflowTable (count) {
                if (count > 20 || !$scope.accSummaries || $scope.accSummaries.length === 0) {
                    return;
                }
                count = isNaN(count) ? 0 : count;
                $(".detail-acc-history-table").floatThead('reflow');
                $timeout(function () {
                    reflowTable(count + 1)
                }, 5);
            }

            $scope.$watchCollection('projections', reflowTable);

            function enableFloatThead() {
                $scope.floatTheadEnabled = true;
            }

            function disableFloatThead() {
                $scope.floatTheadEnabled = false;
            }
        }
    }
}
var essMyInfo = angular.module('essMyInfo');

essMyInfo.controller('EmpCheckHistoryCtrl',
    ['$scope', 'appProps', 'EmpCheckHistoryApi', 'modals',
        function($scope, appProps, EmpCheckHistoryApi, modals) {

            /** Map with deduction descriptions as keys.
             * Map is used instead of array for faster look ups.
             * This map's keys represent the set of all deductions used in $scope.paychecks. */
            $scope.deductionMap = {};

            $scope.checkHistory = {
                searching: false,
                recordYears: null,
                year: null
            };

            var initialYtd = {
                gross: 0,
                directDeposit: 0,
                check: 0
            };

            $scope.ytd = null;

            $scope.init = function() {
                $scope.checkHistory.recordYears = appProps.empActiveYears;
                $scope.checkHistory.year = $scope.checkHistory.recordYears[$scope.checkHistory.recordYears.length - 1];
                $scope.getRecords();
            };

            $scope.getRecords = function() {
                $scope.checkHistory.searching = true;
                $scope.paychecks = [];
                var empId = appProps.user.employeeId;
                var params = {
                    empId: empId,
                    year: $scope.checkHistory.year.toString()
                };
                EmpCheckHistoryApi.get(params, function(response) {
                    $scope.paychecks = response.paychecks.sort(function(a, b) {return new Date(a.checkDate) - new Date(b.checkDate)});
                    initDeductionMap(response.paychecks);
                    addDeductionsToPaychecks(response.paychecks);
                    initYtdValues(response.paychecks);
                    $scope.checkHistory.searching = false;
                }, function(response) {
                    $scope.checkHistory.searching = false;
                    modals.open('500', {details: response});
                    console.log(response);
                })
            };

            function initYtdValues(paychecks) {
                $scope.ytd = angular.extend({}, initialYtd);
                for (var i = 0; i < paychecks.length; i++) {
                    var paycheck = paychecks[i];
                    $scope.ytd.gross += paycheck.grossIncome;
                    $scope.ytd.directDeposit += paycheck.directDepositAmount;
                    $scope.ytd.check += paycheck.checkAmount;
                    for (var key in paycheck.deductions) {
                        if (paycheck.deductions.hasOwnProperty(key)) {
                            addDeductionToYtd(paycheck.deductions[key]);
                        }
                    }
                }
            }

            function initDeductionMap(paychecks) {
                for (var i = 0; i < paychecks.length; i++) {
                    var checkDeductMap = paychecks[i].deductions;
                    for (var key in checkDeductMap) {
                        if (checkDeductMap.hasOwnProperty(key)) {
                            addToDeductionSet(checkDeductMap[key]);
                        }
                    }
                }
            }

            /** Must have a set of all deductions to display in table;
             * if a deduction only occurs in one paycheck we still need a column for it. */
            function addToDeductionSet(deduction) {
                if (!$scope.deductionMap.hasOwnProperty(deduction.description)) {
                    $scope.deductionMap[deduction.description] = true;
                }
            }

            function addDeductionToYtd(deduction) {
                if ($scope.ytd[deduction.description]) {
                    $scope.ytd[deduction.description] += deduction.amount;
                } else {
                    $scope.ytd[deduction.description] = deduction.amount;
                }
            }

            /** Ensure paychecks have entries for all deduction types.
             * If missing add it with a amount of 0. */
            function addDeductionsToPaychecks(paychecks) {
                for (var i = 0; i < paychecks.length; i++) {
                    for (var deductionKey in $scope.deductionMap) {
                        if ($scope.deductionMap.hasOwnProperty(deductionKey)) {
                            // If paycheck is missing deduction
                            if (!paychecks[i].deductions.hasOwnProperty(deductionKey)) {
                                // Add deduction to paycheck
                                paychecks[i].deductions[deductionKey] = createEmptyDeduction();
                            }
                        }
                    }
                }
            }

            /** Returns a stub deduction containing only the amount property where amount = 0.
             * Removes the need to default to 0 in view if deduction is undefined, which simplifies bolding of changes. */
            function createEmptyDeduction() {
                return { amount: 0 };
            }

            /** Compares two currency values, returning true if they differ by more than 3 cents. */
            $scope.isSignificantChange = function(curr, previous) {
                if (typeof previous !== 'undefined') {
                    if (Math.abs(curr - previous) > 0.03) {
                        return true;
                    }
                }
                return false;
            };

            $scope.init();
        }
    ]
);


angular.module('essTime')
    .directive('allowanceStatus', ['appProps', 'modals', 'AllowanceApi', 'AllowanceUtils', allowanceStatusDirective]);

function allowanceStatusDirective(appProps, modals, allowanceApi, allowanceUtils) {
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
        templateUrl: appProps.ctxPath + '/template/time/allowance/allowance-status-directive',
        link: function ($scope, $elem, $attrs) {
            $scope.allowance = null;
            $scope.payType = null;

            $scope.request = {
                allowance: false
            };

            /* --- Watches --- */

            $scope.$watch('empSupInfo', onEmpSupInfoChange);

            /* --- Display Methods --- */

            $scope.isUser = function () {
                return $scope.empId === appProps.user.employeeId;
            };

            /* --- Request Methods --- */

            function getAllowance() {
                $scope.allowance = null;
                $scope.payType = null;
                var params = {
                    empId: $scope.empId,
                    year: moment().year()
                };
                $scope.request.allowance = true;
                return allowanceApi.get(params, onSuccess, onFail)
                    .$promise.finally(function () {
                        $scope.request.allowance = false;
                    });

                function onSuccess (resp) {
                    var results = resp.result || [];
                    if (results.length !== 1) {
                        return onFail(resp);
                    }
                    $scope.allowance = resp.result[0];
                    var dateRange = {
                        beginDate: moment(),
                        endDate: moment()
                    };
                    allowanceUtils.computeRemaining($scope.allowance, dateRange);
                    extractCurrentPayType();
                }
                function onFail (resp) {
                    modals.open('500', {details: resp});
                    console.error(resp);
                }
            }

            /* --- Internal Methods --- */

            function onEmpSupInfoChange() {
                setEmpId();
                getAllowance();
            }

            function extractCurrentPayType() {
                if (!($scope.allowance && $scope.allowance.salaryRecs)) {
                    console.error('No Salary Recs!!');
                    return;
                }

                $scope.allowance.salaryRecs.forEach(function (salaryRec) {
                    var startDate = moment(salaryRec.effectDate);
                    var endDate = moment(salaryRec.endDate || '3000-01-01');
                    if (moment().isBefore(startDate) || moment().isAfter(endDate, 'day')) {
                        return;
                    }
                    $scope.payType = salaryRec.payType;
                })

            }

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
        }
    }
}
angular.module('essTime')
    .filter('entryHours', [entryHoursFilter])
    .directive('timeRecordInput', [timeRecordInputDirective])
    .directive('recordDetails', ['appProps', 'modals', 'AccrualPeriodApi', 'AllowanceApi',
                                 'AllowanceUtils', recordDetailsDirective])
    .directive('recordDetailModal', ['modals', recordDetailModalDirective])
;

/* --- Filters */

/**
 * Returns hour value if input is a valid number, "--" otherwise
 */
function entryHoursFilter () {
    var unenteredValue = "--";
    return function (value) {
        if (isNaN(parseInt(value))) {
            return unenteredValue;
        }
        return value;
    }
}

/* --- Directives --- */

function timeRecordInputDirective () {
    var restrictedKeys = ['e', 'E', '-', '+'];
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.on('focus', function(event){
                $(this).parent().parent().addClass("active");
            });
            element.on('blur', function(event){
                $(this).parent().parent().removeClass("active");
            });
            element.on('keypress', function (event) {
                if (restrictedKeys.indexOf(event.key) >= 0) {
                    event.preventDefault();
                }
            });
        }
    }
}

/**
 * A table that displays details for a specific time record
 */
function recordDetailsDirective(appProps, modals, accrualApi, allowanceApi, allowanceUtils) {
    return {
        scope: {
            record: '='
        },
        templateUrl: appProps.ctxPath + '/template/time/record/details',
        link: function($scope, $elem, $attrs) {
            var showAccrualsSelected = $attrs['showAccruals'] === "true";
            $scope.close = modals.reject;
            $scope.showExitBtn = $attrs['exitBtn'] !== "false";
            $scope.showAccruals = showAccrualsSelected;
            $scope.loadingAccruals = false;
            $scope.loadingAllowance = false;

            $scope.$watch('record', function (record) {
                if (record) {
                    detectPayTypes();
                    loadAccruals();
                    loadAllowance();
                }
            });

            function detectPayTypes() {
                $scope.tempEntries = $scope.annualEntries = false;
                angular.forEach($scope.record.timeEntries, function (entry) {
                    $scope.tempEntries = $scope.tempEntries || entry.payType === 'TE';
                    $scope.annualEntries = $scope.annualEntries || ['RA', 'SA'].indexOf(entry.payType) > -1;
                });
                $scope.showAccruals = showAccrualsSelected && $scope.annualEntries;
                $scope.showAllowance = showAccrualsSelected && $scope.tempEntries;
            }

            function loadAccruals() {
                if (!$scope.showAccruals) {
                    return;
                }
                var record = $scope.record;
                var empId = record.employeeId;
                var recordStartDate = moment(record.beginDate);
                var params = {
                    empId: empId,
                    beforeDate: recordStartDate.format('YYYY-MM-DD')
                };
                $scope.loadingAccruals = true;
                return accrualApi.get(params,
                    function (resp) {
                        if (resp.success) {
                            $scope.accrual = resp.result;
                            console.log($scope.accrual);
                        }
                    }, function (resp) {
                        modals.open('500', {details: resp});
                        console.error(resp);
                    }).$promise.finally(function() {
                        $scope.loadingAccruals = false;
                    });
            }

            function loadAllowance() {
                if (!$scope.showAllowance) {
                    return;
                }
                var record = $scope.record;
                var empId = record.employeeId;
                var year = moment(record.endDate).year();
                var params = {
                    empId: empId,
                    year: year
                };
                $scope.loadingAllowance = true;
                return allowanceApi.get(params, onSuccess, onFail)
                    .$promise.finally(function () {
                        $scope.loadingAllowance = false;
                    });

                function onSuccess (resp) {
                    var results = resp.result || [];
                    if (results.length !== 1) {
                        return onFail(resp);
                    }
                    $scope.allowance = resp.result[0];
                    allowanceUtils.computeRemaining($scope.allowance, record);
                }
                function onFail (resp) {
                    modals.open('500', {details: resp});
                    console.error(resp);
                }
            }
        }
    };
}

/**
 * A modal containing a record-details view
 * @param modals
 * @returns {{template: string, link: link}}
 */
function recordDetailModalDirective(modals) {
    return {
        template: '<div class="record-detail-modal" record-details record="record"></div>',
        link: function ($scope, $elem, $attrs) {
            var params = modals.params();
            $scope.record = params.record;
        }
    }
}


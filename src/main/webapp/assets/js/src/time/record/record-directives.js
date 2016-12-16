var essTime = angular.module('essTime');

/** --- Directives --- */

essTime.directive('timeRecordInput', [function(){
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.on('focus', function(event){
                $(this).parent().parent().addClass("active");
            });
            element.on('blur', function(event){
                $(this).parent().parent().removeClass("active");
            });
        }
    }
}]);

/**
 * A table that displays details for a specific time record
 */
essTime.directive('recordDetails', ['appProps', 'modals', 'AccrualPeriodApi', function (appProps, modals, accrualApi) {
    return {
        scope: {
            record: '='
        },
        templateUrl: appProps.ctxPath + '/template/time/record/details',
        link: function($scope, $elem, $attrs) {
            $scope.close = modals.reject;
            $scope.tempEntries = $scope.annualEntries = false;
            $scope.showExitBtn = $attrs['exitBtn'] !== "false";
            $scope.showAccruals = $attrs['showAccruals'] === "true";
            $scope.loadingAccruals = false;

            $scope.$watch('record', function (record) {
                if (record) {
                    detectPayTypes();
                    loadAccruals();
                }
            });

            function detectPayTypes() {
                angular.forEach($scope.record.timeEntries, function (entry) {
                    $scope.tempEntries = $scope.tempEntries || entry.payType === 'TE';
                    $scope.annualEntries = $scope.annualEntries || ['RA', 'SA'].indexOf(entry.payType) > -1;
                    $scope.showAccruals = $scope.annualEntries && ($attrs.showAccruals === "true");
                });
            }

            function loadAccruals() {
                if (!$scope.showAccruals) {
                    return;
                }
                var record = $scope.record;
                console.log(record);
                var empId = record.employeeId;
                var periodStartMoment = moment(record.payPeriod.startDate);
                var params = {
                    empId: empId,
                    beforeDate: periodStartMoment.format('YYYY-MM-DD')
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
        }
    };
}]);

essTime.directive('recordDetailModal', ['modals', function (modals) {
    return {
        template: '<div class="record-detail-modal" record-details record="record"></div>',
        link: function ($scope, $elem, $attrs) {
            var params = modals.params();
            $scope.record = params.record;
        }
    }
}]);


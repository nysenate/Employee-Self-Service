var essTime = angular.module('essTime');

/** --- Directives --- */

essTime.directive('timeRecordInput', [function(){
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.on('focus', function(event){
                $(this).attr('type', 'number');
                $(this).parent().parent().addClass("active");
            });
            element.on('blur', function(event){
                $(this).attr('type', 'text');
                $(this).parent().parent().removeClass("active");
            });
        }
    }
}]);

/**
 * A table that displays details for a specific time record
 */
essTime.directive('recordDetails', ['appProps', 'modals', function (appProps, modals) {
    return {
        scope: {
            record: '='
        },
        templateUrl: appProps.ctxPath + '/template/time/record/details',
        link: function($scope, $elem, attrs) {
            $scope.close = modals.reject;
            $scope.tempEntries = $scope.annualEntries = false;
            var recordInitialized = false;
            $scope.$watch('record', function () {
                if (!recordInitialized) {
                    angular.forEach($scope.record.timeEntries, function (entry) {
                        $scope.tempEntries = $scope.tempEntries || entry.payType === 'TE';
                        $scope.annualEntries = $scope.annualEntries || ['RA', 'SA'].indexOf(entry.payType) > -1;
                    });
                    recordInitialized = true;
                }
            });
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


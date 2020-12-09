(function () {
    var essTime = angular.module('essTime');

    essTime.directive('timeOffRequestList', ['appProps', requestDirective]);

    function requestDirective(appProps) {
        return {
            restrict: 'E',
            templateUrl: appProps.ctxPath + '/template/time/accrual/time-off-request-list',
            transclude: true,
            scope: {
                data: '='
            },
            link: link
        };

        function link($scope ) {
            /* Function to open the edit page for a selected request */
            $scope.openEditPage = function(request) {
                var currentUrl = appProps.ctxPath;
                var id_num = request.requestId;
                var newUrl = currentUrl + "/time/accrual/time-off-request/" + id_num;
                window.open(newUrl, "_blank");
            };
        }
    }
})();
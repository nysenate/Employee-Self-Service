
angular.module('essMyInfo')
    .directive('ackDocSelect', ['appProps', 'AckDocApi', 'AcknowledgmentYearApi', function (appProps,AckDocApi,AcknowledgmentYearApi) {

        return {
            restrict: "E",
            templateUrl: appProps.ctxPath + "/template/myinfo/personnel/ack-doc-select",
            link: link
        };

        function link($scope, $ele, $attrs) {
            var requestGetYears = true;
            $scope.docYears = [];
            $scope.year = null;

            $scope.$watch("year", getAckDocsInAYear);

            getAckDocYears($scope, requestGetYears);
            $scope.selectDocYear = function (docYear) {
                getAckDocsInAYear($scope,docYear);
            };




            function getAckDocsInAYear() {
                if (!$scope.year) {
                    return;
                }

                $scope.ackDocsInSelectedYear = [];

                var params = {
                    year: $scope.year
                };
                var requestAcquireAcks = true;
                return AckDocApi.get(params, onSuccess, $scope.handleErrorResponse)
                    .$promise.finally(function () {
                        requestAcquireAcks = false;
                    });

                function onSuccess(resp) { //put ack docs in ackDocsInSelectedYear
                    angular.forEach(resp.documents, function (ackDoc) {
                        $scope.ackDocsInSelectedYear.push(ackDoc);
                    });
                }
            }
        }



        function getAckDocYears($scope, requestGetYears) {
            return AcknowledgmentYearApi.get(onSuccess, $scope.handleErrorResponse)
                .$promise.finally(function () {
                    requestGetYears = false;
                });

            function onSuccess(resp) { //put years in ackDocYears
                angular.forEach(resp.ackDocYears, function (year) {
                    $scope.docYears.push(year);
                });
                $scope.year = $scope.docYears[0];
            }
        }

    }]);
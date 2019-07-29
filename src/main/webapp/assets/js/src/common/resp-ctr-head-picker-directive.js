(function () {
    angular.module('ess')
        .directive('rchPicker', ['appProps', 'RCHSearchApi', 'RestErrorService', rchPickerDirective]);

    /**
     * A search driven multiselect allowing the user to select a number of senate offices.
     * @param appProps
     * @param rchSearchApi
     * @param restErrorService
     * @return {{scope: {respCtrHeads: string}, link: link, restrict: string, templateUrl: string}}
     */
    function rchPickerDirective(appProps, rchSearchApi, restErrorService) {
        return {
            scope: {
                respCtrHeads: '='
            },
            restrict: 'E',
            templateUrl: appProps.ctxPath + '/template/myinfo/personnel/resp-ctr-head-picker-directive',
            link: link
        };

        function link($scope, $element, $attrs) {
            $scope.rchResults = [];
            $scope.loading = false;

            // $scope.searchRCH = searchRCH;
            $scope.clearSelected = clearSelected;

            getRCH();

            function clearSelected() {
                console.log('clearing selected rchs', $scope.respCtrHeads);
                $scope.respCtrHeads.selection.length = 0
            }

            function getRCH() {
                var params = {
                    term: "",
                    limit: 0
                };
                $scope.loading = true;
                rchSearchApi.get(params).$promise
                    .then(setResults)
                    .catch(restErrorService.handleErrorResponse)
                    .finally(function () {
                        $scope.loading = false;
                    })
            }

            function setResults(searchResponse) {
                $scope.rchResults = searchResponse.result;
            }
        }
    }
})();
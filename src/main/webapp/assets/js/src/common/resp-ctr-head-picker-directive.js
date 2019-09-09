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
            var limit = 20;

            $scope.rchResults = [];
            $scope.loading = false;
            $scope.offset = 1;
            $scope.total = 0;

            // $scope.searchRCH = searchRCH;
            $scope.clearSelected = clearSelected;

            $scope.searchRCH = searchRCH;

            searchRCH("");

            function clearSelected() {
                console.log('clearing selected rchs', $scope.respCtrHeads);
                $scope.respCtrHeads.selection.length = 0
            }

            function searchRCH(term, nextPage) {
                if (nextPage) {
                    var nextOffset = $scope.offset + limit;
                    // Cancel page load if the last result is already loaded
                    // Also if the next page is already loading
                    if ($scope.loading || nextOffset > $scope.total) {
                        return;
                    }
                    $scope.offset = nextOffset;
                } else {
                    $scope.rchResults = [];
                    $scope.offset = 1;
                }
                var params = {
                    term: term,
                    limit: limit,
                    offset: $scope.offset
                };
                $scope.loading = true;
                rchSearchApi.get(params).$promise
                    .then(appendResults)
                    .catch(restErrorService.handleErrorResponse)
                    .finally(function () {
                        $scope.loading = false;
                    })
            }

            function appendResults(searchResponse) {
                console.log('loaded rch', searchResponse.offsetStart, searchResponse.offsetEnd);
                $scope.total = searchResponse.total;
                $scope.rchResults = $scope.rchResults.concat(searchResponse.result);
            }
        }
    }
})();
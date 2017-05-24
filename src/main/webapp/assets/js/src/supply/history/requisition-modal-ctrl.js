var essSupply = angular.module('essSupply');

essSupply.directive('requisitionModal',
                    ['appProps', 'modals', function (appProps, modals) {

    return {
        templateUrl: appProps.ctxPath + 'template/supply/history/requisition-modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.requisition = modals.params();
        console.log($scope.requisition);
        $scope.closeModal = function () {
            modals.reject();
        }
    }
}]);

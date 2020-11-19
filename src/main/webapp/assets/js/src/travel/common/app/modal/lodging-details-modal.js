var essTravel = angular.module('essTravel');

essTravel.directive('essLodgingDetailsModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/common/app/modal/lodging-details-modal',
        controller: 'LodgingDetailsModalCtrl'
    }
}])
    .controller('LodgingDetailsModalCtrl', ['$scope', 'modals', lodgingDetailsModalCtrl]);

function lodgingDetailsModalCtrl($scope, modals) {

    this.$onInit = function () {
        $scope.amendment = modals.params().amendment;
        $scope.isOverridden = $scope.amendment.lodgingPerDiems.isOverridden;
    };

    $scope.closeModal = function () {
        modals.resolve();
    };
}
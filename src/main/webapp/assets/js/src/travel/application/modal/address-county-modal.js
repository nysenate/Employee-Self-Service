var essTravel = angular.module('essTravel');

essTravel.directive('addressCountyModal', ['appProps', function (appProps) {
    return {
        scope: {},
        templateUrl: appProps.ctxPath + '/template/travel/application/modal/address-county-modal',
        controller: 'AddressCountyModalCtrl'
    }
}])
    .controller('AddressCountyModalCtrl', ['$scope', 'modals', addressCountyModalCtrl]);

function addressCountyModalCtrl($scope, modals) {

    $scope.address = modals.params().address;

    $scope.resolveModal = function () {
        modals.resolve();
    }

}
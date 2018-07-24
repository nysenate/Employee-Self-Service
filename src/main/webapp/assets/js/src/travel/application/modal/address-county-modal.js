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

    this.$onInit = function () {
        $scope.address = modals.params().address;
        document.getElementById('countyInput').focus();
    };

    $scope.submit = function () {
        modals.resolve();
    };

    $scope.cancel = function () {
        console.log("Canceling Modal");
        modals.reject();
    };
}
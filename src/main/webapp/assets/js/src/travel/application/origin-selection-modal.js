var essTravel = angular.module('essTravel');

essTravel.directive('originSelectionModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/application/origin-selection-modal',
        scope: {},
        controller: 'OriginSelectionModalCtrl'
    }
}])
    .controller('OriginSelectionModalCtrl', ['$scope', originSelectionCtrl]);

function originSelectionCtrl($scope) {

    $scope.address = {
        street1: "",
        street2: "",
        city: "",
        state: "",
        zip: ""
    };

}

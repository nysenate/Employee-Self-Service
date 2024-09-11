var essTravel = angular.module('essTravel');

essTravel.directive('appFormViewModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/common/form/app-form-view-modal',
        controller: 'AppFormViewModal'
    }
}])
    .controller('AppFormViewModal', ['$scope', 'modals', 'TravelAppCancelApi', 'LocationService', appFormView]);

function appFormView($scope, modals, appCancelApi, locationService) {

    $scope.app = modals.params();

    $scope.exit = function () {
        modals.resolve();
    };

    $scope.cancel = function() {
        modals.open("app-cancel-confirm").then(function() {
            appCancelApi.save({id: $scope.app.id}).$promise.then(function(app) {
                locationService.go("/travel/applications", true);
            });
        })
    }
}

var essTravel = angular.module('essTravel');

essTravel.directive('travelAppLocations', ['appProps', 'modals', appLocations]);

function appLocations(appProps, modals) {
   return {
       templateUrl: appProps.ctxPath + '/template/travel/application/travel-app-locations',
       link: function ($scope, $elem, $attrs) {

           $scope.setOrigin = function() {
               modals.open('origin-selection-modal');
           }
       }
   }
}

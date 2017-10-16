var essTravel = angular.module('essTravel');

essTravel.directive('travelAppLocations', ['appProps', 'modals', appLocations]);

function appLocations(appProps, modals) {
   return {
       templateUrl: appProps.ctxPath + '/template/travel/application/travel-app-locations',
       link: function ($scope, $elem, $attrs) {

           $scope.enterDestination = function() {
               modals.open('destination-selection-modal')
                   .then(addDestination);
           };

           function addDestination(destination) {
               console.log(destination);
           }

           $scope.setAddress = function (address) {
               console.log("callback executed");
               console.log(address);
           }
       }
   }
}

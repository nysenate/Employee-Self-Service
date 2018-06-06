var essTravel = angular.module('essTravel');

essTravel.directive('travelHistoryDetailModal', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/travel-history-detail-modal',
        scope: {},
        controller: 'TravelHistoryDetailCtrl'
    }
}])
    .controller('TravelHistoryDetailCtrl', ['$scope', 'modals', travelDetailCtrl]);

function travelDetailCtrl($scope, modals) {
    //display the travel application details corresponding to what they clicked on

    $scope.app = modals.params();
    console.log($scope.app);

    /**
     * @return {Array} Array containing all modes of transportation in this travel application.
     */
    $scope.getModesOfTransportation = function() {
        var mots = [];
        var legs = $scope.app.route.outboundLegs.concat($scope.app.route.returnLegs);
        legs.forEach(function(leg, index) {
            var mot = leg.modeOfTransportation;
            if (!arrayContains(mots, mot)) {
                mots.push(mot);
            }
        });
        return mots;

        // Returns true of an array contains an object. Compares object equality.
        function arrayContains(array, obj) {
            var contains = false;
            array.forEach(function(element) {
                if (angular.equals(element, obj)) {
                    contains = true;
                }
            });
            return contains;
        }

    };

    $scope.exit = function () {
        modals.resolve();
    };
}

var essTravel = angular.module('essTravel');

essTravel.directive('essOutboundEditForm', ['appProps', outboundEditLink]);

function outboundEditLink(appProps) {
    return {
        restrict: 'E',
        scope: {
            app: '<',               // The application being edited.
            title: '@',             // The title
            positiveCallback: '&',  // Callback function called when continuing. Takes a travel app param named 'app'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'app'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a travel app param named 'app'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/outbound-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyApp = angular.copy(scope.app);

            if (scope.dirtyApp.route.outboundLegs.length === 0) {
                var leg = new Leg();
                // Init from address to employees work address.
                leg.from = scope.app.traveler.empWorkLocation.address;
                scope.dirtyApp.route.outboundLegs.push(leg);
            }

            scope.addSegment = function () {
                // Initialize new leg
                var segment = new Leg();
                var prevSeg = scope.dirtyApp.route.outboundLegs[scope.dirtyApp.route.outboundLegs.length - 1];
                segment.from = prevSeg.to;
                segment.methodOfTravel = prevSeg.methodOfTravel;
                segment.methodOfTravelDescription = prevSeg.methodOfTravelDescription;
                scope.dirtyApp.route.outboundLegs.push(segment);
            };

            scope.setFromAddress = function (leg, address) {
                leg.from = address;
            };

            scope.setToAddress = function (leg, address) {
                leg.to = address;
            };

            scope.isLastSegment = function (index) {
                return scope.dirtyApp.route.outboundLegs.length - 1 === index;
            };

            scope.deleteSegment = function () {
                scope.dirtyApp.route.outboundLegs.pop();
            };

            scope.next = function () {
                scope.setFormElementsTouched(scope.outbound.form);
                if (scope.outbound.form.$valid) {
                    scope.normalizeTravelDates(scope.dirtyApp.route.outboundLegs);
                    scope.checkCounties(scope.dirtyApp.route.outboundLegs)
                        .then(function () {
                            scope.positiveCallback({app: scope.dirtyApp});
                        });
                }
            };

            scope.back = function () {
                scope.neutralCallback({app: scope.dirtyApp});
            };

            scope.cancel = function () {
                scope.negativeCallback({app: scope.dirtyApp});
            };
        }
    }
}
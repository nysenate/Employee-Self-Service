var essTravel = angular.module('essTravel');

essTravel.directive('essOutboundEditForm', ['appProps', outboundEditLink]);

function outboundEditLink(appProps) {
    return {
        restrict: 'E',
        scope: {
            amendment: '<',         // The amendment being edited.
            traveler: '<',          // The employee who will be traveling.
            title: '@',             // The title.
            positiveCallback: '&',  // Callback function called when continuing. Takes a travel app param named 'amendment'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a travel app param named 'amendment'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a travel app param named 'amendment'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/outbound-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyAmendment = angular.copy(scope.amendment);

            if (scope.dirtyAmendment.route.outboundLegs.length === 0) {
                var leg = new Leg();
                console.log(scope.traveler);
                // Init from address to employees work address.
                leg.from.address = scope.traveler.empWorkLocation.address;
                scope.dirtyAmendment.route.outboundLegs.push(leg);
            }

            scope.addSegment = function () {
                // Initialize new leg
                var segment = new Leg();
                var prevSeg = scope.dirtyAmendment.route.outboundLegs[scope.dirtyAmendment.route.outboundLegs.length - 1];
                segment.from = prevSeg.to;
                segment.methodOfTravel = prevSeg.methodOfTravel;
                segment.methodOfTravelDescription = prevSeg.methodOfTravelDescription;
                scope.dirtyAmendment.route.outboundLegs.push(segment);
            };

            scope.setFromAddress = function (leg, address) {
                leg.from.address = address;
            };

            scope.setToAddress = function (leg, address) {
                leg.to.address = address;
            };

            scope.isLastSegment = function (index) {
                return scope.dirtyAmendment.route.outboundLegs.length - 1 === index;
            };

            scope.deleteSegment = function () {
                scope.dirtyAmendment.route.outboundLegs.pop();
            };

            scope.next = function () {
                scope.setInvalidFormElementsTouched(scope.outbound.form);
                if (scope.outbound.form.$valid) {
                    scope.normalizeTravelDates(scope.dirtyAmendment.route.outboundLegs);
                    scope.checkCounties(scope.dirtyAmendment.route.outboundLegs)
                        .then(function () {
                            scope.positiveCallback({amendment: scope.dirtyAmendment});
                        });
                }
            };

            scope.back = function () {
                scope.neutralCallback({app: scope.dirtyAmendment});
            };

            scope.cancel = function () {
                scope.negativeCallback({app: scope.dirtyAmendment});
            };
        }
    }
}
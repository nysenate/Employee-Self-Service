var essTravel = angular.module('essTravel');

essTravel.directive('essOutboundEditForm', ['appProps', outboundEditLink]);

function outboundEditLink(appProps) {
    return {
        restrict: 'E',
        scope: {
            data: '<',         // The amendment being edited.
            positiveCallback: '&',  // Callback function called when continuing. Takes a draft param named 'draft'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a draft param named 'draft'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a draft param named 'draft'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/outbound-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyDraft = angular.copy(scope.data.draft);
            console.log(scope.dirtyDraft);

            if (scope.dirtyDraft.amendment.route.outboundLegs.length === 0) {
                var leg = new Leg();
                // Init from address to employees work address.
                leg.from.address = scope.dirtyDraft.traveler.empWorkLocation.address;
                scope.dirtyDraft.amendment.route.outboundLegs.push(leg);
            }

            scope.addSegment = function () {
                // Initialize new leg
                var segment = new Leg();
                var prevSeg = scope.dirtyDraft.amendment.route.outboundLegs[scope.dirtyDraft.amendment.route.outboundLegs.length - 1];
                segment.from = prevSeg.to;
                segment.methodOfTravel = prevSeg.methodOfTravel;
                segment.methodOfTravelDescription = prevSeg.methodOfTravelDescription;
                scope.dirtyDraft.amendment.route.outboundLegs.push(segment);
            };

            scope.setFromAddress = function (leg, address) {
                leg.from.address = address;
            };

            scope.setToAddress = function (leg, address) {
                leg.to.address = address;
            };

            scope.isLastSegment = function (index) {
                return scope.dirtyDraft.amendment.route.outboundLegs.length - 1 === index;
            };

            scope.deleteSegment = function () {
                scope.dirtyDraft.amendment.route.outboundLegs.pop();
            };

            scope.next = function () {
                scope.setInvalidFormElementsTouched(scope.outbound.form);
                if (scope.outbound.form.$valid) {
                    scope.normalizeTravelDates(scope.dirtyDraft.amendment.route.outboundLegs);
                    scope.checkCounties(scope.dirtyDraft.amendment.route.outboundLegs)
                        .then(function () {
                            scope.positiveCallback({draft: scope.dirtyDraft});
                        });
                }
            };

            scope.back = function () {
                scope.neutralCallback({draft: scope.dirtyDraft});
            };

            scope.cancel = function () {
                scope.negativeCallback({draft: scope.dirtyDraft});
            };
        }
    }
}
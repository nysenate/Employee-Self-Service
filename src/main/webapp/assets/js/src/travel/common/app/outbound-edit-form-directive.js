var essTravel = angular.module('essTravel');

essTravel.directive('essOutboundEditForm', ['appProps', '$timeout', 'AppEditStateService', outboundEditLink]);

function outboundEditLink(appProps, $timeout, stateService) {
    return {
        restrict: 'E',
        scope: {
            appContainer: '='
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/outbound-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.stateService = stateService;
            scope.dirtyApp = angular.copy(scope.appContainer.app);

            if (scope.dirtyApp.route.outboundLegs.length === 0) {
                var leg = new Leg();
                // Init from address to employees work address.
                leg.from = scope.appContainer.app.traveler.empWorkLocation.address;
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
                        .then(scope.continue);
                }
            };

            scope.continue = function () {
                scope.appContainer.app.route.outboundLegs = scope.dirtyApp.route.outboundLegs;
                stateService.nextState();
            };
        }
    }
}
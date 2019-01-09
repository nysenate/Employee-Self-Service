var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationReturnCtrl', ['$scope', '$timeout', '$q', 'modals',
                                                     'TravelApplicationReturnApi', returnCtrl]);

function returnCtrl($scope, $timeout, $q, modals, returnApi) {

    this.$onInit = function () {
        $scope.return = {
            form: {}
        };

        $scope.route = angular.copy($scope.data.app.route);

        if ($scope.route.returnLegs.length === 0) {
            // Init return leg
            var segment = {};
            segment.from = angular.copy($scope.route.outboundLegs[$scope.route.outboundLegs.length - 1].to);
            segment.to = angular.copy($scope.route.outboundLegs[0].from);
            // If only 1 outbound mode of transportation, initialize to that.
            if (numDistinctModesOfTransportation($scope.data.app) === 1) {
                segment.modeOfTransportation = angular.copy($scope.route.outboundLegs[0].modeOfTransportation);
            }
            $scope.route.returnLegs.push(segment);
        }

        function numDistinctModesOfTransportation(app) {
            var mots = (app.route.outboundLegs.concat(app.route.returnLegs)).map(function (leg) {
                return leg.modeOfTransportation.description;
            });
            var distinct = _.uniq(mots);
            return distinct.length;
        }
    };

    $scope.addSegment = function () {
        // When adding a new segment, mark the form as unsubmitted if there are not any errors.
        // This prevents the error notification from being display for errors in the new segment
        // which the user has not had a chance to fill out yet.
        if ($scope.return.form.$valid) {
            $scope.return.form.$submitted = false;
        }

        // Initialize new leg
        var segment = {};
        var prevSeg = $scope.route.returnLegs[$scope.route.returnLegs.length - 1];
        segment.from = prevSeg.to;
        segment.to = angular.copy($scope.route.outboundLegs[0].from);
        segment.modeOfTransportation = prevSeg.modeOfTransportation;
        segment.isMileageRequested = prevSeg.isMileageRequested;
        segment.isMealsRequested = prevSeg.isMealsRequested;
        segment.isLodgingRequested = prevSeg.isLodgingRequested;
        $scope.route.returnLegs.push(segment);
    };

    $scope.setFromAddress = function (leg, address) {
        leg.from = address;
    };

    $scope.setToAddress = function (leg, address) {
        leg.to = address;
    };

    $scope.isLastSegment = function (index) {
        return $scope.route.returnLegs.length - 1 === index;
    };

    $scope.deleteSegment = function () {
        $scope.route.returnLegs.pop();
    };

    // Ensure user does not select a return travel date before the outbound travel date.
    $scope.fromDate = function () {
        return $scope.route.outboundLegs.slice(-1)[0].travelDate;
    };

    $scope.next = function () {
        $scope.setFormElementsTouched($scope.return.form);
        if ($scope.return.form.$valid) {
            $scope.normalizeTravelDates($scope.route.returnLegs);
            promptUserIfLongTrip()
                .then(function () {
                    $scope.checkCounties($scope.route.returnLegs)
                })
                .then($scope.continue);
        }

        /**
         * Checks if the trip is longer than 7 days which would suggest a date typo by the user.
         * If it is, display a modal to ask the user if this is intentional.
         * @return {Promise} A promise which is resolved if the trip is less than 7 days long or the user
         * approves of their long trip. Rejected if the user wishes to review/change their travel dates.
         */
        function promptUserIfLongTrip() {
            var deferred = $q.defer();
            var startDate = moment($scope.data.app.startDate, "YYYY-MM-DD", true);
            var endDate = moment($scope.route.returnLegs[$scope.route.returnLegs.length - 1].travelDate, "MM/DD/YYYY", true);
            var duration = moment.duration(endDate - startDate);
            if (duration.asDays() > 7) {
                modals.open('long-trip-warning')
                    .then(function () {
                        deferred.resolve();
                    })
                    .catch(function () {
                        deferred.reject();
                    })
            }
            else {
                deferred.resolve();
            }
            return deferred.promise;
        }
    };

    $scope.continue = function () {
        $scope.openLoadingModal();
        returnApi.update({id: $scope.data.app.id}, $scope.route, function (response) {
            $scope.data.app = response.result;
            $scope.nextState();
            $scope.closeLoadingModal();
        }, function (error) {
            $scope.closeLoadingModal();
            if (error.status === 502) {
                $scope.handleDataProviderError();
            }
            else {
                $scope.handleErrorResponse(error);
            }
        });
    };

    /**
     * Sets the focus on the Other MOT input box when selecting Other MOT.
     * @param leg
     */
    $scope.motChange = function (leg, index) {
        $timeout(function () { // Execute on next digest cycle, giving the input a chance to render.
            if (leg.modeOfTransportation.methodOfTravel === 'OTHER') {
                document.getElementById('returnMotOtherInput_' + index).focus();
            }
        });
    };
}

var essTravel = angular.module('essTravel');

essTravel.directive('essReturnEditForm', ['$q', 'appProps', 'modals', 'TravelDraftsApi', returnEditForm]);

function returnEditForm($q, appProps, modals, draftsApi) {
    return {
        restrict: 'E',
        scope: {
            data: '<',         // The application being edited.
            positiveCallback: '&',  // Callback function called when continuing. Takes a draft param named 'draft'.
            neutralCallback: '&',   // Callback function called when moving back. Takes a draft param named 'draft'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a draft param named 'draft'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/return-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.mode = scope.data.mode;
            scope.dirtyDraft = angular.copy(scope.data.draft);
            scope.route = scope.data.dirtyRoute;

            if (scope.route.returnLegs.length === 0) {
                // Init return leg
                var leg = new Leg();
                leg.from = angular.copy(scope.route.outboundLegs[scope.route.outboundLegs.length - 1].to);
                leg.to = angular.copy(scope.route.outboundLegs[0].from);
                // If only 1 outbound mode of transportation, initialize to that.
                if (numDistinctModesOfTransportation(scope.dirtyDraft.amendment) === 1) {
                    leg.methodOfTravelDisplayName = angular.copy(scope.route.outboundLegs[0].methodOfTravelDisplayName);
                    leg.methodOfTravelDescription = angular.copy(scope.route.outboundLegs[0].methodOfTravelDescription);
                }
                scope.route.returnLegs.push(leg);
            }

            function numDistinctModesOfTransportation(amendment) {
                var mots = (amendment.route.outboundLegs.concat(amendment.route.returnLegs)).map(function (leg) {
                    return leg.methodOfTravelDisplayName;
                });
                var distinct = _.uniq(mots);
                return distinct.length;
            }

            scope.addSegment = function () {
                // When adding a new segment, mark the form as unsubmitted if there are not any errors.
                // This prevents the error notification from being display for errors in the new segment
                // which the user has not had a chance to fill out yet.
                if (scope.return.form.$valid) {
                    scope.return.form.$submitted = false;
                }

                // Initialize new leg
                var segment = {};
                var prevSeg = scope.route.returnLegs[scope.route.returnLegs.length - 1];
                segment.from = prevSeg.to;
                segment.to = angular.copy(scope.route.outboundLegs[0].from);
                segment.methodOfTravelDisplayName = prevSeg.methodOfTravelDisplayName;
                segment.methodOfTravelDescription = prevSeg.methodOfTravelDescription;
                scope.route.returnLegs.push(segment);
            };

            scope.setFromAddress = function (leg, address) {
                leg.from.address = address;
            };

            scope.setToAddress = function (leg, address) {
                leg.to.address = address;
            };

            scope.isLastSegment = function (index) {
                return scope.route.returnLegs.length - 1 === index;
            };

            scope.deleteSegment = function () {
                scope.route.returnLegs.pop();
            };

            // Ensure user does not select a return travel date before the outgoing travel date.
            scope.fromDate = function () {
                return scope.route.outboundLegs.slice(-1)[0].travelDate;
            };

            scope.next = function () {
                validateData()
                    .then(updateRoute)
                    .then(function () {
                        scope.positiveCallback({draft: scope.dirtyDraft})
                    })
            };

            function validateData() {
                scope.setInvalidFormElementsTouched(scope.return.form);
                if (scope.return.form.$valid) {
                    scope.normalizeTravelDates(scope.route.returnLegs);
                    return promptUserIfLongTrip(scope.route)
                        .then(function () {
                            scope.checkCounties(scope.route.returnLegs)
                        })
                } else {
                    scope.return.form.$submitted = true;
                }
            }

            function updateRoute() {
                var deferred = $q.defer();
                if (angular.toJson(scope.route) !== angular.toJson(scope.data.draft.amendment.route)) {
                    scope.openLoadingModal();
                    scope.dirtyDraft.amendment.route = scope.route;
                    draftsApi.update(
                        {
                            options: ['ROUTE'],
                            draft: scope.dirtyDraft
                        })
                        .$promise
                        .then(function (res) {
                            scope.dirtyDraft = res.result;
                            scope.closeLoadingModal();
                            deferred.resolve();
                        })
                        .catch(function (error) {
                            scope.closeLoadingModal();
                            if (error.status === 502) {
                                scope.handleDataProviderError();
                            } else if (error.status === 400) {
                                scope.$parent.handleTravelDateError();
                            } else {
                                scope.$parent.handleErrorResponse(error);
                            }
                            deferred.reject(error);
                        })
                } else {
                    deferred.resolve();
                }
                return deferred.promise;
            }

            scope.save = function () {
                validateData()
                    .then(updateRoute)
                    .then(function () {
                        scope.saveDraft(scope.dirtyDraft)
                            .then(function (draft) {
                                scope.dirtyDraft = draft;
                            })
                            .catch(function (error) {
                                console.error(error);
                                scope.$parent.handleErrorResponse(error);
                            });
                    });
            }

            scope.back = function () {
                scope.neutralCallback({draft: scope.dirtyDraft});
            };

            scope.cancel = function () {
                scope.negativeCallback({draft: scope.dirtyDraft});
            };

            /**
             * Checks if the trip is longer than 7 days which would suggest a date typo by the user.
             * If it is, display a modal to ask the user if this is intentional.
             * @return {Promise} A promise which is resolved if the trip is less than 7 days long or the user
             * approves of their long trip. Rejected if the user wishes to review/change their travel dates.
             */
            function promptUserIfLongTrip(route) {
                var deferred = $q.defer();
                var startDate = moment(scope.dirtyDraft.startDate, "YYYY-MM-DD", true);
                var endDate = moment(route.returnLegs[route.returnLegs.length - 1].travelDate, "MM/DD/YYYY", true);
                var duration = moment.duration(endDate - startDate);
                if (duration.asDays() > 7) {
                    modals.open('long-trip-warning')
                        .then(function () {
                            deferred.resolve();
                        })
                        .catch(function () {
                            deferred.reject();
                        })
                } else {
                    deferred.resolve();
                }
                return deferred.promise;
            }
        }
    }
}
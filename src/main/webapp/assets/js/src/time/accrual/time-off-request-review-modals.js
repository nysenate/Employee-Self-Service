/**
 * Modal directives that are used in the time off request review page
 */

var essApp = angular.module('ess');

essTime.factory('ReviewRequestApi', ['$resource', function ($resource) {
    return $resource("/api/v1/accruals/request/review/:requestId", {
        requestId: '@requestId',
        action: '@action',
        comment: '@comment'
    });
}]);
essApp.directive('timeOffRequestReviewModal', ['appProps', 'modals', 'LocationService', 'AccrualApi', timeOffRequestReviewModal]);
essApp.directive('timeOffRequestApproveSubmitModal', ['modals', 'appProps', 'ReviewRequestApi', timeOffRequestApproveSubmitModal]);


function timeOffRequestReviewModal(appProps, modals, locationService, AccrualApi) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/accrual/time-off-request-review-modal',
        link: link
    };

    function link($scope, $elem, $attrs) {
        $scope.addedComments = {};

        $scope.accruals = {
            personal: 0,
            sick: 0,
            vacation: 0
        };
        $scope.accrualsPost = {
            personal: 0,
            sick: 0,
            vacation: 0
        };
        $scope.iSelectedRequest = 0;
        $scope.requests = modals.params().requests;
        $scope.alreadyApproved = modals.params().alreadyApproved;
        $scope.empId = appProps.user.employeeId;

        //for all the requests, set the initial comment to ""
        $scope.requests.forEach(function(request) {
            $scope.addedComments[request.requestId] = "";
        });


        /**
         *  Time-Off Requests are categorized under approved or disapproved,
         *  keyed by requestId
         */
        var approved = {};
        var disapproved = {};

        /**
         * If the supervisor is reviewing already approved requests, they must
         * be in the approved list.
         */
        if ($scope.alreadyApproved) {
            angular.forEach($scope.requests, function (request) {
                approved[request.requestId] = request;
            });
            console.log("Approved list: ", approved);
        }

        /**
         * Add a custom fit class to this element so that it isn't sized in the default way
         */
        $elem.addClass('custom-fit');

        /** Sets the given index as the index of the selected request */
        $scope.selectRequest = function (index) {
            $scope.iSelectedRequest = index;
            retrieveAccrualInformation();
        };

        function retrieveAccrualInformation() {
            var accrualArgs = {
                'empId': $scope.requests[$scope.iSelectedRequest].employeeId,
                'beforeDate': new Date().toISOString().substr(0, 10)
            };
            AccrualApi.get(accrualArgs).$promise.then(
                function (data) {
                    $scope.accruals.personal = data.result.personalAvailable;
                    $scope.accruals.vacation = data.result.vacationAvailable;
                    $scope.accruals.sick = data.result.sickAvailable;
                    $scope.accrualsPost.personal = data.result.personalAvailable;
                    $scope.accrualsPost.vacation = data.result.vacationAvailable;
                    $scope.accrualsPost.sick = data.result.sickAvailable;
                    updateAccrualTotals();
                },
                function (data) {
                    $scope.accruals.personal = 0;
                    $scope.accruals.vacation = 0;
                    $scope.accruals.sick = 0;
                    $scope.accrualsPost.personal = 0;
                    $scope.accrualsPost.vacation = 0;
                    $scope.accrualsPost.sick = 0;
                    console.error("There was an error accessing accrual data.", data);
                    updateAccrualTotals();
                }
            );
            function updateAccrualTotals() {
                var vacationUsed = 0, personalUsed = 0, sickUsed = 0;
                $scope.requests[$scope.iSelectedRequest].days.forEach(function (day) {
                    day.totalHours = day.workHours + day.vacationHours + day.personalHours + day.sickEmpHours
                        + day.sickFamHours + day.miscHours + day.holidayHours;
                    vacationUsed += day.vacationHours;
                    personalUsed += day.personalHours;
                    sickUsed += day.sickEmpHours + day.sickFamHours;
                });
                $scope.accrualsPost.vacation = $scope.accruals.vacation - vacationUsed;
                $scope.accrualsPost.personal = $scope.accruals.personal - personalUsed;
                $scope.accrualsPost.sick = $scope.accruals.sick - sickUsed;
            };
        }
        retrieveAccrualInformation();//initialize accruals

        $scope.next = function () {
            selectNextPendingRequest();
        };

        /**
         * Removes the selected request from both the approved and disapproved
         * categories
         */
        $scope.cancelRequest = function () {
            var request = $scope.requests[$scope.iSelectedRequest];
            delete approved[request.requestId];
            delete disapproved[request.requestId];
        };

        /**
         * Adds the selected request to the 'approved' category
         */
        $scope.approveRequest = function () {
            var request = $scope.requests[$scope.iSelectedRequest];
            $scope.cancelRequest();
            approved[request.requestId] = request;
            selectNextPendingRequest();
        };

        /**
         * Reject the selected request, add it to'disapproved' category
         */
        $scope.rejectRequest = function () {
            var request = $scope.requests[$scope.iSelectedRequest];
            $scope.cancelRequest();
            disapproved[request.requestId] = request;
            selectNextPendingRequest();
        };

        $scope.submissionEmpty = function () {
            return Object.keys(approved).length === 0 && Object.keys(disapproved).length === 0;
        };

        /**
         * Returns a string that indicates whether a request has been approved,
         * disapproved or neither
         */
        $scope.getApprovalStatus = function (request) {
            if (request.requestId in approved) {
                return 'approved';
            }
            if (request.requestId in disapproved) {
                return 'disapproved';
            }
            return 'untouched';
        };

        /**
         * Submit changes
         */
        $scope.submitChanges = function () {
            modals.open('time-off-request-approve-submit-modal', {
                approved: approved,
                disapproved: disapproved,
                comments: $scope.addedComments
            }).then(modals.resolve);
        };

        /**
         * Closes the modal without the intention of submitting requests
         * Opens a prompt if there are unsubmitted approvals/disapprovals
         */
        $scope.close = function () {
            if ($scope.submissionEmpty()) {
                modals.reject();
                return;
            }
            modals.open('time-off-request-review-close')
                .then(modals.reject);
        };

        /**
         * Bind function to move a request cursor when arrow keys are pressed
         */
        var $doc = angular.element(document);
        $doc.on('keydown', onKeydown);
        $scope.$on('$destroy', function () {
            $doc.off('keydown', onKeydown);
        });

        /** --- Internal Methods --- */

        /**
         * Locates and selects the next pending request by searching after and then
         * before the selected request;
         */
        function selectNextPendingRequest() {
            for (var i = 0; i < $scope.requests.length; i++) {
                var iAdj = (i + $scope.iSelectedRequest) % $scope.requests.length;
                if ($scope.getApprovalStatus($scope.requests[iAdj]) === 'untouched') {
                    $scope.iSelectedRequest = iAdj;
                    retrieveAccrualInformation();
                    return;
                }
            }
        }

        /**
         * Selects the next request.  For a smarter approach see selectNextPendingRequest()
         */
        function selectNextRequest() {
            var nextIndex = $scope.iSelectedRequest + 1;
            if (nextIndex < $scope.requests.length) {
                $scope.iSelectedRequest = nextIndex;
            }
            retrieveAccrualInformation();
        }

        /**
         * Selects the previous request. For a smarter approach see selectNextPendingRequest()
         */
        function selectPreviousRequest() {
            var previousIndex = $scope.iSelectedRequest - 1;
            if (previousIndex >= 0) {
                $scope.iSelectedRequest = previousIndex;
            }
            retrieveAccrualInformation();
        }

        /**
         * Detect arrow keypress and move selected request cursor accordingly
         * @param e
         */
        function onKeydown(e) {
            if ([38, 39].indexOf(e.keyCode) >= 0) {
                selectPreviousRequest();
            } else if ([40, 41].indexOf(e.keyCode) >= 0) {
                selectNextRequest();
            } else {
                return;
            }
            $scope.$digest();
        }
    }
}

function timeOffRequestApproveSubmitModal(modals, appProps, ReviewRequestApi) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/accrual/time-off-request-approve-submit-modal',
        link: function ($scope, $elem, $attrs) {
            $scope.approved = modals.params().approved;
            $scope.approvedCount = ($scope.approved) ? Object.keys($scope.approved).length : 0;
            $scope.disapproved = modals.params().disapproved;
            $scope.disapprovedCount = ($scope.disapproved) ? Object.keys($scope.disapproved).length : 0;
            $scope.addedComments = modals.params().comments;

            $scope.cancel = function() {
                modals.reject(null);
            }

            $scope.resolve = function () {
                //Supervisor has agreed to submit changes
                //Submit the changes to the database via api call
                console.log($scope.addedComments);

                //approved requests
                angular.forEach($scope.approved, function (approvedRequest) {
                    var params = {
                        requestId: approvedRequest.requestId,
                        action: "APPROVE"
                    };
                    if ($scope.addedComments[approvedRequest.requestId] !== "") {
                        params.comment= $scope.addedComments[approvedRequest.requestId];
                    }
                    ReviewRequestApi.save(params).$promise.then(
                        function (data) {
                            console.log("Success!: ", data);
                        },
                        function (data) {
                            console.log("ERROR", data);
                        }
                    );
                });

                //disapproved requests
                angular.forEach($scope.disapproved, function (disapprovedRequest) {
                    var params = {
                        requestId: disapprovedRequest.requestId,
                        action: "DISAPPROVE"
                    };
                    if ($scope.addedComments[disapprovedRequest.requestId] !== "") {
                        params.comment= $scope.addedComments[disapprovedRequest.requestId];
                    }
                    ReviewRequestApi.save(params).$promise.then(
                        function (data) {
                            console.log("Success!: ", data);
                        },
                        function (data) {
                            console.log("ERROR: ", data);
                        }
                    );
                });
                modals.resolve();
                $scope.updateLists();
            };
        }
    }
}
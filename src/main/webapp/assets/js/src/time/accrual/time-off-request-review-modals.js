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
essApp.directive('timeOffRequestReviewModal', ['appProps', 'modals', 'LocationService', timeOffRequestReviewModal]);
essApp.directive('timeOffRequestApproveSubmitModal', ['modals', 'appProps', 'ReviewRequestApi', timeOffRequestApproveSubmitModal]);


function timeOffRequestReviewModal(appProps, modals, locationService) {
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
        $scope.accrualsPost = $scope.accruals;
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
        };

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
            console.log("1");
            if ($scope.submissionEmpty()) {
                console.log("2");
                modals.reject();
                console.log("3");
                return;
            }
            console.log("4");
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
                    locationService.scrollToId($scope.requests[iAdj].requestId);
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
        }

        /**
         * Selects the previous request. For a smarter approach see selectNextPendingRequest()
         */
        function selectPreviousRequest() {
            var previousIndex = $scope.iSelectedRequest - 1;
            if (previousIndex >= 0) {
                $scope.iSelectedRequest = previousIndex;
            }
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
            };
        }
    }
}
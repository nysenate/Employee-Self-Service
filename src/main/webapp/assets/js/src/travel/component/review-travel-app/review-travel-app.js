var essTravel = angular.module("essTravel");

essTravel.controller("ReviewTravelAppCtrl", ["$scope", "$q", "modals", "LocationService", "ApplicationReviewApi", "TravelRoleService", reviewController
]);

function reviewController($scope, $q, modals, locationService, appReviewApi, roleService) {

    const APP_ID_SEARCH_PARAM = "appId";
    const ISO_FORMAT = "YYYY-MM-DD";

    var vm = this;
    vm.modalPromise;
    vm.isLoading = true;
    vm.reviews = {
        all: [],
        toReview: [], // App Reviews waiting to be reviewed by the selected role.
        shared: []    // Shared reviews are visible to both the Travel Admin and SOS in separate queues.
    };
    vm.appIdToReview = new Map(); // Map of TravelApplication id to its ApplicationReview
    vm.userRoles = [];
    vm.activeRole = {};

    (function init() {
        roleService.roles()
            .then(function (response) {
                vm.userRoles = response.roles;
                vm.activeRole = vm.userRoles[vm.userRoles.length - 1];
                queryPendingAppReviews()
                    .then(getSharedReviews)
                    .then(removeDuplicates)
                    .then(initAppIdToReviewMap)
                    .then(sortReviews)
                    .then(openReviewModalIfSearchParamsSet)
                    .then(function () {
                        vm.isLoading = false;
                    })
            });
    })();

    /**
     * Get ApplicationReviews pending review by all of the users roles.
     * Displayed ApplicationReviews will be filtered on the front end to show only those for the selected role.
     * This is done so linking to this page with a preset appId will always work.
     * @return {*}
     */
    function queryPendingAppReviews() {
        var roleNames = vm.userRoles.map(function (role) {
            return role.name;
        });
        return appReviewApi.pendingReviews(roleNames)
            .$promise
            .then(appReviewApi.parseAppReviewResponse)
            .then(function (appReviews) {
                vm.reviews.all = appReviews;
            })
            .catch($scope.handleErrorResponse);
    }

    function getSharedReviews() {
        return appReviewApi.sharedReviews()
            .$promise
            .then(appReviewApi.parseAppReviewResponse)
            .then(function (sharedReviews) {
                vm.reviews.all = vm.reviews.all.concat(sharedReviews);
            })
            .catch($scope.handleErrorResponse);
    }

    function removeDuplicates() {
        vm.reviews.all = _.uniq(vm.reviews.all, false, function(n) { return n.appReviewId })
    }

    function initAppIdToReviewMap() {
        vm.reviews.all.forEach(function (review) {
            vm.appIdToReview.set(review.travelApplication.id, review);
        })
    }

    function sortReviews() {
        vm.reviews.toReview = [];
        vm.reviews.shared = [];
        vm.reviews.all.forEach(function (r) {
            // Add non shared reviews
            if (!r.isShared) {
                if (r.nextReviewerRole === vm.activeRole.name) {
                    vm.reviews.toReview.push(r);
                }
            }
            // Show shared reviews if the active role is the travel admin or SOS.
            if (r.isShared && (vm.activeRole.name === 'TRAVEL_ADMIN' || vm.activeRole.name === 'SECRETARY_OF_THE_SENATE')) {
                vm.reviews.shared.push(r);
            }
        });
        sortByStartDate(vm.reviews.toReview);
        sortByStartDate(vm.reviews.shared);
        console.log(vm);
    }

    function sortByStartDate(appReviews) {
        appReviews.sort(function (a, b) {
            return moment(a.travelApplication.activeAmendment.startDate, ISO_FORMAT).format('x')
                - moment(b.travelApplication.activeAmendment.startDate, ISO_FORMAT).format('x');
        })
    }

    function openReviewModalIfSearchParamsSet() {
        var appId = getAppIdParam();
        if (appId) {
            openReviewModal(vm.appIdToReview.get(appId));
        }
    }

    vm.onRowClick = function (review) {
        openReviewModal(review);
    };

    /**
     * Open a Review modal displaying a ApplicationReview
     * If review is undefined or null clear the search param and don't display a modal.
     */
    function openReviewModal(review) {
        if (review) {
            locationService.setSearchParam(APP_ID_SEARCH_PARAM, review.travelApplication.id);
            vm.modalPromise = modals.open("app-review-action-modal", {review: review, role: vm.activeRole}, true)
                .then(reload)
                .finally(resetAppIdParam);
        } else {
            resetAppIdParam();
        }
    }

    vm.onActiveRoleChange = function () {
        filterReviews();
    };

    function reload() {
        locationService.go("/travel/manage/review", true);
    }

    function getAppIdParam() {
        return locationService.getSearchParam(APP_ID_SEARCH_PARAM);
    }

    function resetAppIdParam() {
        locationService.setSearchParam(APP_ID_SEARCH_PARAM, null);
    }

    /**
     * Used by the app-review-action-modal when the user clicks on the edit link.
     *
     * This logic seems to work best outside the modal since it involves setting
     * the appId requestParameter which is also used by this modal.
     * If this logic is in the modal, the appId request param is set inconsistently when
     * loading the edit page.
     */
    vm.onEdit = function () {
        var appId = getAppIdParam();
        modals.reject();
        $q.all(vm.modalPromise).then(function () {
            locationService.go("/travel/application/edit", false, {appId: appId});
        });
    }
}

var essTravel = angular.module("essTravel");

essTravel.controller("AppReviewCtrl", ["$scope", "$q", "modals", "LocationService", "ApplicationReviewApi", "TravelRoleService", reviewController
]);

function reviewController($scope, $q, modals, locationService, appReviewApi, roleService) {

    const APP_ID_SEARCH_PARAM = "appId";

    var vm = this;
    vm.modalPromise;
    vm.isLoading = true;
    vm.reviews = [];
    vm.appIdToReview = new Map(); // Map of TravelApplication id to its ApplicationReview
    vm.userRoles = [];

    (function init() {
        roleService.roles()
            .then(function (response) {
                vm.userRoles = response.roles;
                initPendingAppReviews()
                    .then(openReviewModalIfSearchParamsSet);
            });
    })();

    function initPendingAppReviews() {
        // TODO Use user selected role
        return appReviewApi.pendingReviews(vm.userRoles[vm.userRoles.length - 1].name)
            .$promise
            .then(appReviewApi.parseAppReviewResponse)
            .then(function (appReviews) {
                appReviews.forEach(function (review) {
                    vm.reviews.push(review);
                    vm.appIdToReview.set(review.travelApplication.id, review);
                });
                vm.isLoading = false;
            })
            .catch($scope.handleErrorResponse);
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
            vm.modalPromise = modals.open("app-review-action-modal", review, true)
                .then(reload)
                .finally(resetAppIdParam);
        } else {
            resetAppIdParam();
        }
    }

    function reload() {
        locationService.go("/travel/review", true);
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
     * This logic needs to be outside the modal since the app-review and app-edit pages
     * both use appId as a search param. Issues can occur setting it correctly if we go
     * to the edit page before completely closing the modal because closing the modal
     * will remove the same search params that the link to the edit page is adding.
     */
    vm.onEdit = function () {
        var appId = getAppIdParam();
        modals.reject();
        $q.all(vm.modalPromise).then(function () {
            locationService.go("/travel/application/edit", false, {appId: appId});
        });
    }
}

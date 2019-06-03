var essTravel = angular.module("essTravel");

essTravel.controller("AppReviewCtrl", ["$scope", "$q", "modals", "LocationService", "ApplicationReviewApi", reviewController
]);

function reviewController($scope, $q, modals, locationService, appReviewApi) {

    const APP_ID_SEARCH_PARAM = "appId";

    var vm = this;
    vm.modalPromise;
    vm.isLoading = true;
    vm.apps = [];
    vm.appIdToReview = new Map(); // Map of TravelApplication id to its ApplicationReview

    (function init() {
        initPendingAppReviews()
            .then(openReviewModalIfSearchParamsSet);
    })();

    function initPendingAppReviews() {
        return appReviewApi.pendingReviews().then(function (appReviews) {
            appReviews.forEach(function (review) {
                vm.apps.push(review.travelApplication);
                vm.appIdToReview.set(review.travelApplication.id, review);
            });
            vm.isLoading = false;
        });
    }

    function openReviewModalIfSearchParamsSet() {
        var appId = getAppIdParam();
        if (appId) {
            openReviewModal(appId);
        }
    }

    vm.onAppRowClick = function (app) {
        openReviewModal(app.id);
    };

    /**
     * Open a Review modal displaying the ApplicationReview for the given appId.
     * If no ApplicationReview exists for the given appId, clear the search param and don't display a modal.
     */
    function openReviewModal(appId) {
        locationService.setSearchParam(APP_ID_SEARCH_PARAM, appId);
        var appReview = vm.appIdToReview.get(appId);
        if (appReview) {
            vm.modalPromise = modals.open("app-review-action-modal", appReview, true)
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
            locationService.go("/travel/application/edit", true, {appId: appId});
        });
    }
}

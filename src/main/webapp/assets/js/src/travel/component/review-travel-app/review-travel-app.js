var essTravel = angular.module("essTravel");

essTravel.controller("ReviewTravelAppCtrl", ["$scope", "$q", "modals", "LocationService", "ApplicationReviewApi",
                                             "TravelRoleService", reviewController
]);

function reviewController($scope, $q, modals, locationService, appReviewApi, roleService) {

    const APP_ID_SEARCH_PARAM = "appId";
    const ROLE_SEARCH_PARAM = "role";
    const ISO_FORMAT = "YYYY-MM-DD";

    var vm = this;
    vm.modalPromise;
    vm.isLoading = true;
    vm.reviews = {
        byRole: {},
        toReview: [], // App Reviews waiting to be reviewed by the selected role.
        shared: []    // Shared reviews are visible to both the Travel Admin and SOS in separate queues.
    };
    vm.appIdToReview = new Map(); // Map of TravelApplication id to its ApplicationReview
    vm.userRoles = [];
    vm.activeRole = {};

    function init() {
        vm.isLoading = true;
        // vm.activeRole = locationService.getSearchParam("role");
        roleService.roles()
            .then(function (response) {
                vm.userRoles = _.uniq(response.allRoles, function (r) {
                    return r.name
                }); // Unique roles.
                queryPendingAppReviews()
                    .then(getSharedReviews)
                    .then(initAppIdToReviewMap)
                    .then(setRoleLabels)
                    .then(setInitialRole)
                    .then(openReviewModalIfSearchParamsSet)
                    .then(function () {
                        updateToReview(vm.activeRole);
                        vm.isLoading = false;
                    })
            });
    }

    /**
     * Get ApplicationReviews pending review by all of the users roles.
     * Displayed ApplicationReviews will be filtered on the front end to show only those for the selected role.
     * This is done so linking to this page with a preset appId will always work.
     * @return {*}
     */
    function queryPendingAppReviews() {
        return appReviewApi.pendingReviews()
            .$promise
            .then(appReviewApi.parseAppReviewResponse)
            .then(function (appReviews) {
                vm.reviews.byRole = appReviews.items;
            })
            .catch($scope.handleErrorResponse);
    }

    function getSharedReviews() {
        return appReviewApi.sharedReviews()
            .$promise
            .then(appReviewApi.parseAppReviewResponse)
            .then(function (sharedReviews) {
                vm.reviews.shared = sharedReviews;
                sortByStartDate(vm.reviews.shared);
            })
            .catch($scope.handleErrorResponse);
    }

    function initAppIdToReviewMap() {
        vm.userRoles.forEach(function (role) {
            vm.reviews.byRole[role.name].forEach(function (review) {
                vm.appIdToReview.set(review.travelApplication.id, review);
            })
        })
    }

    function setRoleLabels() {
        vm.userRoles.forEach(function (role) {
            var reviewCount = vm.reviews.byRole[role.name].length;
            role.label = role.displayName + ' - (' + reviewCount + ') Pending'
        })
    }

    function setInitialRole() {
        var roleName = locationService.getSearchParam(ROLE_SEARCH_PARAM);
        if (roleName == undefined) {
            vm.activeRole = vm.userRoles[vm.userRoles.length - 1];
        } else {
            for (var i = 0; i < vm.userRoles.length; i++) {
                var el = vm.userRoles[i];
                if (el.name === roleName) {
                    vm.activeRole = el;
                }
            }
        }
    }

    // Update the `toReview` array to the reviews associated with the selected role.
    // Removes any reviews that have been shared, they will be displayed by `vm.reviews.shared`.
    function updateToReview(role) {
        vm.reviews.toReview = vm.reviews.byRole[role.name]
        vm.reviews.toReview = vm.reviews.toReview.filter(function (r) {
            return !r.isShared
        })
        sortByStartDate(vm.reviews.toReview);
    }

    function sortByStartDate(appReviews) {
        appReviews.sort(function (a, b) {
            return moment(a.travelApplication.activeAmendment.startDate, ISO_FORMAT).format('x')
                - moment(b.travelApplication.activeAmendment.startDate, ISO_FORMAT).format('x');
        })
    }

    function openReviewModalIfSearchParamsSet() {
        var appId = parseInt(getAppIdParam())
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
                .then(init)
                .finally(resetAppIdParam);
        } else {
            resetAppIdParam();
        }
    }

    vm.onActiveRoleChange = function () {
        locationService.setSearchParam(ROLE_SEARCH_PARAM, vm.activeRole.name);
        updateToReview(vm.activeRole);
    };

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
            locationService.go("/travel/application/edit", false, {appId: appId, role: vm.activeRole.name});
        });
    }

    vm.roleSelectBackgroundColor = function () {
        switch (vm.activeRole.name) {
            case "DEPARTMENT_HEAD":
                return "orange";
                break;
            case "TRAVEL_ADMIN":
                return "teal"
                break;
            case "SECRETARY_OF_THE_SENATE":
                return "green"
                break;
        }
    }

    init();
}

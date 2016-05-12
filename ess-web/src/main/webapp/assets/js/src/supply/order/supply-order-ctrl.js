var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'appProps', 'SupplyItemsApi', 'SupplyCategoryService', 'LocationService',
        'SupplyCart', 'PaginationModel', 'SupplyLocationAutocompleteService', 'EmpInfoApi', supplyOrderController]);

function supplyOrderController($scope, appProps, itemsApi, supplyCategoryService, locationService, supplyCart,
                               paginationModel, locationAutocompleteService, employeeApi) {

    $scope.state = {};
    $scope.states = {
        SELECTING_DESTINATION: 5,
        SHOPPING: 10
    };

    $scope.itemSearch = {
        matches: [],
        paginate: angular.extend({}, paginationModel),
        categories: [],
        response: {},
        error: false
    };

    $scope.destination = {
        code: "",       // The code entered by the user.
        location: {}    // The location object associated with the code.
    };

    $scope.quantity = 1;

    /** --- Initialization --- */

    $scope.init = function () {
        $scope.state = $scope.states.SELECTING_DESTINATION;
        $scope.itemSearch.paginate.itemsPerPage = 16;
        initLocationToEmpWorkLocation();
        manageUrlParams();
        getItems();
    };

    function initLocationToEmpWorkLocation() {
        employeeApi.get({empId: appProps.user.employeeId, detail: true}, function (response) {
            $scope.destination.location = response.employee.empWorkLocation;
            $scope.destination.code = response.employee.empWorkLocation.code;
        });
    }

    function manageUrlParams() {
        $scope.itemSearch.categories = locationService.getSearchParam("category") || [];
        $scope.itemSearch.paginate.currPage = locationService.getSearchParam("page") || 1;
        // Set page param if not in url.
        locationService.setSearchParam("page", $scope.itemSearch.paginate.currPage, true, true);
    }

    function getItems(resetPagination) {
        if (resetPagination) {
            $scope.itemSearch.paginate.reset();
            setPageUrlParams();
        }
        var params = {
            category: $scope.itemSearch.categories,
            limit: $scope.itemSearch.paginate.getLimit(),
            offset: $scope.itemSearch.paginate.getOffset()
        };
        $scope.itemSearch.response = itemsApi.get(params, function (response) {
            $scope.itemSearch.matches = response.result;
            $scope.itemSearch.paginate.setTotalItems(response.total);
            $scope.itemSearch.error = false;
        }, function (errorResponse) {
            $scope.itemSearch.matches = [];
            $scope.itemSearch.error = true;
        })
    }

    /** --- Navigation --- */

    $scope.onPageChange = function () {
        setPageUrlParams();
        getItems(false);
    };

    function setPageUrlParams() {
        locationService.setSearchParam("page", $scope.itemSearch.paginate.currPage, true, false);
    }

    /**
     * Detect url category param changes due to category side bar selections or back/forward browser navigation.
     * Need to reset the item matches when category search criteria changes.
     */
    $scope.$on('$locationChangeStart', function (event, newUrl) {
        if (newUrl.indexOf(appProps.ctxPath + "/supply/order") > -1) { // If still on order page.
            var urlCategories = locationService.getSearchParam("category") || [];
            if (!_.isEqual(urlCategories, $scope.itemSearch.categories)) { // If the category param changed.
                $scope.itemSearch.categories = urlCategories;
                getItems(true);
            }
        }
    });

    /** --- Shopping --- */

    $scope.addToCart = function (item, qty) {
        supplyCart.addToCart(item, qty);
    };

    $scope.isInCart = function (item) {
        return supplyCart.itemInCart(item.id)
    };

    $scope.orderQuantityRange = function (item) {
        var range = [];
        for (var i = 1; i <= item.suggestedMaxQty * 2; i++) {
            range.push(i);
        }
        return range;
    };

    /** --- Location selection --- */

    $scope.setDestination = function () {
        if (locationAutocompleteService.isValidCode($scope.destination.code)) {
            $scope.destination.location = locationAutocompleteService.getLocationFromCode($scope.destination.code);
            $scope.state = $scope.states.SHOPPING;
        }
    };

    $scope.getLocationAutocompleteOptions = function () {
        return locationAutocompleteService.getLocationAutocompleteOptions();
    };

    $scope.init();
}

/**
 * Directive for validating destination selection.
 */
essSupply.directive('destinationValidator', ['SupplyLocationAutocompleteService', function (locationAutocompleteService) {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.destination = function (modelValue, viewValue) {
                return locationAutocompleteService.isValidCode(modelValue) || modelValue.length === 0;
            }
        }
    }
}]);

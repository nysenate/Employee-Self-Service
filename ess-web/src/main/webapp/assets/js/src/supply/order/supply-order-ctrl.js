var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'appProps', 'LocationService', 'SupplyCartService', 'PaginationModel', 'SupplyLocationAutocompleteService',
        'SupplyLocationAllowanceService', 'OrderDestinationService', supplyOrderController]);

function supplyOrderController($scope, appProps, locationService, supplyCart, paginationModel, locationAutocompleteService,
                               allowanceService, destinationService) {
    $scope.state = {};
    $scope.states = {
        LOADING: 0,
        SELECTING_DESTINATION: 5,
        SHOPPING: 10
    };

    $scope.paginate = angular.extend({}, paginationModel);

    $scope.filter = {
        categories: []
    };

    // An array of allowances which match the current filters.
    $scope.displayAllowances = [];

    // The user specified destination code. Defaults to the code of the employees work location.
    $scope.destinationCode = "";

    /** --- Initialization --- */


    $scope.init = function () {
        $scope.paginate.itemsPerPage = 16;
        $scope.destinationCode = destinationService.getDefaultCode();
        initializeState();
        manageUrlParams();
    };

    $scope.init();

    /** --- State --- */

    function initializeState() {
        $scope.state = $scope.states.SELECTING_DESTINATION;
        if (destinationService.isDestinationConfirmed()) {
            transitionToShoppingState();
        }
    }

    function transitionToShoppingState() {
        $scope.state = $scope.states.LOADING;
        allowanceService.queryLocationAllowance(destinationService.getDestination())
            .then(filterAllowances)
            .then(setToShoppingState);
    }

    function filterAllowances() {
        $scope.displayAllowances = allowanceService.getFilteredAllowances($scope.filter.categories);
    }

    function setToShoppingState() {
        $scope.state = $scope.states.SHOPPING;
    }

    /**
     * Synchronizes the categories and currPage objects with the values in the url.
     */
    function manageUrlParams() {
        $scope.filter.categories = locationService.getSearchParam("category") || [];
        $scope.paginate.currPage = locationService.getSearchParam("page") || 1;
        // Set page param. This ensures it gets set to 1 if it was never previously set.
        locationService.setSearchParam("page", $scope.paginate.currPage, true, true);
    }

    /** --- Navigation --- */

    /**
     * Set the page url parameter when the user changes the page.
     * Triggers the $on('$locationChangeStart') event which will update url params and filter allowances.
     */
    $scope.onPageChange = function () {
        locationService.setSearchParam("page", $scope.paginate.currPage, true, false);
    };

    /**
     * Detect url param changes due to category side bar selections, page change, or back/forward browser navigation.
     * Update local $scope params to match the new url params and filter allowances for any categories specified.
     */
    $scope.$on('$locationChangeStart', function (event, newUrl) {
        if (newUrl.indexOf(appProps.ctxPath + "/supply/order") > -1) { // If still on order page.
            manageUrlParams();
            filterAllowances();
        }
    });

    /** --- Shopping --- */

    $scope.addToCart = function (allowance) {
        supplyCart.addToCart(allowance.item, allowance.selectedQuantity);
    };

    $scope.isInCart = function (item) {
        return supplyCart.itemInCart(item.id)
    };

    $scope.getAllowedQuantities = function (allowance) {
        return allowanceService.getAllowedQuantities(allowance);
    };

    /** --- Location selection --- */

    $scope.confirmDestination = function () {
        var success = destinationService.setDestination($scope.destinationCode);
        if (success) {
            transitionToShoppingState();
        }
    };

    $scope.getLocationAutocompleteOptions = function () {
        return locationAutocompleteService.getLocationAutocompleteOptions();
    };
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

/**
 * OrderDestinationService is responsible for storing and changing the user selected destination.
 */
essSupply.service('OrderDestinationService', ['appProps', 'EmpInfoApi', 'SupplyLocationAutocompleteService', orderDestinationService]);
function orderDestinationService(appProps, empInfoApi, locationAutocompleteService) {

    var defaultCode = undefined;
    var destination = undefined;

    return {
        queryDefaultDestination: function () {
            if (!defaultCode) {
                return empInfoApi.get({empId: appProps.user.employeeId, detail: true}, function (response) {
                    defaultCode = response.employee.empWorkLocation.code;
                }).$promise
            }
        },

        isDestinationConfirmed: function () {
            return destination !== undefined;
        },

        /**
         * Sets the destination corresponding to the given code.
         * If code is valid sets the destination, otherwise returns false.
         */
        setDestination: function (code) {
            if (locationAutocompleteService.isValidCode(code)) {
                destination = locationAutocompleteService.getLocationFromCode(code);
                return true;
            }
            return false;
        },

        getDefaultCode: function () {
            return defaultCode;
        },

        getDestination: function () {
            return destination;
        }
    }
}

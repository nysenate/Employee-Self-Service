var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'appProps', 'LocationService', 'SupplyCart', 'PaginationModel', 'SupplyLocationAutocompleteService',
        'SupplyLocationAllowanceApi', 'SupplyUtils', 'OrderDestinationService', supplyOrderController]);

function supplyOrderController($scope, appProps, locationService, supplyCart, paginationModel, locationAutocompleteService,
                               locationAllowanceApi, supplyUtils, destinationService) {
    $scope.state = {};
    $scope.states = {
        LOADING: 0,
        SELECTING_DESTINATION: 5,
        SHOPPING: 10
    };

    // Information on items available at the selected location.
    $scope.inventory = {
        response: {},
        paginate: angular.extend({}, paginationModel),
        allowances: []
    };

    // An array of allowances which match the current filters.
    $scope.displayAllowances = [];

    $scope.filter = {
        categories: []
    };

    $scope.destinationCode = "";

    $scope.quantity = 1;

    /** --- Initialization --- */

    $scope.init = function () {
        initializeState();
        manageUrlParams();
        $scope.inventory.paginate.itemsPerPage = 16;
        $scope.destinationCode = destinationService.getDefaultCode();
    };

    /**
     * Synchronizes the categories and currPage objects with the values in the url.
     * Sets page url param to 1 if not page is set.
     */
    function manageUrlParams() {
        $scope.filter.categories = locationService.getSearchParam("category") || [];
        $scope.inventory.paginate.currPage = locationService.getSearchParam("page") || 1;
        locationService.setSearchParam("page", $scope.inventory.paginate.currPage, true, true);
    }

    /**
     * Get the item allowance for the selected destination.
     */
    function getLocationAllowance() {
        $scope.inventory.response = locationAllowanceApi.get({id: destinationService.getDestinationLocationId()}, function (response) {
            $scope.inventory.allowances = supplyUtils.alphabetizeAllowances(response.result.itemAllowances);
        });
        return $scope.inventory.response.$promise;
    }

    /** --- State --- */

    function initializeState() {
        $scope.state = $scope.states.SELECTING_DESTINATION;
        if (destinationService.isDesinationConfirmed()) {
            transitionToShoppingState();
        }
    }

    function transitionToShoppingState() {
        $scope.state = $scope.states.LOADING;
        getLocationAllowance()
            .then(filterAllowances)
            .then(setToShoppingState);
    }

    function setToShoppingState() {
        $scope.state = $scope.states.SHOPPING;
    }

    /** --- Navigation --- */

    /**
     * Set the page url parameter when the user changes the page.
     */
    $scope.onPageChange = function () {
        locationService.setSearchParam("page", $scope.inventory.paginate.currPage, true, false);
    };

    /**
     * Detect url category param changes due to category side bar selections or back/forward browser navigation.
     * Need to reset the displayed allowances when category search criteria changes.
     */
    $scope.$on('$locationChangeStart', function (event, newUrl) {
        if (newUrl.indexOf(appProps.ctxPath + "/supply/order") > -1) { // If still on order page.
            var urlCategories = locationService.getSearchParam("category") || [];
            if (!_.isEqual(urlCategories, $scope.filter.categories)) { // If the category param changed.
                $scope.filter.categories = urlCategories;
                filterAllowances();
            }
        }
    });

    /** --- Shopping --- */

    /**
     * Updates the displayed allowances to only contain the allowances that fit in the current filters.
     */
    function filterAllowances() {
        // If no categories selected, display all items/allowances
        if ($scope.filter.categories.length === 0) {
            $scope.displayAllowances = $scope.inventory.allowances;
        }
        else {
            // Display those that match the selected categories.
            $scope.displayAllowances = [];
            angular.forEach($scope.inventory.allowances, function (allowance) {
                if ($scope.filter.categories.indexOf(allowance.item.category.name) !== -1) {
                    $scope.displayAllowances.push(allowance);
                }
            })
        }
    }

    $scope.addToCart = function (item, allowance) {
        supplyCart.addToCart(item, allowance, allowance.selectedQuantity);
    };

    $scope.isInCart = function (item) {
        return supplyCart.itemInCart(item.id)
    };

    /**
     * Returns an array with integers from 1 to the per order allowance for an allowance.
     */
    $scope.oneToPerOrderAllowanceRange = function (allowance) {
        // TODO: tempoary adjustment of per order allowances since database is inaccurate.
        if (allowance.perOrderAllowance === 0) {
            allowance.perOrderAllowance = 2;
        }
        var range = [];
        for (var i = 1; i <= allowance.perOrderAllowance; i++) {
            range.push(i);
        }
        return range;
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

essSupply.service('OrderDestinationService', ['appProps', 'EmpInfoApi', 'SupplyLocationAutocompleteService',
    function (appProps, empInfoApi, locationAutocompleteService) {

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

            isDesinationConfirmed: function () {
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
            },

            getDestinationLocationId: function () {
                return this.getDestination().locId;
            }

        }
    }]);

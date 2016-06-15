var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'appProps', 'LocationService', 'SupplyCartService', 'PaginationModel', 'SupplyLocationAutocompleteService',
        'SupplyLocationAllowanceService', 'SupplyOrderDestinationService', 'modals', supplyOrderController]);

function supplyOrderController($scope, appProps, locationService, supplyCart, paginationModel, locationAutocompleteService,
                               allowanceService, destinationService, modals) {
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
        initializeShoppingCart();
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

    function initializeShoppingCart() {
        supplyCart.init();
    }

    $scope.addToCart = function (allowance) {
        if (!supplyCart.itemInCart(allowance.item.id) && allowance.visibility === 'SPECIAL') {
            modals.open('special-order-item-modal', {allowance: allowance});
        }
        else if (supplyCart.isOverOrderAllowance(allowance.item, allowance.selectedQuantity)) {
            modals.open('order-more-modal', {item: allowance.item, type: 'order'});
        }
        else if (supplyCart.isOverMonthlyAllowance(allowance.item, allowance.selectedQuantity)) {
            modals.open('order-more-modal', {item: allowance.item, type: 'month'});
        }
        else {
            supplyCart.addToCart(allowance.item, allowance.selectedQuantity);
        }
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
 * Validator for the special order quantity form.
 * Note: The form considers 'e' valid input. I would like to use this validator to mark 'e' as invalid, however
 * this validator is not being called when 'e' characters are entered...
 */
essSupply.directive('wholeNumberValidator', [function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.wholeNumber = function (modelValue, viewValue) {
                return modelValue % 1 === 0 && modelValue !== null;
            };
        }
    }
}]);

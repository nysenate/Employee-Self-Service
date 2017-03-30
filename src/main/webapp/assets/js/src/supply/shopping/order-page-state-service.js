angular.module('essSupply')
    .factory('SupplyOrderPageStateService', ['$rootScope', supplyOrderPageStateService]);

/**
 * Uses the Observer pattern to call subscribed functions when a state change occurs
 * on the order page.
 *
 * Contains simple methods for setting and accessing the current state.
 */
function supplyOrderPageStateService($rootScope) {

    var state;
    var states = {
        LOADING: 0,
        SELECTING_DESTINATION: 5,
        SHOPPING: 10,
        INVALID: 15 // When not on order page, state should be invalid.
    };

    var EVENT = 'supply-order-page-state-change';

    /**
     * Execute all subscribed functions.
     */
    function notify () {
        $rootScope.$emit(EVENT);
    }

    return {
        /**
         * Register a callback function to be called when a state change occurs.
         *
         * This will automatically remove the callback when the given scope is destroyed.
         * @param scope The scope the callback function is associated with.
         * @param callback The callback function.
         */
        subscribe: function (scope, callback) {
            var handler = $rootScope.$on(EVENT, callback);
            scope.$on('$destroy', handler);
        },

        isLoading: function () {
            return state === states.LOADING;
        },

        isSelectingDestination: function () {
            return state === states.SELECTING_DESTINATION;
        },

        isShopping: function () {
            return state === states.SHOPPING;
        },

        toLoading: function () {
            state = states.LOADING;
            notify();
        },

        toSelectingDestination: function () {
            state = states.SELECTING_DESTINATION;
            notify();
        },

        toShopping: function () {
            state = states.SHOPPING;
            notify();
        },

        toInvalid: function () {
            state = states.INVALID;
            notify();
        }
    }
}
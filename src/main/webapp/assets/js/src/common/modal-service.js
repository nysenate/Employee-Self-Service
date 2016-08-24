var essApp = angular.module('ess');

/**
 * A service that manages promise based modals
 * This service manages the promises and data behind the modals, 
 * See modal-directive.js for details on the display of modals
 */
essApp.service('modals', ['$rootScope', '$q', function($rootScope, $q) {

    // State of the active modal instances
    var modals = [];

    return { // exposed methods
        open: open,
        params: params,
        reject: reject,
        rejectAll: rejectAll,
        softReject: softReject,
        resolve: resolve
    };

    /** --- Public Methods --- */

    /**
     * Opens a new modal
     * 
     * @param type - specifies which modal will open
     * @param params - object containing passed parameters
     * @param softRejectable - if true, the modal is easily closable via esc key, click on backdrop etc..
     * @returns {Promise}
     */
    function open(type, params, softRejectable) {
        var modal = {
            deferred: $q.defer(),
            params: params,
            softRejectable: softRejectable === true
        };

        $rootScope.$emit("modals.open", type);
        modals.push(modal);
        return modal.deferred.promise;
    }

    /**
     * @returns {*} - the parameters of the currently active modal
     */
    function params() {
        var modal = modals[modals.length - 1];
        if (modal) {
            return modal.params || {};
        }
        return {};
    }

    /**
     * Rejects the active modal
     * @param reason - data associated with rejection
     */
    function reject(reason) {
        var modal = modals.pop();
        if (!modal) {
            return;
        }

        modal.deferred.reject(reason);
        console.log('rejecting modal',modal, reason);
        $rootScope.$emit("modals.close");
    }

    /**
     * Rejects all open modals
     * @param reason
     */
    function rejectAll(reason) {
        while(modals.length > 0) {
            reject(reason);
        }
    }

    /**
     * Rejects the active modal if it is softRejectable
     * This is used by the modal directive to close a modal via esc key or backdrop click
     * @param reason
     */
    function softReject(reason) {
        var modal = modals[modals.length - 1];
        if (modal.softRejectable) {
            reject(reason);
        }
    }

    /**
     * Resolves the active modal
     * @param response - resolution data
     */
    function resolve(response) {
        var modal = modals.pop();
        if (!modal) {
            return;
        }

        modal.deferred.resolve(response);
        $rootScope.$emit("modals.close");
    }

}]);
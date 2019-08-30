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
        isOpen: isOpen,
        isTop: isTop,
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
     * @param modalId - specifies which modal will open
     * @param params - object containing passed parameters
     * @param softRejectable - if true, the modal is easily closable via esc key, click on backdrop etc..
     * @returns {Promise}
     */
    function open(modalId, params, softRejectable) {
        if (!modalId) {
            return $q.defer();
        }
        var modal = {
            modalId: modalId,
            deferred: $q.defer(),
            params: params,
            softRejectable: softRejectable === true
        };

        modal.deferred.promise.then(function () {
            console.log('resolved modal', modalId);
        }, function () {
            console.log('rejected modal', modalId);
        });

        modals.unshift(modal);
        return modal.deferred.promise;
    }

    function isOpen(modalId) {
        if (modalId === undefined) {
            return modals.length > 0;
        }
        var found = false;
        angular.forEach(modals, function(modal) {
            if (modal.modalId === modalId) {
                found = true;
            }
        });
        return found;
    }

    function isTop(modalId) {
        return modals.length > 0 && modals[0].modalId === modalId;
    }

    /**
     * @returns {*} - the parameters of the currently active modal
     */
    function params() {
        var modal = modals[0];
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
        var modal = modals.shift();
        if (!modal) {
            return;
        }

        modal.deferred.reject(reason);
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
        var modal = modals[0];
        if (modal && modal.softRejectable) {
            reject(reason);
        }
    }

    /**
     * Resolves the active modal
     * @param response - resolution data
     */
    function resolve(response) {
        var modal = modals.shift();
        if (!modal) {
            return;
        }

        modal.deferred.resolve(response);
    }

}]);
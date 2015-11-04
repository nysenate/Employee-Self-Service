var essApp = angular.module('ess');

essApp.service('modals', ['$rootScope', '$q', function($rootScope, $q) {

    // State of the active modal instances
    var modals = [];
    var modal = {
        deferred: null,
        params: null
    };

    return { // exposed methods
        open: open,
        params: params,
        reject: reject,
        rejectAll: rejectAll,
        resolve: resolve
    };

    /** --- Public Methods --- */

    function open(type, params, pipeResponse) {
        var modal = {
            deferred: $q.defer(),
            params: params
        };

        if (modals.length > 0 && pipeResponse) {
            var prevDeferred = modals[modals.length - 1].deferred;
            modal.deferred.promise.then(resolve, reject);
        }

        $rootScope.$emit("modals.open", type);
        modals.push(modal);
        return modal.deferred.promise;
    }

    function params() {
        var modal = modals[modals.length - 1];
        if (modal) {
            return modal.params || {};
        }
        return {};
    }

    function reject(reason) {
        var modal = modals.pop();
        if (!modal) {
            return;
        }

        modal.deferred.reject(reason);
        console.log('rejecting modal',modal, reason);
        $rootScope.$emit("modals.close");
    }

    function rejectAll(reason) {
        while(modals.length > 0) {
            reject(reason);
        }
    }

    function resolve(response) {
        var modal = modals.pop();
        if (!modal) {
            return;
        }

        modal.deferred.resolve(response);
        $rootScope.$emit("modals.close");
    }

}]);
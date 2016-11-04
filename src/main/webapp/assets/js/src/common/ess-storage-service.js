var ess = angular.module('ess');
ess.service('EssStorageService', ['$window', 'appProps', essStorageService]);

function essStorageService($window, appProps) {

    var userId = appProps.user.employeeId;

    /**
     * A unique prefix for key values.
     * Helps to ensure other websites don't overwrite our data.
     * @param cart
     */
    var keyPrefix = "nysenate.gov-" + userId + "-";

    return {

        /**
         * Saves a key value pair into local storage.
         * Overwrites any existing value.
         * @param key
         * @param value
         */
        save: function (key, value) {
            $window.localStorage.setItem(keyPrefix + key, JSON.stringify(value));
        },

        /**
         * Loads a key from local storage, returning its associated value or null if its not found.
         * @param key
         */
        load: function (key) {
            return JSON.parse($window.localStorage.getItem(keyPrefix + key));
        },

        /**
         * Remove a value from local storage.
         * @param key identifies the value to be removed.
         */
        remove: function (key) {
            $window.localStorage.removeItem(keyPrefix + key);
        }
    }
}
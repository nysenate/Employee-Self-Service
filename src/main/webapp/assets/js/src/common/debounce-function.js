angular.module('ess')
    .factory('debounce', ['$timeout', debounceFactory]);

function debounceFactory($timeout) {
    return function debounce(func, wait, immediate) {
        var promise;
        return function () {
            var ctx = this, args = arguments;
            function deferred () {
                promise = null;
                if (!immediate) {
                    func.apply(ctx, args);
                }
            }
            var callNow = immediate && !promise;
            $timeout.cancel(promise);
            promise = $timeout(deferred, wait);
            if (callNow) {
                func.apply(ctx, args);
            }
        }
    }
}
var essCore = angular.module('essCore', []);
var essApi = angular.module('essApi', ['essCore']);

var essMyInfo = angular.module('essMyInfo', ['essApi']);
var essTime = angular.module('essTime', ['essApi']);
var essSupply = angular.module('essSupply', ['essApi']);
var essHelp = angular.module('essHelp', ['essApi']);

var essApp = angular.module('ess', [
    // Angular modules
    'ngRoute', 'ngResource', 'ngAnimate',
    // Local modules
    'essMyInfo', 'essTime', 'essSupply', 'essHelp',
    // Third party modules
    'floatThead', 'angularUtils.directives.dirPagination']);

/** Transfers properties stored on the global window var into the root module. */
essCore.constant('appProps', globalProps);

essApi.factory('httpTimeoutChecker', ['appProps', '$window', '$q', function(appProps, $window, $q) {
    var loginUrl = appProps.ctxPath + appProps.loginUrl;
    return {
        response: function (response) {
            // If the response is not from the login page request
            if (response.config.url.indexOf(loginUrl) == -1) {
                // Simple check to see if a blob of characters on the login page is found.
                // It would be nice to have a different way to determine if the response is the
                // login page.
                if (response.data && typeof response.data === 'string' &&
                    response.data.indexOf('0LOKZECwqdNGvKZFy3uB') !== -1) {
                    alert("Sorry, your session has timed out. Please login again.");
                    $window.location.reload(true);
                    $q.reject(response);
                }
            }
            return response;
        }
    };
}]);

essApi.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('httpTimeoutChecker');
}]);



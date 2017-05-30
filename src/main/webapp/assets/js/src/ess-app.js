var essCore = angular.module('essCore', ['ngCookies']);
var essApi = angular.module('essApi', ['essCore']);

var essMyInfo = angular.module('essMyInfo', ['essApi']);
var essTime = angular.module('essTime', ['essApi']);
var essSupply = angular.module('essSupply', ['essApi', 'ngCookies']);
var essHelp = angular.module('essHelp', ['essApi']);

var essApp = angular.module('ess', [
    // Angular modules
    'ngRoute', 'ngResource', 'ngAnimate', 'ngSanitize',
    // Local modules
    'essCore', 'essMyInfo', 'essTime', 'essSupply', 'essHelp',
    // Third party modules
    'floatThead', 'angularUtils.directives.dirPagination', 'ui.autocomplete', 'nsPopover']);

/** Transfers properties stored on the global window var into the root module. */
essCore.constant('appProps', globalProps);

/*Evict all cookies when each time pump the app version*/
essCore.run(['$cookies', function ($cookies) {
    if ($cookies.get("appVersion") === undefined || $cookies.get("appVersion") != globalProps.releaseVersion) {
        var cookies = $cookies.getAll();
        angular.forEach(cookies, function (v, k) {
            $cookies.remove(k);
        });
    }
    $cookies.put("appVersion", globalProps.releaseVersion);
}]);

// disable backspace key from nav
essCore.run(function unbindBackspace() {
    $(document).unbind('keydown').bind('keydown', function (event) {
        var doPrevent = false;
        if (event.keyCode === 8) {
            var d = event.srcElement || event.target;
            if ((d.tagName.toUpperCase() === 'INPUT' &&
                    (
                    d.type.toUpperCase() === 'TEXT' ||
                    d.type.toUpperCase() === 'PASSWORD' ||
                    d.type.toUpperCase() === 'FILE' ||
                    d.type.toUpperCase() === 'SEARCH' ||
                    d.type.toUpperCase() === 'EMAIL' ||
                    d.type.toUpperCase() === 'NUMBER' ||
                    d.type.toUpperCase() === 'DATE' )
                ) ||
                d.tagName.toUpperCase() === 'TEXTAREA') {
                doPrevent = d.readOnly || d.disabled;
            }
            else {
                doPrevent = true;
            }
        }
        if (doPrevent) {
            event.preventDefault();
        }
    });
});

essCore.config(['$httpProvider', function ($httpProvider) {
    $httpProvider.interceptors.push('httpTimeoutChecker');
}]);

essCore.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
    $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache, no-store, max-age=0, must-revalidate';
    $httpProvider.defaults.headers.common['Pragma'] = 'no-cache';
    $httpProvider.defaults.headers.common['Expires'] = '-1';
}]);


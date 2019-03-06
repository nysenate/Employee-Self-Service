var essCore = angular.module('essCore', ['ngCookies']);
var essApi = angular.module('essApi', ['essCore']);

var essMyInfo = angular.module('essMyInfo', ['essApi']);
var essTime = angular.module('essTime', ['essApi']);
var essSupply = angular.module('essSupply', ['essApi', 'ngCookies']);
var essTravel = angular.module('essTravel', ['essApi']);
var essHelp = angular.module('essHelp', ['essApi']);

var essApp = angular.module('ess', [
    // Angular modules
    'ngRoute', 'ngResource', 'ngAnimate', 'ngSanitize',
    // Local modules
    'essCore', 'essMyInfo', 'essTime', 'essSupply', 'essTravel', 'essHelp',
    // Third party modules
    'floatThead', 'angularUtils.directives.dirPagination', 'nsPopover', 'infinite-scroll',
    'jlareau.bowser', 'ui.select']);

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
        if (event.keyCode === 8) {
            var doPrevent = true;
            var types = ["text", "password", "file", "search", "email", "number", "date", "color",
                         "datetime", "datetime-local", "month", "range", "search", "tel", "time", "url", "week"];
            var d = $(event.srcElement || event.target);
            var disabled = d.prop("readonly") || d.prop("disabled");
            if (!disabled) {
                if (d[0].isContentEditable) {
                    doPrevent = false;
                } else if (d.is("input")) {
                    var type = d.attr("type");
                    if (type) {
                        type = type.toLowerCase();
                    }
                    if (types.indexOf(type) > -1) {
                        doPrevent = false;
                    }
                } else if (d.is("textarea")) {
                    doPrevent = false;
                }
            }
            if (doPrevent) {
                event.preventDefault();
                return false;
            }
        }
    });
});

essCore.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
    $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache, no-store, max-age=0, must-revalidate';
    $httpProvider.defaults.headers.common['Pragma'] = 'no-cache';
    $httpProvider.defaults.headers.common['Expires'] = '-1';
}]);

essApp.config(['uiSelectConfig', function(uiSelectConfig) {
    uiSelectConfig.theme = 'select2';
}]);
var essCore = angular.module('essCore', []);
var essApi = angular.module('essApi', ['essCore']);

var essMyInfo = angular.module('essMyInfo', ['essApi']);
var essTime = angular.module('essTime', ['essApi']);
var essSupply = angular.module('essSupply', ['essApi', 'ngCookies']);
var essHelp = angular.module('essHelp', ['essApi']);

var essApp = angular.module('ess', [
    // Angular modules
    'ngRoute', 'ngResource', 'ngAnimate',
    // Local modules
    'essMyInfo', 'essTime', 'essSupply', 'essHelp',
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

/*Time Out*/
essCore.factory('httpTimeoutChecker', ['appProps', function (appProps) {
    return {
        request: function (request) {
            if (globalProps.timeoutExempt == "true") //timeout exempt 
                return request;
            var myvar = '<div id="timeout-confirm" title="Inactive Session Timeout">' +
                '  <p style="padding-left: 35px;padding-top: 10px;"><span class="ui-icon ui-icon-alert" >' +
                '</span><span style="font-size: large; align-content: center;">Due to inactive, this session is about to timeout in <span id="tick">60</span>  seconds, do you want to continues your work?</span></p>' +
                '</div>';
            if ($("#timeout-confirm").length <= 0) {
                $("body").append(myvar);
            }
            $("#timeout-confirm").hide();
            $("#timeout-confirm").dialog();
            $("#timeout-confirm").dialog("close");
            var heartBeatingRate = 5; // in sec
            var idelTime = 0; // in sec.window.location.pathname
            localStorage.setItem("alerted", "false");
            if (localStorage.getItem("hb") == null || window.location.pathname.split("/")[1] != localStorage.getItem("hb").split("/")[1]) {
                localStorage.setItem("hb", window.location.pathname);
                var hb = setInterval(function () {
                    idelTime += heartBeatingRate;
                    $.ajax({
                        type: "GET",
                        url: appProps.apiPath + '/timeout/ping.json?sessionId=' + window.globalProps.sessionId + "&idelTime=" + idelTime,
                        success: function (data) {
                            if (data["message"] > 0 && localStorage.getItem("alerted") == "false") {
                                //timeout in data["message"]  time
                                localStorage.setItem("alerted", "true");
                                var tick = 60;
                                var ticking = setInterval(function () {
                                    if (tick >= 0) {
                                        $("#tick").text(tick);
                                        tick = tick - 1;
                                    }
                                    else {
                                        $.ajax({
                                            type: "GET",
                                            url: appProps.apiPath + '/timeout/ping.json?sessionId=' + window.globalProps.sessionId + "&idelTime=" + -1,
                                            success: function (data) {
                                                window.location.replace(appProps.loginUrl);
                                                window.location.reload(true);
                                            }
                                        });
                                    }
                                }, 1000);
                                $("#timeout-confirm").dialog({
                                    resizable: false,
                                    height: 240,
                                    width: 400,
                                    modal: true,
                                    closeOnEscape: false,
                                    open: function (event, ui) {
                                        $(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
                                        $(".ui-widget-overlay").attr('style', 'position: fixed;top: 0;left: 0; width: 100%;height: 100%; background: #666666 url(ui-bg_diagonals-thick_20_666666_40x40.png) 50% 50% repeat;opacity: .50;filter:Alpha(Opacity=50); ');
                                    },
                                    close: function () {
                                    },
                                    buttons: {
                                        "Continue": function () {
                                            $.ajax({
                                                type: "GET",
                                                url: appProps.apiPath + '/timeout/ping.json?sessionId=' + window.globalProps.sessionId + "&idelTime=" + idelTime
                                            });
                                            $(this).dialog("close");
                                            idelTime = 0;
                                            localStorage.setItem("alerted", "false");
                                            tick = 60;
                                            clearInterval(ticking);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }, heartBeatingRate * 1000);

                $(document).on('change click keydown keypress keyup load  resize scroll select submit', function () {
                    idelTime = 0;
                });

                window.onbeforeunload = function (e) {
                    localStorage.removeItem("hb");
                    clearInterval(hb);
                }
            }
            return request;
        }
    };
}])
;

essCore.config(['$httpProvider', function ($httpProvider) {
    $httpProvider.interceptors.push('httpTimeoutChecker');
}]);

essCore.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
    $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache, no-store, max-age=0, must-revalidate';
    $httpProvider.defaults.headers.common['Pragma'] = 'no-cache';
    $httpProvider.defaults.headers.common['Expires'] = '-1';
}]);


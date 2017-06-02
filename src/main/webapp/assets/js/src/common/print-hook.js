
angular.module('ess')
    .run(['$rootScope', '$window', registerPrintHook])
    ;

/**
 * Watches for print events
 * dispatches an appropriate angular event when a print event is fired
 * @param $rootScope
 * @param $window
 */
function registerPrintHook ($rootScope, $window) {

    var beforePrint = function() {
        // console.log('beforePrint');
        $rootScope.$broadcast('beforePrint');
        $rootScope.$apply();
    };
    var afterPrint = function() {
        // console.log('afterPrint');
        $rootScope.$broadcast('afterPrint');
        $rootScope.$apply();
    };

    // For webkit
    if ($window.matchMedia) {
        var mediaQueryList = $window.matchMedia('print');
        mediaQueryList.addListener(function(mql) {
            if (mql.matches) {
                beforePrint();
            } else {
                afterPrint();
            }
        });
    }

    // For ie and Firefox
    $window.onbeforeprint = beforePrint;
    $window.onafterprint = afterPrint;
}
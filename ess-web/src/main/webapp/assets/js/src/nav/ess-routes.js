var essApp = angular.module('ess');

/**
 * Sets up URL routing for the angular app. When a route is matched, the template
 * is loaded via the templateUrl, bound to the associated controller, and rendered
 * in an ngView element on the main page.
 *
 * We use angular routes because it allows for url linking in a single page app.
 *
 * {@link http://docs.angularjs.org/api/ngRoute.$route}
 */
essApp.config(function($routeProvider, $locationProvider) {
    var ctxPath = globalProps.ctxPath;

    /** My Info */
    $routeProvider.when(ctxPath + '/myinfo', {
        redirectTo: ctxPath + '/myinfo/personnel/summary'
    });

    $routeProvider.when(ctxPath + '/myinfo/personnel/summary', {
        templateUrl: ctxPath + '/template/myinfo/personnel/summary'
    });

    $routeProvider.when(ctxPath + '/myinfo/personnel/transactions', {
        templateUrl: ctxPath + '/template/myinfo/personnel/transactions'
    });

    $routeProvider.when(ctxPath + '/myinfo/payroll/checkhistory', {
        templateUrl: ctxPath + '/template/myinfo/payroll/checkhistory'
    });

    /** Time and Attendance */
    $routeProvider.when(ctxPath + '/time', {
       redirectTo: ctxPath + '/time/record/entry'
    });

    $routeProvider.when(ctxPath + '/time/record/entry', {
        templateUrl: ctxPath + '/template/time/record/entry',
        reloadOnSearch: false
    });

    $routeProvider.when(ctxPath + '/time/record/history', {
        templateUrl: ctxPath + '/template/time/record/history'
    });

    $routeProvider.when(ctxPath + '/time/record/emphistory', {
        templateUrl: ctxPath + '/template/time/record/emphistory'
    });

    $routeProvider.when(ctxPath + '/time/record/manage', {
        templateUrl: ctxPath + '/template/time/record/manage',
        reloadOnSearch: false
    });

    $routeProvider.when(ctxPath + '/time/record/grant', {
        templateUrl: ctxPath + '/template/time/record/grant'
    });

    $routeProvider.when(ctxPath + '/time/period/calendar', {
        templateUrl: ctxPath + '/template/time/period/calendar'
    });

    $routeProvider.when(ctxPath + '/time/accrual/history', {
        templateUrl: ctxPath + '/template/time/accrual/history'
    });

    $routeProvider.when(ctxPath + '/time/accrual/projections', {
        templateUrl: ctxPath + '/template/time/accrual/projections'
    });

    /** Supply */
    $routeProvider.when(ctxPath + '/supply', {
        redirectTo: ctxPath + '/supply/requisition/order'
    });

    $routeProvider.when(ctxPath + '/supply/requisition/order', {
        templateUrl: ctxPath + '/template/supply/requisition/order'
    });

    $routeProvider.when(ctxPath + '/supply/requisition/manage', {
        templateUrl: ctxPath + '/template/supply/requisition/manage'
    });

    $routeProvider.when(ctxPath + '/supply/cart/cart', {
        templateUrl: ctxPath + '/template/supply/cart/cart'
    });

    /** Help */

    $routeProvider.when(ctxPath + '/help/ta/plan', {
        templateUrl: ctxPath + '/template/help/ta/plan'
    });

    /** 404 */
    $routeProvider.otherwise({
        templateUrl: ctxPath + '/template/404'
    });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
});

/**
 * Create a smooth fade transition for the ng-view.
 */
essApp.animation('.view-animate', function() {
    return {
        enter: function(element, done) {
            element.hide();
            element.delay(150).fadeIn(300, done);
            return function() {
                element.stop();
            }
        },
        leave: function(element, done) {
            element.fadeOut(100, done);
            return function() {
                element.stop();
            }
        }
    }
});
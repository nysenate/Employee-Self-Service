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

    $routeProvider.when(ctxPath + '/myinfo/personnel/emergency-alert-info', {
        templateUrl: ctxPath + '/template/myinfo/personnel/emergency-alert-info'
    });

    $routeProvider.when(ctxPath + '/myinfo/personnel/acknowledgments', {
        templateUrl: ctxPath + '/template/myinfo/personnel/acknowledgment'
    });

    $routeProvider.when(ctxPath + '/myinfo/personnel/acknowledgments/:ackDocId', {
        templateUrl: ctxPath + '/template/myinfo/personnel/ack-doc-view'
    });

    $routeProvider.when(ctxPath + '/myinfo/personnel/ack-doc-report', {
        templateUrl: ctxPath + '/template/myinfo/personnel/ack-doc-report'
    });

    $routeProvider.when(ctxPath + '/myinfo/personnel/emp-ack-doc-report', {
        templateUrl: ctxPath + '/template/myinfo/personnel/emp-ack-doc-report',
        reloadOnSearch: false
    });

    $routeProvider.when(ctxPath + '/myinfo/personnel/transactions', {
        templateUrl: ctxPath + '/template/myinfo/personnel/transactions'
    });

    $routeProvider.when(ctxPath + '/myinfo/payroll/checkhistory', {
        templateUrl: ctxPath + '/template/myinfo/payroll/checkhistory'
    });

    /** Time and Attendance */
    $routeProvider.when(ctxPath + '/time', {
       redirectTo: function () {
           if (globalProps.userIsSenator) {
               if (globalProps.userIsSupervisor) {
                   return ctxPath + '/time/record/manage'
               }
               return ctxPath + '/time/period/calendar'
           }
           return ctxPath + '/time/record/entry';
       }
    });

    $routeProvider.when(ctxPath + '/time/record/entry', {
        templateUrl: ctxPath + '/template/time/record/entry',
        reloadOnSearch: false
    });

    $routeProvider.when(ctxPath + '/time/record/entry/papertimesheet', {
        templateUrl: ctxPath + '/template/time/record/record-paper-entry',
        reloadOnSearch: false
    });

    $routeProvider.when(ctxPath + '/time/record/history', {
        templateUrl: ctxPath + '/template/time/record/history'
    });

    $routeProvider.when(ctxPath + '/time/record/emphistory', {
        templateUrl: ctxPath + '/template/time/record/emp-history'
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
        templateUrl: ctxPath + '/template/time/accrual/history',
        reloadOnSearch: false
    });

    $routeProvider.when(ctxPath + '/time/accrual/projections', {
        templateUrl: ctxPath + '/template/time/accrual/projections'
    });

    $routeProvider.when(ctxPath + '/time/accrual/emphistory', {
        templateUrl: ctxPath + '/template/time/accrual/emp-history'
    });

    $routeProvider.when(ctxPath + '/time/accrual/emp-projections', {
        templateUrl: ctxPath + '/template/time/accrual/emp-projections'
    });

    $routeProvider.when(ctxPath + '/time/allowance/status', {
        templateUrl: ctxPath + '/template/time/allowance/status'
    });

    $routeProvider.when(ctxPath + '/time/allowance/emp-status', {
        templateUrl: ctxPath + '/template/time/allowance/emp-status'
    });

    $routeProvider.when(ctxPath + '/time/personnel/search', {
        templateUrl: ctxPath + '/template/time/personnel/search',
        reloadOnSearch: false
    });

    /** Test */

    $routeProvider.when(ctxPath + '/time/test/errormodal', {
        templateUrl: ctxPath + '/template/time/test/error-modal-test'
    });

    /** Supply */

    $routeProvider.when(ctxPath + '/supply', {
        redirectTo: ctxPath + '/supply/shopping/order'
    });

    $routeProvider.when(ctxPath + '/supply/shopping/order', {
        templateUrl: ctxPath + '/template/supply/shopping/order',
        reloadOnSearch: false
    });

    $routeProvider.when(ctxPath + '/supply/shopping/cart/cart', {
        templateUrl: ctxPath + '/template/supply/shopping/cart/cart'
    });

    $routeProvider.when(ctxPath + '/supply/manage/fulfillment', {
        templateUrl: ctxPath + '/template/supply/manage/fulfillment',
        reloadOnSearch: false
    });

    $routeProvider.when(ctxPath + '/supply/manage/reconciliation', {
        templateUrl: ctxPath + '/template/supply/manage/reconciliation'
    });

    $routeProvider.when(ctxPath + '/supply/history/requisition-history', {
        templateUrl: ctxPath + '/template/supply/history/requisition-history'
    });

    $routeProvider.when(ctxPath + '/supply/history/item-history', {
        templateUrl: ctxPath + '/template/supply/history/item/item-history'
    });

    $routeProvider.when(ctxPath + '/supply/history/item-history-print', {
        templateUrl: ctxPath + '/template/supply/history/item/item-history-print'
    });

    $routeProvider.when(ctxPath + '/supply/history/order-history', {
        templateUrl: ctxPath + '/template/supply/history/order-history'
    });

    $routeProvider.when(ctxPath + '/supply/requisition/requisition-view', {
        templateUrl: ctxPath + '/template/supply/requisition/requisition-view'
    });

    /** Help */

    $routeProvider.when(ctxPath + '/help/ta/plan', {
        templateUrl: ctxPath + '/template/help/ta/plan'
    });

    /** Logout */
    $routeProvider.when(ctxPath + '/logout', {
        // Don't need to do anything, just prevent /logout from hitting 404.
    });


    /** Login */
    $routeProvider.when(ctxPath + '/login', {
        // This request is handled by servlets, don't redirect to "/template/404"
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
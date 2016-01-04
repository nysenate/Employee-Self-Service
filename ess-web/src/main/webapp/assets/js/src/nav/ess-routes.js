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
        redirectTo: ctxPath + '/supply/order/order'
    });

    $routeProvider.when(ctxPath + '/supply/order/order', {
        templateUrl: ctxPath + '/template/supply/order/order',
        resolve: {
            'MyInventoryServiceData': function(SupplyInventoryService) {
                return SupplyInventoryService.promise;
            },
            'MyCategoryServiceData': function(SupplyCategoryService) {
                return SupplyCategoryService.promise;
            }
        }
    });

    $routeProvider.when(ctxPath + '/supply/order/cart/cart', {
        templateUrl: ctxPath + '/template/supply/order/cart/cart'
    });

    $routeProvider.when(ctxPath + '/supply/manage/manage', {
        templateUrl: ctxPath + '/template/supply/manage/manage',
        resolve: {
            'MyInventoryServiceData': function (SupplyInventoryService) {
                return SupplyInventoryService.promise;
            }
        }
    });

    $routeProvider.when(ctxPath + '/supply/manage/reconciliation', {
        templateUrl: ctxPath + '/template/supply/manage/reconciliation',
        resolve: {
            'MyInventoryServiceData': function (SupplyInventoryService) {
                return SupplyInventoryService.promise;
            }
        }
    });

    $routeProvider.when(ctxPath + '/supply/history/history', {
        templateUrl: ctxPath + '/template/supply/history/history'
    });

    $routeProvider.when(ctxPath + '/supply/history/location-history', {
        templateUrl: ctxPath + '/template/supply/history/location-history'
    });

    $routeProvider.when(ctxPath + '/supply/requisition/view', {
        templateUrl: ctxPath + '/template/supply/requisition/view'
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
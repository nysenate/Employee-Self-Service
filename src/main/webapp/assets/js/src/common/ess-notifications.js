var essApp = angular.module('ess');

/** TODO: dialog styles. */
essApp.directive('essNotification', [function() {
    return {
        restrict: 'AE',
        scope: {
            level: '@',
            title: '@',
            message: '@',
            dialog: '@',
            dialogShow: '@'
        },
        transclude: true,
        template: '<div class="ess-notification {{level}}">' +
                    '<h2 ng-if="title" ng-bind="title"></h2>' +
                    '<p ng-if="message" ng-bind="message"></p>'+
                    '<ng-transclude></ng-transclude>' +
                  '</div>',
        link: function(scope, element, attrs) {
            if (scope.dialog === 'true') {
                $elem = jQuery(element);
                if ($elem) {
                    $elem.dialog({
                        autoOpen: false,
                        height: 'auto',
                        width: 'auto',
                        modal: true
                    });
                }
                else {
                    console.log("Failed to load dialog, needs jQuery.")
                }
                scope.$watch('dialogShow', function(newVal, oldVal) {
                    if (newVal === 'true') {
                        $elem.dialog("open");
                    }
                });
            }
            //console.log("Inside the essNotification link function");
        }
    };
}]);
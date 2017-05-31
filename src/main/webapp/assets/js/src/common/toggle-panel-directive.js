var essApp = angular.module('ess');

/**
 * The toggle-panel directive wraps your content in expandable/collapsible container.
 *
 * Ex Usage
 * -----
 * <toggle-panel label="My Title" open="true" extra-classes="my-css">
 *   Insert your content here...
 * </toggle-panel>
 *
 * Attributes
 * ----------
 * label (String) The text for your container header
 * open (boolean) Set to true to expand the content, false to collapse
 * render-closed (boolean) Allows closed content to render when set to true
 * extra-classes (String) Any css classes you want to apply to the outermost toggle panel container
 * show-tip (boolean) Set to true to see a 'Click to expand section' tip when panel is collapsed.
 */
essApp.directive('togglePanel', ['$timeout', '$rootScope', function($timeout, $rootScope){
    return {
        restrict: 'E',
        scope: {
            label: "@",
            extraClasses: "@",
            callback: "&",
            renderClosed: "@"
        },
        replace: true,
        transclude: true,
        template:
        '<div class="content-container {{extraClasses}}" ng-class="{\'open\': open}">' +
        '   <h1 class="toggle-panel-label" ng-click="toggle()">{{label}}</h1>' +   // TODO: Add iconss...
        //'           <span flex></span>' +
        //'           <i ng-class="{\'icon-chevron-up\': open, \'icon-chevron-down\': !open}" style="float: right"></i>' +
        //'           <span class="text-xsmall margin-right-20" ng-show="showTip && !open" style="float: right">' +
        //'               (Click to expand section)</span>' +
        //'       </div>' +
        //'   </div>' +
        '   <div ng-if="opened || renderClosed" ng-show="open" class="panel-content" ng-cloak ng-transclude></div>' +
        '</div>',
        link : function($scope, $element, $attrs) {
            $scope.opened = false;
            $scope.toggle = function() {
                $scope.open = !$scope.open;
                if ($scope.callback) {
                    $scope.callback($scope.open);
                }
                $timeout(function () {
                    $rootScope.$emit('reflowEvent');
                });
            };
            $scope.renderClosed = $scope.renderClosed == 'true';
            // Convert attribute value to boolean using watch
            $scope.$watch($attrs.open, function(open) {
                $scope.open = open;
            });
            $scope.$watch($attrs.showTip, function(showTip) {
                $scope.showTip = showTip;
            });
            $scope.$watch('open', function(newOpen, oldOpen){
                var panelElem = $element.children(".panel-content");
                (newOpen) ? panelElem.slideDown(200) : panelElem.slideUp(200);
                $scope.opened = newOpen || $scope.opened;
                //console.log("opened", $scope.opened);
            });
        }
    }
}]);
var essApp = angular.module('ess');

/**
 * A modal container
 *
 * Insert markup for one or more modals inside this directive and display based on $scope.subview
 */
essApp.directive('modalContainer', ['$rootScope', '$document', 'modals',
function ($rootScope, $document, modals) {

    return {
        template:
            '<div id="modal-container" ng-show="top">' +
            '  <div id="modal-backdrop"></div>' +
            '  <div id="common-modals">' +
            '    <div internal-error-modal ng-if="isOpen(\'500\')"></div>' +
            '  </div>' +
            '  <ng-transclude></ng-transclude>' +
            '</div>',
        transclude: true,
        link: link
    };

    function link($scope, $element) {
        // A stack of modal names in order of opening
        $scope.openModals = [];

        // The name of the modal most recently opened
        $scope.top = null;

        // Returns true if the given view name exists in the open modals stack
        $scope.isOpen = function(viewName) {
            return $scope.openModals.indexOf(viewName) >= 0;
        };

        var backDropEle = $element.find('#modal-backdrop')[0];
        // Reject modal when the user clicks the backdrop
        backDropEle.onclick = function (event) {
            if (backDropEle !== event.target) {
                return;
            }
            $scope.$apply(modals.softReject);
        };

        // Reject modal when the user presses ESC
        $document.bind('keyup', function (event) {
            if (event.keyCode === 27) {
                $scope.$apply(modals.softReject);
            }
        });

        // Set subview upon modal open event
        $rootScope.$on('modals.open', function (event, modalType) {
            console.log('showing modal of type', modalType);
            $scope.openModals.push(modalType);
            updateTop();
        });

        // Remove subview upon modal close event
        $rootScope.$on('modals.close', function(event) {
            $scope.openModals.pop();
            updateTop();
        });

        function updateTop() {
            $scope.top = $scope.openModals[$scope.openModals.length - 1];
            if ($scope.top) {
                lockScrollbar();
            } else {
                unlockScrollbar();
            }
        }

        function lockScrollbar() {
            var rootEle = angular.element('html');
            rootEle.addClass('overflow-hidden');
        }

        function unlockScrollbar() {
            var rootEle = angular.element('html');
            rootEle.removeClass('overflow-hidden');
        }
    }
}]);

essApp.directive('modal', function() {
    return {
        scope: {viewName: '@', isOpen: '&'},
        transclude: true,
        template:
            '<div class="modal" ng-if="isOpen(\'record-details\')" ng-class="{\'background-modal\': top !== viewName}">' +
            '  <ng-transclude></ng-transclude>' +
            '</div>',
        link: function($scope) {
            console.log($scope.isOpen, $scope.$parent.$parent, $scope.viewName);
        }
    };
});
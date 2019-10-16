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
            '<div id="modal-container" ng-show="isOpen()">' +
            '  <div id="modal-backdrop"></div>' +
            '  <div id="common-modals">' +
            '    <modal modal-id="500"><div internal-error-modal></div></modal>' +
            '    <modal modal-id="timeout"><div timeout-modal></div></modal>' +
            '  </div>' +
            '  <ng-transclude id="custom-modals"></ng-transclude>' +
            '  <div id="modal-display"></div>' +
            '</div>',
        transclude: true,
        link: link
    };

    function link($scope, $element, $attrs, $ctrl, $transclude) {

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

        $scope.isOpen = function () {
            return modals.isOpen();
        };
    }
}]);

essApp.directive('modal', ['modals', modalDirective]);
function modalDirective(modals) {
    return {
        scope: {modalId: '@'},
        transclude: true,
        template:
            '<div class="modal" ng-if="isOpen()" ng-class="{\'background-modal\': !isTop()}">' +
            '  <ng-transclude></ng-transclude>' +
            '</div>',
        link: function($scope) {
            $scope.isOpen = function () {
                return modals.isOpen($scope.modalId);
            };

            $scope.isTop = function () {
                return modals.isTop($scope.modalId);
            };
        }
    };
}
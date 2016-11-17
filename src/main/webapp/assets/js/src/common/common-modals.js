angular.module('essTime')
    .directive('progressModal', [progressModal])
    .directive('confirmModal', ['modals', confirmModal])
;

function progressModal () {
    return {
        template:
            '<div class="progress-modal">' +
              '<h3 class="content-info" ng-bind="title"></h3>' +
              '<div loader-indicator class="loader"></div>' +
            '</div>',
        link: link
    };
    function link ($scope, $elem, $attrs) {
        $scope.title = $attrs.title;
    }
}

function confirmModal (modals) {
    return {
        template:
        '<div class="confirm-modal">' +
          '<h3 class="content-info" ng-bind="title"></h3>' +
          '<div class="confirmation-message">' +
            '<h4 ng-show="confirmMessage" ng-bind="confirmMessage"></h4>' +
            '<div ng-hide="rejectable" class="input-container">' +
              '<input type="button" ng-click="resolve()" class="time-neutral-button" value="{{resolveButton || \'OK\'}}"/>' +
            '</div>' +
            '<div ng-show="rejectable" class="input-container">' +
              '<input type="button" ng-click="resolve()" class="submit-button" value="{{resolveButton || \'Yes\'}}"/>' +
              '<input type="button" ng-click="reject()" class="reject-button" value="{{rejectButton || \'No\'}}"/>' +
            '</div>' +
          '</div>' +
        '</div>',
        link: link
    };
    function link ($scope, $elem, $attrs) {
        $scope.resolve = modals.resolve;
        $scope.reject = modals.reject;

        $scope.rejectable = $attrs.rejectable === 'true';

        $scope.title = $attrs.title;

        $scope.confirmMessage = $attrs.confirmMessage;

        $scope.resolveButton = $attrs.resolveButton;
        $scope.rejectButton = $attrs.rejectButton;
    }
}


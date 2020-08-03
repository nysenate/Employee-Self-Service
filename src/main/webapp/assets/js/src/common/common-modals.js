angular.module('ess')
    .directive('progressModal', [progressModal])
    .directive('confirmModal', ['modals', confirmModal])
    .directive('errorModal', ['modals', errorModal])
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
        '<div class="confirm-modal {{level}}">' +
          '<h3 class="content-info" ng-bind="title"></h3>' +
          '<div class="confirmation-message">' +
            '<h4 ng-show="confirmMessage" ng-bind="confirmMessage"></h4>' +
            '<ng-transclude></ng-transclude>' +
            '<div ng-hide="rejectable" class="input-container">' +
              '<input type="button" ng-click="resolve()" class="{{confirmClass}}" ' +
                     'value="{{resolveButton}}" title="{{resolveButton}}" tabindex="1"/>' +
            '</div>' +
            '<div ng-show="rejectable" class="input-container">' +
              '<input type="button" ng-click="resolve()" class="{{resolveClass}}" ' +
                     'value="{{resolveButton}}" title="{{resolveButton}}" tabindex="1"/>' +
              '<input type="button" ng-click="reject()" class="{{rejectClass}}" ' +
                     'value="{{rejectButton}}" title="{{rejectButton}}" tabindex="1"/>' +
            '</div>' +
          '</div>' +
        '</div>',
        transclude: true,
        link: link
    };
    function link ($scope, $elem, $attrs) {
        $scope.resolve = modals.resolve;
        $scope.reject = modals.reject;

        $scope.rejectable = $attrs.rejectable === 'true';

        $scope.title = $attrs.title;

        $scope.level = $attrs.level || 'info';

        $scope.confirmMessage = $attrs.confirmMessage;

        $scope.resolveButton = $attrs.resolveButton || ($scope.rejectable ? 'Yes' : 'Ok');
        $scope.rejectButton = $attrs.rejectButton || 'No';

        $scope.resolveClass = $attrs.resolveClass || 'submit-button';
        $scope.rejectClass = $attrs.rejectClass || 'reject-button';
        $scope.confirmClass = $attrs.confirmClass || 'time-neutral-button';
    }
}

function errorModal (modals) {
    return {
        template:
        '<div class="error-modal">' +
        '<h1 ng-bind="title"></h1>' +
        '<div class=error-description>' +
        '<ng-transclude></ng-transclude>' +
        '</div>' +
        '<div class="button-container">' +
        '<input type="button" ng-click="resolve()" class="{{buttonClass}}" value="{{buttonValue}}" title="{{buttonValue}}"}>' +
        '</div>' +
        '</div>',
        transclude: true,
        link: link
    };
    function link($scope, $elem, $attrs) {
        $scope.resolve = modals.resolve;

        $scope.title = $attrs.title;
        $scope.buttonValue = $attrs.buttonValue || "Ok";
        $scope.buttonClass = $attrs.buttonClass || "reject-button";
    }
}


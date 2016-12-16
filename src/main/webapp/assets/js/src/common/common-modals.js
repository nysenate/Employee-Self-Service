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
              '<input type="button" ng-click="resolve()" class="{{confirmClass}}"' +
                     'value="{{resolveButton || \'OK\'}}" tabindex="1"/>' +
            '</div>' +
            '<div ng-show="rejectable" class="input-container">' +
              '<input type="button" ng-click="resolve()" class="{{resolveClass}}"' +
                     'value="{{resolveButton || \'Yes\'}}" tabindex="1"/>' +
              '<input type="button" ng-click="reject()" class="{{rejectClass}}"' +
                     'value="{{rejectButton || \'No\'}}" tabindex="1"/>' +
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

        $scope.resolveClass = $attrs.resolveClass || 'submit-button';
        $scope.rejectClass = $attrs.rejectClass || 'reject-button';
        $scope.confirmClass = $attrs.confirmClass || 'time-neutral-button';
    }
}


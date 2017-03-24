angular.module('ess').directive('modalCloseButton', ['modals', function (modals) {
    return {
        template: '<div class="icon-cross modal-close-button" ng-click="rejectModal()"></div>',
        link: function ($scope) {
            $scope.rejectModal = function () {
                modals.reject();
            }
        }
    }
}]);
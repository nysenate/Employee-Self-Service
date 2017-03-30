var supply = angular.module('essSupply');
supply.directive('largeItemImageModal', [ largeItemImageModal]);

function largeItemImageModal() {
    return {
        template:
            '<div class="supply-item-image-modal">' +
                '<div class="title-pane">' +
                  '<h3 ng-bind="item.description"></h3>' +
                  '<div class="icon-cross modal-close-button" ng-click="rejectModal()"></div>' +
                '</div>' +
                '<img ng-src="{{imgUrl}}" err-src="{{errorUrl}}">' +
            '</div>',
        controller: 'LargeItemImageModalCtrl'
    }
}

supply.controller('LargeItemImageModalCtrl', ['$scope', 'modals', 'appProps', function ($scope, modals, appProps) {
    $scope.item = modals.params().item;
    $scope.imgUrl = appProps.imageUrl + '/' + $scope.item.commodityCode + '_800.jpg';
    $scope.errorUrl = appProps.imageUrl +'/no_photo_available.png';
    $scope.rejectModal = modals.reject;
}]);

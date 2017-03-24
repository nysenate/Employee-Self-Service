var supply = angular.module('essSupply');
supply.directive('largeItemImageModal', [ largeItemImageModal]);

function largeItemImageModal() {
    return {
        template:
            '<div>' +
                '<modal-close-button></modal-close-button>' +
                '<img style="max-height:800px; display:block;"' +
                'ng-src="{{imgUrl}}"' +
                'err-src="{{errorUrl}}">' +
            '</div>',
        controller: 'LargeItemImageModalCtrl'
    }
}

supply.controller('LargeItemImageModalCtrl', ['$scope', 'modals', 'appProps', function ($scope, modals, appProps) {
    $scope.commodityCode = modals.params().commodityCode;
    $scope.imgUrl = appProps.imageUrl + '/' + $scope.commodityCode + '_800.jpg';
    $scope.errorUrl = appProps.ctxPath +'/assets/img/supply/no_photo_available.png';
}]);

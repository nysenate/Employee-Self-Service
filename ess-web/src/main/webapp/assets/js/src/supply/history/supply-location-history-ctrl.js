essSupply = angular.module('essSupply').controller('SupplyLocationHistoryCtrl', [
    '$scope', 'appProps', 'EmpInfoApi', 'SupplyOrdersApi', supplyLocationHistoryCtrl]);

function supplyLocationHistoryCtrl($scope, appProps, empInfoApi, ordersApi) {

    $scope.empLocation = null;
    $scope.locOrders = null;

    $scope.state = {
        searching: true
    };

    $scope.init = function() {
        var params = {
            empId: appProps.user.employeeId,
            detail: true
        };
        empInfoApi.get(params, function(response) {
            $scope.empLocation = response.employee.empWorkLocation;
            getLocationOrders();
        }, function (response) {

        })
    };

    function getLocationOrders() {
        var params = {
            locCode: $scope.empLocation.code,
            locType: $scope.empLocation.locationTypeCode,
            from: moment().subtract(1, 'month').format('YYYY-MM-DD'),
            to: moment().format('YYYY-MM-DD')
        };
        ordersApi.get(params, function(response) {
            $scope.locOrders = response.result;
            sortOrdersByDescendingDate($scope.locOrders);
            $scope.state.searching = false;
        }, function (response) {

        })
    }

    $scope.init();

    function sortOrdersByDescendingDate(orders) {
        shipments.sort(function(a, b) {
            var aDate = moment(a.orderDateTime);
            var bDate = moment(b.orderDateTime);
            return bDate.format('X') - aDate.format('X');
        });
        return shipments;
    }
}
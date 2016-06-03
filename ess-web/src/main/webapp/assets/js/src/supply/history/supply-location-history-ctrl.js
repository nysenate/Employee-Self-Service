var essSupply = angular.module('essSupply').controller('SupplyLocationHistoryCtrl', [
    '$scope', 'appProps', 'LocationService', 'EmpInfoApi', 'SupplyRequisitionApi', supplyLocationHistoryCtrl]);

function supplyLocationHistoryCtrl($scope, appProps, locationService, empInfoApi, requisitionApi) {

    $scope.employeeLocation = {};
    $scope.locationRequisitions = [];
    $scope.employeeRequisitions = [];
    $scope.allRequisitions = [];
    $scope.loading = true;

    $scope.init = function () {
        getRequisitionsOrderedForEmployeesLocation()
            .then(getRequisitionsOrderedByEmployee)
            .then(sumRequisitions)
            .then(sortOrdersByDescendingDate)
            .finally(doneLoading);
    };

    function getRequisitionsOrderedForEmployeesLocation() {
        return getLoggedInEmployeeInfo()
            .then(setEmployeeLocation)
            .then(setLocationRequisitions);
    }

    function getLoggedInEmployeeInfo() {
        var params = {
            empId: appProps.user.employeeId,
            detail: true
        };
        return empInfoApi.get(params).$promise;
    }

    function setEmployeeLocation(employeeInfoResponse) {
        $scope.employeeLocation = employeeInfoResponse.employee.empWorkLocation;
        return $scope.employeeLocation;
    }

    function setLocationRequisitions(location) {
        var params = {
            location: location.locId,
            from: moment().subtract(1, 'month').format(),
            to: moment().format()
        };
        return requisitionApi.get(params, function (response) {
            $scope.locationRequisitions = response.result;
        }).$promise;
    }

    function getRequisitionsOrderedByEmployee() {
        getEmployeesOrderedRequisitions()
            .then(setEmployeeRequisitions)
    }

    function getEmployeesOrderedRequisitions() {
        var params = {
            customer: appProps.user.employeeId,
            from: moment().subtract(1, 'month').format(),
            to: moment().format()
        };
        return requisitionApi.get(params).$promise;
    }

    function setEmployeeRequisitions(response) {
        $scope.employeeRequisitions = response.result;
        return $scope.employeeRequisitions;
    }

    function sumRequisitions() {
        $scope.allRequisitions = angular.copy($scope.locationRequisitions);
        for (var i = 0; i < $scope.employeeRequisitions.length; i++) {
            if (!arrayContainsRequisition($scope.allRequisitions, $scope.employeeRequisitions[i])) {
                $scope.allRequisitions.push($scope.employeeRequisitions[i]);
            }
        }
        return $scope.allRequisitions;
    }

    function arrayContainsRequisition(array, requisition) {
        for (var i = 0; i < array.length; i++) {
            if (requisition.id === array[i].id) {
                return true;
            }
        }
        return false;
    }

    function doneLoading() {
        $scope.loading = false;
    }

    $scope.init();

    function sortOrdersByDescendingDate(orders) {
        orders.sort(function (a, b) {
            var aDate = moment(a.orderedDateTime);
            var bDate = moment(b.orderedDateTime);
            return bDate.format('X') - aDate.format('X');
        });
        return orders;
    }

    $scope.viewRequisition = function (requisition) {
        locationService.go("/supply/requisition/requisition-view", false, "requisition=" + requisition.id);
    }
}
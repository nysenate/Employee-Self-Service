var essSupply = angular.module('essSupply').controller('SupplyOrderHistoryCtrl', [
    '$scope', 'appProps', 'LocationService', 'EmpInfoApi', 'SupplyRequisitionOrderHistoryApi', 'PaginationModel', supplyOrderHistoryCtrl]);

/**
 * The Order History is a collection of all requisitions submitted by the logged in user
 * plus any other requisitions with a destination equal to the logged in users work location.
 * 
 * Therefore, in the order history page a user should be able to see all requisitions for their work location
 * plus all of their requisitions that went to a different location.
 */
function supplyOrderHistoryCtrl($scope, appProps, locationService, empInfoApi, orderHistoryApi, paginationModel) {

    /** Valid Requisitions statuses */
    $scope.STATUSES = ['PENDING', 'PROCESSING', 'COMPLETED', 'APPROVED', 'REJECTED'];
    
    /** All requisitions in the logged in users order history. */
    $scope.requisitions = [];
    $scope.paginate = angular.extend({}, paginationModel);
    $scope.filter = {
        date: {
            from: moment().subtract(1, 'month').format("MM/DD/YYYY"),
            to: moment().format("MM/DD/YYYY")
        },
        status: []
    };
    $scope.loading = true;

    $scope.init = function () {
        $scope.paginate.itemsPerPage = 12;
        $scope.filter.status = angular.copy($scope.STATUSES);
        queryOrderHistory()
    };

    $scope.init();

    /** Updates the displayed requisitions whenever filters or page is changed. */ 
    $scope.updateRequisitions = function () {
        $scope.loading = true;
        queryOrderHistory();
    };

    /**
     * Queries all requisitions made by the logged in user plus any other requisitions with a destination
     * equal to the logged in users work location.
     */
    function queryOrderHistory() {
        return getLoggedInEmployeeInfo()
            .then(getRequisitions)
            .then(setRequisitions)
            .finally(doneLoading);
    }

    function getLoggedInEmployeeInfo() {
        var params = {
            empId: appProps.user.employeeId,
            detail: true
        };
        return empInfoApi.get(params).$promise;
    }

    function getRequisitions(employeeInfoResponse) {
        var params = {
            location: employeeInfoResponse.employee.empWorkLocation,
            customerId: employeeInfoResponse.employee.employeeId,
            status: $scope.filter.status,
            from: moment().subtract(1, 'month').format(),
            to: moment().format(),
            limit: $scope.paginate.itemsPerPage,
            offset: $scope.paginate.getOffset()
        };
        return orderHistoryApi.get(params, function (response) {
            $scope.paginate.setTotalItems(response.total);
        }).$promise;
    }

    function setRequisitions(orderHistoryResponse) {
        $scope.requisitions = orderHistoryResponse.result;
        return $scope.requisitions;
    }

    function doneLoading() {
        $scope.loading = false;
    }

    $scope.viewRequisition = function (requisition) {
        locationService.go("/supply/requisition/requisition-view", false, "requisition=" + requisition.id);
    }
}
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

    var DATE_FORMAT = "MM/DD/YYYY";
    /** Valid Requisitions statuses */
    $scope.STATUSES = ['ALL', 'PENDING', 'PROCESSING', 'COMPLETED', 'APPROVED', 'REJECTED'];
    
    /** All requisitions in the logged in users order history. */
    $scope.requisitions = [];
    $scope.paginate = angular.extend({}, paginationModel);
    $scope.filter = {
        date: {
            from: moment().subtract(1, 'month').format(DATE_FORMAT),
            to: moment().format(DATE_FORMAT)
        },
        status: []
    };
    $scope.loading = true;

    $scope.init = function () {
        $scope.paginate.itemsPerPage = 12;
        $scope.filter.status = angular.copy($scope.STATUSES.slice(1));
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
            .finally(doneLoading).catch(
                function (errorResponse) {
                    modals.open('500', {details: errorResponse});
                    console.error(errorResponse);
                }
            );
    }

    function getLoggedInEmployeeInfo() {
        var params = {
            empId: appProps.user.employeeId,
            detail: true
        };
        return empInfoApi.get(params).$promise.catch(function (errorResponse) {
            modals.open('500', {details: errorResponse});
            console.error(errorResponse);
        });
    }

    function getRequisitions(employeeInfoResponse) {
        if ($scope.filter.status.indexOf('ALL') != -1) {
            $scope.filter.status = angular.copy($scope.STATUSES.slice(1));
        }
        var params = {
            location: employeeInfoResponse.employee.empWorkLocation.locId,
            customerId: employeeInfoResponse.employee.employeeId,
            status: $scope.filter.status,
            from: moment($scope.filter.date.from, DATE_FORMAT).startOf('day').format(),
            to: moment($scope.filter.date.to, DATE_FORMAT).endOf('day').format(),
            limit: $scope.paginate.itemsPerPage,
            offset: $scope.paginate.getOffset()
        };
        return orderHistoryApi.get(params, function (response) {
            $scope.paginate.setTotalItems(response.total);
        },function (errorResponse) {
            modals.open('500', {details: errorResponse});
            console.error(errorResponse);
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
        locationService.go("/supply/requisition/requisition-view", false, "requisition=" + requisition.requisitionId + "&fromPage=orderhistory");
    }
}
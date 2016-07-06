var essApi = angular.module('essApi');

/** --- Pay Period API --- */

essApi.factory('PayPeriodApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/periods/:periodType');
}]);

/** --- Holiday API --- */

essApi.factory('HolidayApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/holidays');
}]);

/** --- Time Record API --- */

essApi.factory('TimeRecordApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/timerecords');  
}]);

essApi.factory('ActiveTimeRecordsApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/timerecords/active');
}]);

essApi.factory('ActiveYearsTimeRecordsApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/timerecords/activeYears');
}]);

essApi.factory('SupervisorTimeRecordsApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/timerecords/supervisor');
}]);

essApi.factory('SupervisorTimeRecordCountsApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/timerecords/supervisor/count');
}]);

/** --- Attendance Record API --- */

essApi.factory('AttendanceRecordApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/attendance/records');
}]);

/** --- Supervisor API --- */

essApi.factory('SupervisorEmployeesApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/supervisor/employees');
}]);

essApi.factory('SupervisorChainApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/supervisor/chain');
}]);

essApi.factory('SupervisorOverridesApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/supervisor/overrides');
}]);

essApi.factory('SupervisorGrantsApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/supervisor/grants');
}]);


/** --- Accrual API --- */

essApi.factory('AccrualPeriodApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/accruals');
}]);

essApi.factory('AccrualHistoryApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/accruals/history');
}]);

/** --- Employee API --- */

essApi.factory('EmpInfoApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/employees.json');
}]);

essApi.factory('EmpActiveYearsApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/employees/activeYears');
}]);

/** --- Transaction API --- */

essApi.factory('EmpTransactionsApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/empTransactions/')
}]);

essApi.factory('EmpTransactionSnapshotApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/empTransactions/snapshot')
}]);

essApi.factory('EmpTransactionCurrentSnapshotApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/empTransactions/snapshot/current')
}]);

essApi.factory('EmpTransactionTimelineApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/empTransactions/timeline')
}]);

/** --- Allowance API --- */

essApi.factory('AllowanceApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/allowances');
}]);

/** --- Misc Leave Type Grant API --- */

essApi.factory('MiscLeaveGrantApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/miscleave/grants')
}]);

/** --- Paycheck History API --- */

essApi.factory('EmpCheckHistoryApi',  ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/paychecks.json')
}]);

/** --- Location API --- */

essApi.factory('LocationApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/locations.json')
}]);

/** --- Supply Items API --- */

essApi.factory('SupplyItemsApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/items.json')
}]);

/** --- Supply Employee API --- */

essApi.factory('SupplyEmployeesApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/employees.json')
}]);

/** --- Supply Location Allowance --- */

essApi.factory('SupplyLocationAllowanceApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/location/allowance/:id.json', {id: '@id'})
}]);

/** --- Supply Destination Api --- */

essApi.factory('SupplyDestinationApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/destinations/:empId.json', {empId: '@empId'})
}]);

/** --- Supply Requisition Api --- */

essApi.factory('SupplyRequisitionApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/requisitions.json')
}]);

essApi.factory('SupplyRequisitionByIdApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/requisitions/:id.json', {id: '@id'})
}]);

essApi.factory('SupplyRequisitionUndoRejectionApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/requisitions/:id/undoReject.json', {id: '@id'})
}]);

essApi.factory('SupplyRequisitionOrderHistoryApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/requisitions/orderHistory.json')
}]);

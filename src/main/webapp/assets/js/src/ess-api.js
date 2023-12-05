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

essApi.factory('TimeRecordReviewApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/timerecords/review');
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

essApi.factory('TimeRecordReminderApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/timerecords/reminder');
}]);

essApi.factory('TimeRecordCreationApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/timerecords/new');
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

essApi.factory('AccrualActiveYearsApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/accruals/active-years');
}]);

essApi.factory('AccrualHistoryApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/accruals/history');
}]);

/** --- Donation API --- */

essApi.factory('DonationInfoApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/donation/info');
}]);

essApi.factory('DonationHistoryApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/donation/history');
}]);

essApi.factory('SubmitDonationApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/donation/submit');
}]);

/** --- Expected Hours API --- */

essApi.factory('ExpectedHoursApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/expectedhrs');
}]);

/** --- Employee API --- */

essApi.factory('EmpInfoApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/employees');
}]);

essApi.factory('EmpActiveYearsApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/employees/activeDates');
}]);

essApi.factory('EmpActiveYearsApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/employees/activeYears');
}]);

essApi.factory('ActiveEmployeeApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/employees/active');
}]);

essApi.factory('EmployeeSearchApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/employees/search');
}]);

/** --- Responsibility Center API --- */

essApi.factory('RCHSearchApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/respctr/head/search');
}]);

/** --- Alert Info API --- */

essApi.factory('AlertInfoApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/alert-info');
}]);

/** --- Personnel Task API --- */

essApi.factory('PersonnelTaskApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/personnel/task');
}]);

essApi.factory('EmpPATSearchApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/personnel/task/emp/search');
}]);

essApi.factory('PersonnelAssignmentsForEmpApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/personnel/task/assignment/:empId', {
        empId: '@empId'
    });
}]);

essApi.factory('PersonnelAssignmentApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/personnel/task/assignment/:empId/:taskId', {
        empId: '@empId',
        taskId: '@taskId'
    });
}]);

essApi.factory('UpdatePersonnelTaskAssignmentCompletionApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/admin/personnel/task/overrride/:updateEmpID/:taskId/true/:empId', {
        updateEmpID: '@updateEmpID',
        taskId: '@taskId',
        empId: '@empId'
    });
}]);

essApi.factory('UpdatePersonnelTaskAssignmentActiveStatusApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/admin/personnel/task/overrride/activation/:updateEmpID/:taskId/false/:empId', {
        updateEmpID: '@updateEmpID',
        taskId: '@taskId',
        empId: '@empId'
    });
}]);

/** --- Acknowledgment API --- */

essApi.factory('AcknowledgmentApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/personnel/task/acknowledgment');
}]);

/** --- PEC Video Code API --- */

essApi.factory('PECVidCodeApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/personnel/task/video/code');
}]);

/** --- Ethics Code API --- */

essApi.factory('PECEthicsCodeApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/personnel/task/ethics/live/code');
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


essApi.factory('AllowanceActiveYearsApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/allowances/active-years');
}]);

essApi.factory('AllowanceApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/allowances');
}]);

essApi.factory('PeriodAllowanceUsageApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/allowances/period');
}]);

/** --- Misc Leave Type Grant API --- */

essApi.factory('MiscLeaveGrantApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/miscleave/grantsWithRemainingHours')
}]);

/** --- Paycheck History API --- */

essApi.factory('EmpCheckHistoryApi',  ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/paychecks')
}]);

/** --- Location API --- */

essApi.factory('LocationApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/locations')
}]);

/** --- Supply Destination Api --- */

essApi.factory('SupplyDestinationApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/destinations/:empId', {empId: '@empId'})
}]);

/** --- Supply Requisition Api --- */

essApi.factory('SupplyRequisitionApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/requisitions')
}]);

essApi.factory('SupplyRequisitionByIdApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/requisitions/:id', {id: '@id'})
}]);

essApi.factory('SupplyRequisitionProcessApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/requisitions/:id/process', {id: '@id'})
}]);

essApi.factory('SupplyRequisitionRejectApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/requisitions/:id/reject', {id: '@id'})
}]);

essApi.factory('SupplyRequisitionHistoryApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/requisitions/history/:id', {id: '@id'})
}]);

essApi.factory('SupplyRequisitionOrderHistoryApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/requisitions/orderHistory')
}]);


/** --- Supply Employees API --- */

essApi.factory('SupplyEmployeesApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/employees')
}]);

essApi.factory('SupplyIssuersApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/employees/issuers')
}]);

/** --- Supply Reconciliation API --- */

essApi.factory('SupplyReconciliationApi', ['$resource', 'appProps', function($resource, appProps) {
    return $resource(appProps.apiPath + "/supply/reconciliation")
}]);

/** --- Supply Statistics API --- */

essApi.factory('SupplyLocationStatisticsApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/supply/statistics/locations')
}]);

/** --- Travel API --- */

essApi.factory('TravelApplicationApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/application')
}]);

essApi.factory('TravelDraftsApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/drafts', {}, {'create': {method: 'PUT'}, 'update': {method: 'PATCH'}})
}]);

essApi.factory('TravelDraftsSubmitApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/drafts/submit')
}]);

essApi.factory('TravelDraftByIdApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/drafts/:id', {id: '@id'}, {'delete': {method: 'DELETE'}})
}]);

essApi.factory('TravelEventTypesApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/event-types')
}]);

essApi.factory('AllowedTravelersApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/allowed-travelers')
}]);

essApi.factory('TravelAppEditApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/application/edit/:id', {id: '@id'}, {'update': {method: 'PATCH'}})
}])

essApi.factory('TravelAppCancelApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/application/edit/:id/cancel', {id: '@id'})
}])

essApi.factory('TravelAppEditResubmitApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/application/edit/resubmit/:id', {id: '@id'}, {'update': {method: 'PATCH'}})
}])

essApi.factory('TravelApplicationByIdApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/application/:id', {id: '@id'})
}]);

essApi.factory('TravelApplicationsForTravelerApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/applications')
}]);

essApi.factory('TravelUserConfigApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/user/config')
}]);

essApi.factory('TravelUserConfigSaveApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/user/config/save')
}]);

essApi.factory('TravelUserConfigDeleteApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/user/config/delete')
}]);

essApi.factory('TravelModeOfTransportationApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/mode-of-transportation')
}]);

essApi.factory('TravelGeocodeApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/geocode')
}]);

essApi.factory('TravelDistrictAssignApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/travel/address/district')
}]);

/** --- Timeout API --- */

essApi.factory('TimeoutApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/timeout/ping')
}]);

/** --- Error Report API --- */

essApi.factory('ErrorReportApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.apiPath + '/report/error')
}]);

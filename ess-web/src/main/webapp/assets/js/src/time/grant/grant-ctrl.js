var essTime = angular.module('essTime');

essTime.controller('GrantPrivilegesCtrl', ['$scope', '$http', 'appProps', 'SupervisorChainApi',
    'SupervisorGrantsApi', 'SupervisorOverridesApi', 'modals',
   function($scope, $http, appProps, SupervisorChainApi, SupervisorGrantsApi, SupervisorOverridesApi, modals) {

       $scope.state = {
           empId: appProps.user.employeeId,
           selectedGrantee: null,
           grantees: null,   // Stores an ordered list of the supervisors.
           granteeMap: null, // Map of supId -> sup, allows easy modification of supervisor grant status.

           granters: [],    // List of overrides this supervisor has been granted

           modified: false,   // If the state has been altered.
           fetched: false,   // If the data has been fetched.
           saving: false,
           saved: false
       };

       $scope.init = function() {
           // Initialize state
           $scope.state.selectedGrantee = null;
           $scope.state.grantees = [];
           $scope.state.granteeMap = {};
           $scope.state.granters = [];
           $scope.state.modified = $scope.state.fetched = $scope.state.saving = $scope.state.saved = false;

           // Fetch supervisor chain
           SupervisorChainApi.get({empId: $scope.state.empId}, function (resp) {
               if (resp.success == true) {
                   angular.forEach(resp.result.supChain, function (sup) {
                       sup.granted = false;
                       sup.grantStart = sup.grantEnd = null;
                       $scope.state.grantees.push(sup);
                       $scope.state.granteeMap[sup.employeeId] = sup;
                   });
               }
           }).$promise.then(function (resp) {
               // Link up with any existing grants
               return SupervisorGrantsApi.get({supId: $scope.state.empId}, function (resp) {
                   if (resp.success) {
                       angular.forEach(resp.grants, function (grant) {
                           var supId = grant.granteeSupervisorId;
                           if (!$scope.state.granteeMap[supId]) {
                               $scope.state.grantees.push(grant.granteeSupervisor);
                               $scope.state.granteeMap[supId] = grant.granteeSupervisor;
                           }
                           $scope.state.granteeMap[supId].granted = true;
                           $scope.state.granteeMap[supId].grantStart =
                               (grant.startDate != null) ? moment(grant.startDate).format('MM/DD/YYYY') : null;
                           $scope.state.granteeMap[supId].grantEnd =
                               (grant.endDate != null) ? moment(grant.endDate).format('MM/DD/YYYY') : null;
                       });
                   }
               }).$promise;
           }).then(function (resp) {
               return SupervisorOverridesApi.get({supId: $scope.state.empId}, function (resp) {
                   if (resp.success) {
                       $scope.state.granters = resp.overrides.map(function (ovr) {
                           var granter = ovr.overrideSupervisor;
                           granter.grantStartStr = (ovr.startDate) ? moment(ovr.startDate).format('MM/DD/YYYY') : 'No Start Date';
                           granter.grantEndStr = (ovr.endDate) ? moment(ovr.endDate).format('MM/DD/YYYY') : 'No End Date';
                           granter.activeStr = (ovr.active) ? 'Active' : 'Inactive';
                           return granter;
                       });
                   }
                   $scope.state.fetched = true;
               }).$promise;
           }).catch(function (resp) {
               modals.open('500', {details: resp});
               console.log(resp);
           });
       };

       // Updater

       $scope.saveGrants = function() {
           if ($scope.state.modified === true && $scope.state.fetched === true) {
               var modifiedGrantees = $scope.state.grantees.filter(function(grantee) {
                   return grantee.modified === true;
               }).map(function(grantee) {
                   return $scope.createGrantSaveView(grantee);
               });
               $scope.state.saving = true;
               SupervisorGrantsApi.save(modifiedGrantees, function(resp) {
                   $scope.state.saving = false;
                   $scope.state.modified = false;
                   $scope.state.saved = true;
               }, function(resp) {
                   modals.open('500', {details: resp});
                   console.log(resp);
               });
           }
       };

       $scope.createGrantSaveView = function(grantee) {
           return {
               granteeSupervisorId: grantee.employeeId,
               active: grantee.granted,
               granterSupervisorId: $scope.state.empId,
               startDate: (grantee.grantStart) ? moment(grantee.grantStart).format('YYYY-MM-DD') : null,
               endDate: (grantee.grantEnd) ? moment(grantee.grantEnd).format('YYYY-MM-DD') : null
           };
       };

       // Modifiers

       $scope.setStartDate = function(grantee) {
           $scope.state.modified = true;
           grantee.modified = true;
           if (grantee.grantStart) {
               grantee.grantStart = null;
           }
           else {
               grantee.grantStart = moment().format('MM/DD/YYYY');
           }
       };

       $scope.setEndDate = function(grantee) {
           $scope.state.modified = true;
           grantee.modified = true;
           if (grantee.grantEnd) {
               grantee.grantEnd = null;
           }
           else {
               grantee.grantEnd = moment().format('MM/DD/YYYY');
           }
       };

       $scope.toggleGrantStatus = function(grantee) {
           $scope.state.modified = true;
           grantee.modified = true;
       };

       $scope.reset = function() {
           $scope.init();
       };

       $scope.init();
    }]);

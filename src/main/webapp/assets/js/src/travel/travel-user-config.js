var essTravel = angular.module('essTravel');
essTravel.controller('UserConfigCtrl', ['$scope', 'appProps', 'ActiveEmployeeApi', userConfigCtrl]);

function userConfigCtrl($scope, appProps, ActiveEmployeeApi) {

    $scope.dataLoaded = false;
    $scope.empId = appProps.user.employeeId;

    console.log(ActiveEmployeeApi.get({activeOnly: true}));

    $scope.init = function () {

        $scope.granteeInfo = {
            selectedGrantee: null,
            granted: false,
            startDate: null,
            endDate: null,
            permanent: false
        };
        $scope.grantees = [];  // Stores an ordered list of the supervisors.

        ActiveEmployeeApi.get({activeOnly: true}, function (resp) {
            if (resp.success) {
                angular.forEach(resp.employees, function (employee) {
                    var fullName = employee.fullName;
                    $scope.grantees.push(fullName);
                });
            }
        }).$promise.then(function (resp) {
            // Link up with any existing grants
            return ActiveEmployeeApi.get({activeOnly: true}, function (resp) {
            }).$promise;
        }).then(function (resp) {
            $scope.dataLoaded = true;
            return ActiveEmployeeApi.get({activeOnly: true}, function (resp) {
            }).$promise;
        }).catch(function (resp) {
            modals.open('500', {details: resp});
            console.log(resp);
        });
    };

    $scope.saveGrants = function(){
       var error = true;

        if($scope.granteeInfo.selectedGrantee){
           if(($scope.granteeInfo.permanent) || ($scope.granteeInfo.startDate && $scope.granteeInfo.endDate)) {
               error = false;
           }
       }

       if(error === true) {
           console.log('error');
           console.log($scope.granteeInfo.selectedGrantee);
           console.log($scope.granteeInfo.startDate);
           console.log($scope.granteeInfo.endDate);
       }
    };

    $scope.setPermanent = function(){
        if($scope.granteeInfo.permanent){
            $scope.granteeInfo.startDate = moment().format('MM/DD/YYYY');

            // TODO: make this permanent
            $scope.granteeInfo.endDate = moment().add({years: 1}).format('MM/DD/YYYY');
        }
        else {
            $scope.granteeInfo.startDate = null;
            $scope.granteeInfo.endDate = null;
        }
    };

    $scope.setStartDate = function(){
        $scope.granteeInfo.startDate = moment().format('MM/DD/YYYY');
    };

    $scope.setEndDate = function(){
        $scope.granteeInfo.endDate = moment().format('MM/DD/YYYY');
    };

    $scope.reset = function() {
        $scope.init();
    };

    $scope.init();
}
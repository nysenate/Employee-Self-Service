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

    $scope.formNotFilledOut = function() {
        return !($scope.granteeInfo.selectedGrantee &&
            ($scope.granteeInfo.permanent || ($scope.granteeInfo.startDate && $scope.granteeInfo.endDate)));
    }

    $scope.saveGrants = function(){
        //hook up to database
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
        $scope.granteeInfo = {
            selectedGrantee: null,
            granted: false,
            startDate: null,
            endDate: null,
            permanent: false
        };
    };

    $scope.init();
}
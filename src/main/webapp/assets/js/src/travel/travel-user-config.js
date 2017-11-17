var essTravel = angular.module('essTravel');
essTravel.controller('UserConfigCtrl', ['$scope', 'appProps', 'ActiveEmployeeApi', 'TravelUserConfigApi', userConfigCtrl]);

function userConfigCtrl($scope, appProps, ActiveEmployeeApi, TravelUserConfigApi) {

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
                $scope.grantees = resp.employees;
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
        TravelUserConfigApi.get({empId: $scope.empId, detail: true}, function (resp) {
            if (resp.success) {
                console.log(resp.employee);
            }
        }).$promise.then(function (resp) {
            // Link up with any existing grants
            return TravelUserConfigApi.get({empId: $scope.empId, detail: true}, function (resp) {
            }).$promise;
        }).then(function (resp) {
            $scope.dataLoaded = true;
            return TravelUserConfigApi.get({empId: $scope.empId, detail: true}, function (resp) {
            }).$promise;
        }).catch(function (resp) {
            modals.open('500', {details: resp});
            console.log(resp);
        });
    };

    $scope.formNotFilledOut = function() {
        return !($scope.granteeInfo.selectedGrantee &&
            ($scope.granteeInfo.permanent || ($scope.granteeInfo.startDate && $scope.granteeInfo.endDate)));
    };

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
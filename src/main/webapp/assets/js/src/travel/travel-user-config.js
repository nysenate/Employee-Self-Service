var essTravel = angular.module('essTravel');
essTravel.controller('UserConfigCtrl', ['$scope', 'appProps', 'ActiveEmployeeApi', userConfigCtrl]);

function userConfigCtrl($scope, appProps, ActiveEmployeeApi) {

    $scope.empId = appProps.user.employeeId;

    console.log(ActiveEmployeeApi.get({activeOnly: true}));

    $scope.init = function () {

        $scope.granteeInfo = {
            selectedGrantee: null,
            startDate: null,
            granted: false,
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
            return ActiveEmployeeApi.get({activeOnly: true}, function (resp) {
            }).$promise;
        }).catch(function (resp) {
            modals.open('500', {details: resp});
            console.log(resp);
        });
    };

    $scope.setStartDate = function(){
        $scope.granteeInfo.startDate = $scope.granteeInfo.startDate ? null : moment().format('MM/DD/YYYY');
    };

    $scope.setEndDate = function(){
        $scope.granteeInfo.endDate = $scope.granteeInfo.endDate ? null : moment().format('MM/DD/YYYY');
    };

    $scope.init();
    console.log($scope.grantees);
}
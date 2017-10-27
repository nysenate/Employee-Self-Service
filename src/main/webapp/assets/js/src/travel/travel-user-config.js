var essTravel = angular.module('essTravel');
essTravel.controller('UserConfigCtrl', ['$scope', 'appProps', 'ActiveEmployeeApi', userConfigCtrl]);

function userConfigCtrl($scope, appProps, ActiveEmployeeApi){

    $scope.state = {
        empId: appProps.user.employeeId,
        selectedGrantee: null,
        grantees: null   // Stores an ordered list of the supervisors.
    };

    console.log(ActiveEmployeeApi.get({activeOnly: true}));

    $scope.init = function(){
        $scope.state.selectedGrantee = null;
        $scope.state.grantees = [];

        ActiveEmployeeApi.get({activeOnly: true}, function (resp) {
            console.log("blah");
        }).$promise.then(function (resp) {
            // Link up with any existing grants
            return ActiveEmployeeApi.get({activeOnly: true}, function (resp) {
                console.log("blah");
            }).$promise;
        }).then(function (resp) {
            return ActiveEmployeeApi.get({activeOnly: true}, function (resp) {
                console.log("blah");
            }).$promise;
        }).catch(function (resp) {
            modals.open('500', {details: resp});
            console.log(resp);
        });
    };
}
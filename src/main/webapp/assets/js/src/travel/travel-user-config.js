var essTravel = angular.module('essTravel');
essTravel.controller('UserConfigCtrl', ['$scope', 'appProps', 'ActiveEmployeeApi', 'TravelUserConfigApi',
                                        'TravelUserConfigSaveApi', 'TravelUserConfigDeleteApi', 'EmpInfoApi', 'modals', userConfigCtrl]);

function userConfigCtrl($scope, appProps, ActiveEmployeeApi, TravelUserConfigApi, TravelUserConfigSaveApi,
                        TravelUserConfigDeleteApi, EmpInfoApi, modals) {

    $scope.dataLoaded = false;
    $scope.empId = appProps.user.employeeId;

    $scope.init = function () {

        $scope.currentGrantee = null;

        $scope.granteeInfo = {
            selectedGrantee: null,
            startDate: null,
            endDate: null,
            permanent: false
        };

        $scope.grantees = [];

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

        TravelUserConfigApi.get({empId: $scope.empId}, function (resp) {
            if (resp.result.requestorId != 0) {
                $scope.currentGrantee = resp.result;
                console.log(resp.result);

                // TODO change $scope.currentGrantee.empId -> $scope.currentGrantee.requestorId
                EmpInfoApi.get({empId: $scope.currentGrantee.empId}, function(resp){
                    if(resp.success) {
                        console.log(resp.employee);
                        console.log($scope.grantees);
                        for(var i = 0, length = $scope.grantees.length; i < length; i++){
                            var grantee = $scope.grantees;
                            if(grantee.fullName == resp.employee.fullName){
                                console.log(grantee);
                                break;
                            }
                        }
                    }
                });
                $scope.granteeInfo.startDate = moment(resp.result.startDate).format('MM/DD/YYYY');
                if (resp.result.endDate == null) {
                    $scope.granteeInfo.permanent = true;
                }
                else {
                    $scope.granteeInfo.endDate = moment(resp.result.endDate).format('MM/DD/YYYY');
                }
            }
        }).$promise.then(function (resp) {
            // Link up with any existing grants
            return TravelUserConfigApi.get({empId: $scope.empId}, function (resp) {
            }).$promise;
        }).then(function (resp) {
            return TravelUserConfigApi.get({empId: $scope.empId}, function (resp) {
            }).$promise;
        }).catch(function (resp) {
            modals.open('500', {details: resp});
            console.log(resp);
        });
    };

    $scope.formNotFilledOut = function () {
        return !($scope.granteeInfo.selectedGrantee && $scope.granteeInfo.startDate
        && ($scope.granteeInfo.permanent || $scope.granteeInfo.endDate));
    };

    $scope.deleteRequester = function () {
        TravelUserConfigDeleteApi.save({empId: $scope.empId}, {}, function (resp) {
            console.log(resp);
        });
    };

    $scope.saveGrants = function () {
        var params = {
            empId: $scope.empId,
            requestorId: $scope.granteeInfo.selectedGrantee.empId,
            startDate: $scope.granteeInfo.startDate,
            endDate: $scope.granteeInfo.endDate
        };

        TravelUserConfigSaveApi.save(params, {}, function (resp) {
            console.log(resp);
        }, function (resp) {
            modals.open('500', {details: resp});
            console.log(resp);
        });
    };

    $scope.setPermanent = function () {
        if ($scope.granteeInfo.permanent) {
            $scope.granteeInfo.startDate = moment().format('MM/DD/YYYY');
            $scope.granteeInfo.endDate = null;
        }
        else {
            $scope.granteeInfo.startDate = null;
            $scope.granteeInfo.endDate = null;
        }
    };

    $scope.setStartDate = function () {
        if (document.getElementById('grant-start-date').checked) {
            $scope.granteeInfo.startDate = moment().format('MM/DD/YYYY');
        }
        else {
            $scope.granteeInfo.startDate = null;
        }
    };

    $scope.setEndDate = function () {
        if (document.getElementById('grant-end-date').checked) {
            $scope.granteeInfo.endDate = moment().format('MM/DD/YYYY');
        }
        else {
            $scope.granteeInfo.endDate = null;
        }
    };

    $scope.reset = function () {
        $scope.granteeInfo = {
            selectedGrantee: $scope.currentGrantee.empId,
            startDate: null,
            endDate: null,
            permanent: false
        };
    };

    $scope.init();
}
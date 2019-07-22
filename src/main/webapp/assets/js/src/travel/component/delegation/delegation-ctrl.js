var essTravel = angular.module('essTravel');

essTravel.controller('DelegationCtrl',
                     ['$scope', '$timeout', 'appProps', 'DelegationApi', 'ActiveEmployeeApi', delegationCtrl]);

function delegationCtrl($scope, $timeout, appProps, delegationApi, empApi) {

    const DATEPICKER_FORMAT = "MM/DD/YYYY";

    var vm = this;
    vm.displaySavedMessage = false;

    (function () {
        vm.activeDelegations = [];
        vm.allowedDelegates = []; // Employees who are allowed to be assigned as a delegate of the current user.

        setActiveDelegations();
        setAllowedDelegates();
    })();

    function setActiveDelegations() {
        delegationApi.findDelegationsByPrincipalId(appProps.user.employeeId).$promise
            .then(function (response) {
                vm.activeDelegations = response.result.filter(function (d) {
                    return !d.isExpired;
                });
        })
    }

    function setAllowedDelegates() {
        empApi.get({activeOnly: true}).$promise
            .then(function (response) {
                vm.allowedDelegates = response.employees;
            });
    }

    vm.addNewDelegation = function () {
        vm.activeDelegations.push(new Delegation());
    };

    vm.deleteDelegation = function (index) {
        vm.activeDelegations.splice(index, 1);
    };

    vm.saveDelegations = function () {
        delegationApi.saveDelegations(vm.activeDelegations).$promise
            .then(function (response) {
                vm.displaySavedMessage = true;
                $timeout(function() {
                    vm.displaySavedMessage = false;
                }, 1500);
            });
    };

    vm.useStartDate = function (delegation) {
        if (delegation.useStartDate === true) {
            delegation.startDate = moment().format(DATEPICKER_FORMAT);
        } else {
            delegation.startDate = undefined;
        }
    };

    vm.useEndDate = function (delegation) {
        if (delegation.useEndDate === true) {
            delegation.endDate = delegation.useStartDate ? delegation.startDate : moment().format(DATEPICKER_FORMAT);
        } else {
            delegation.endDate = undefined;
        }
    };

    function Delegation () {
        this.id = 0;
        this.delegate = undefined;
        this.useStartDate = false;
        this.startDate = undefined;
        this.useEndDate = false;
        this.endDate = undefined;
    }
}

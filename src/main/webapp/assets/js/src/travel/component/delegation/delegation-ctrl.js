var essTravel = angular.module('essTravel');

essTravel.controller('DelegationCtrl',
                     ['$scope', '$timeout', 'appProps', 'DelegationApi', 'ActiveEmployeeApi', delegationCtrl]);

function delegationCtrl($scope, $timeout, appProps, delegationApi, empApi) {

    const DATEPICKER_FORMAT = "MM/DD/YYYY";

    var vm = this;
    vm.data = {
        displaySavedMessage: false,
        activeDelegations: [],
        allowedDelegates: [], // Employees who are allowed to be assigned as a delegate of the current user.
        isLoading: true
    };

    (function () {
        setActiveDelegations();
        setAllowedDelegates();
    })();

    function setActiveDelegations() {
        delegationApi.findDelegationsByPrincipalId(appProps.user.employeeId).$promise
            .then(function (response) {
                vm.data.activeDelegations = response.result.filter(function (d) {
                    return !d.isExpired;
                });
        })
    }

    function setAllowedDelegates() {
        empApi.get({activeOnly: true}).$promise
            .then(function (response) {
                vm.data.allowedDelegates = response.employees;
                vm.data.isLoading = false;
            });
    }

    vm.addNewDelegation = function () {
        vm.data.activeDelegations.push(new Delegation());
    };

    vm.deleteDelegation = function (index) {
        vm.data.activeDelegations.splice(index, 1);
    };

    vm.saveDelegations = function () {
        delegationApi.saveDelegations(vm.data.activeDelegations).$promise
            .then(function (response) {
                vm.data.displaySavedMessage = true;
                $timeout(function() {
                    vm.data.displaySavedMessage = false;
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

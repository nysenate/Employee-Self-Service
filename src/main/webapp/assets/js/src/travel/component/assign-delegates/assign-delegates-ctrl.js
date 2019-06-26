var essTravel = angular.module('essTravel');

essTravel.controller('AssignDelegatesCtrl',
                     ['$scope', '$timeout', 'appProps', 'DelegateApi', 'ActiveEmployeeApi', assignDelegates]);

function assignDelegates($scope, $timeout, appProps, delegateApi, empApi) {

    const DATEPICKER_FORMAT = "MM/DD/YYYY";

    var vm = this;
    vm.displaySavedMessage = false;

    (function () {
        vm.activeDelegates = [];
        vm.possibleDelegates = [];

        queryActiveDelegates();
        queryPossibleDelegates();
    })();

    function queryActiveDelegates() {
        delegateApi.findDelegatesByPrincipalId(appProps.user.employeeId).$promise
            .then(function (response) {
                vm.activeDelegates = response.result;
        })
    }

    function queryPossibleDelegates() {
        empApi.get({activeOnly: true}).$promise
            .then(function (response) {
                vm.possibleDelegates = response.employees;
            });
    }

    vm.addNewDelegate = function () {
        vm.activeDelegates.push(new Delegate());
    };

    vm.deleteDelegate = function (index) {
        vm.activeDelegates.splice(index, 1);
    };

    vm.saveDelegates = function () {
        delegateApi.saveDelegates(vm.activeDelegates).$promise
            .then(function (response) {
                vm.displaySavedMessage = true;
                $timeout(function() {
                    vm.displaySavedMessage = false;
                }, 1500);
            });
    };

    vm.useStartDate = function (delegate) {
        if (delegate.useStartDate === true) {
            delegate.startDate = moment().format(DATEPICKER_FORMAT);
        } else {
            delegate.startDate = undefined;
        }
    };

    vm.useEndDate = function (delegate) {
        if (delegate.useEndDate === true) {
            delegate.endDate = delegate.useStartDate ? delegate.startDate : moment().format(DATEPICKER_FORMAT);
        } else {
            delegate.endDate = undefined;
        }
    };

    function Delegate () {
        this.id = 0;
        this.delegate = undefined;
        this.useStartDate = false;
        this.startDate = undefined;
        this.useEndDate = false;
        this.endDate = undefined;
    }
}

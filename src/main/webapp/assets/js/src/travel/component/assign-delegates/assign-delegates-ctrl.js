var essTravel = angular.module('essTravel');

essTravel.controller('AssignDelegatesCtrl',
                     ['$scope', 'appProps', 'DelegateApi', 'ActiveEmployeeApi', assignDelegates]);

function assignDelegates($scope, appProps, delegateApi, empApi) {

    const DATEPICKER_FORMAT = "MM/DD/YYYY";

    var vm = this;
    vm.possibleDelegates = [];

    (function () {
        queryActiveDelegates();
        queryPossibleDelegates();
        vm.pastDelegates = queryPastDelegates();
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
                console.log(vm.possibleDelegates);
            });
    }

    function queryPastDelegates() {
        return [
            {
                fullName: "Kevin C",
                startDate: "2019/01/01",
                endDate: "2019/04/01"
            }
        ];
    }

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

    vm.addNewDelegate = function () {
        vm.activeDelegates.push(new Delegate());
        console.log(vm.activeDelegates);
    };

    vm.deleteDelegate = function (index) {
        vm.activeDelegates.splice(index, 1);
    };

    vm.saveDelegates = function () {
        delegateApi.saveDelegates(vm.activeDelegates).$promise
            .then(function (response) {
                // Update activeDelegates from the response so id's are updated.
                vm.activeDelegates = response.result;
            });
    };

    function Delegate () {
        this.delegate = undefined;
        this.useStartDate = false;
        this.startDate = undefined;
        this.endDate = undefined;
    }
}

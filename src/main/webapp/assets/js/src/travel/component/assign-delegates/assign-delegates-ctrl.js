var essTravel = angular.module('essTravel');

essTravel.controller('AssignDelegatesCtrl',
                     ['$scope', assignDelegates]);

function assignDelegates($scope) {

    const DATEPICKER_FORMAT = "MM/DD/YYYY";

    var vm = this;
    vm.possibleDelegates = [];

    (function () {
        vm.activeDelegates = queryActiveDelegates();
        vm.possibleDelegates = queryPossibleDelegates();
        vm.pastDelegates = queryPastDelegates();
    })();

    function queryActiveDelegates() {
        return [];
    }

    function queryPossibleDelegates() {
        return ["Sam S", "Anthony C", "Kevin C"]
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
        // TODO Implement when backend is done.
    };

    function Delegate () {
        this.fullName = undefined;
        this.useStartDate = false;
        this.startDate = undefined;
        this.endDate = undefined;
    }
}

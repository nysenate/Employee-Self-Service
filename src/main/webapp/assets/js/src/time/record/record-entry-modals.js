angular.module('essTime')
    .directive('recordPostSaveModal', ['appProps', 'modals', postSaveModal])
    .directive('recordSubmitAckModal', ['appProps', 'modals', submitAckModal])
    .directive('recordFutureEndConfModal', ['appProps', 'modals', futureEndDateConfModal])
    .directive('recordExpectedHoursModal', ['appProps', 'modals', expectedHoursModal])
;

function postSaveModal (appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/record/record-post-save-modal',
        link: link
    };
    function link ($scope, $elem, $attrs) {
        var params = modals.params();
        $scope.submit = params.submit;
        $scope.resolve = modals.resolve;
        $scope.reject = modals.reject;
    }
}

function submitAckModal (appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/record/record-submit-ack-modal',
        link: link
    };
    function link ($scope, $elem, $attrs) {
        $scope.resolve = modals.resolve;
        $scope.reject = modals.reject;
    }
}

function futureEndDateConfModal (appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/record/record-future-end-date-conf-modal',
        link: link
    };
    function link ($scope, $elem, $attrs) {
        $scope.resolve = modals.resolve;
        $scope.reject = modals.reject;
    }
}

function expectedHoursModal (appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/record/record-expected-hrs-modal',
        link: link
    };
    function link ($scope, $elem, $attrs) {
        var params = modals.params();

        $scope.serviceYtd = params.serviceYtd;
        $scope.serviceYtdExpected = params.serviceYtdExpected;
        $scope.biWeekHrsExpected = params.biWeekHrsExpected;
        $scope.raSaTotal = params.raSaTotal;

        $scope.serviceSurplus = $scope.serviceYtd - $scope.serviceYtdExpected;
        $scope.expectedDifference = $scope.biWeekHrsExpected - $scope.raSaTotal;

        $scope.resolve = modals.resolve;
        $scope.reject = modals.reject;
    }
}

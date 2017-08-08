
angular.module('essMyInfo')
    .controller('AlertCtrl', ['$scope', '$timeout', '$filter', 'appProps', 'AlertInfoApi',
                                              alertCtrl])
    ;

function alertCtrl($scope, $timeout, $filter, appProps, alertInfoApi) {

    $scope.telPattern = /^ *(\([0-9]{3}\)|[0-9]{3} *-?) *[0-9]{3} *-? *[0-9]{4} *$/;
    $scope.phoneErrorMsg = "Please enter a valid phone number";
    $scope.emailErrorMsg = "Please enter a valid email address";

    var initialState = {
        name: appProps.user.fullName,
        empId: appProps.user.employeeId,
        request: {},
        alertInfo: null
    };

    /* --- Initialization --- */

    function init() {
        $scope.state = angular.extend(initialState);
        return getAlertInfo();
    }

    init();

    /* --- Display Methods --- */

    $scope.saveAlertInfo = saveAlertInfo;

    $scope.isLoading = function () {
        var loading = false;
        angular.forEach($scope.state.request, function (status) {
            loading = loading || status;
        });
        return loading;
    };

    $scope.validMobileOptions = function () {
        var alertInfo = $scope.state.alertInfo;
        if (!alertInfo.mobilePhone) {
            return true;
        }

        return !alertInfo.mobilePhone || alertInfo.mobileCallable || alertInfo.mobileTextable;
    };

    /* --- Api Methods --- */

    /**
     * Retrieves the user's alert info
     */
    function getAlertInfo() {
        var params = {
            empId: $scope.state.empId
        };
        $scope.state.request.loadingAlertInfo = true;
        return alertInfoApi.get(params, onSuccess, $scope.handleErrorResponse)
            .$promise
            .finally(postRequest);

        function onSuccess(resp) {
            $scope.state.alertInfo = resp.result;
            $scope.state.alertInfo.homePhone = $filter('phoneNumber')($scope.state.alertInfo.homePhone);
            $scope.state.alertInfo.mobilePhone = $filter('phoneNumber')($scope.state.alertInfo.mobilePhone);
            $scope.state.alertInfo.alternatePhone = $filter('phoneNumber')($scope.state.alertInfo.alternatePhone);
        }

        function postRequest() {
            $scope.state.request.loadingAlertInfo = false;
        }
    }

    /**
     * Saves the current alert info and reinitialize the page
     */
    function saveAlertInfo() {
        $scope.state.request.savingAlertInfo = true;

        ensureMobileOptions();

        return alertInfoApi.save({}, $scope.state.alertInfo, onSuccess, $scope.handleErrorResponse)
            .$promise
            .then(init)
            .then(setSaved)
            .finally(postRequest);

        function onSuccess() {
        }

        function postRequest() {
            $scope.state.request.savingAlertInfo = false;
        }
    }

    /**
     * Flicker the saved state to trigger a transition
     */
    function setSaved() {
        $scope.state.saved = true;
        console.log('saved');
        $timeout(function () {
            $scope.state.saved = false;
        });
    }

    /**
     * Ensures that the mobile options are valid before saving alert info.
     * Invalid alert options should not be allowed if a mobile number is specified.
     * If no mobile number is specified, and the alert options are valid, then they should be set to default
     * to prevent errors on the backend.
     */
    function ensureMobileOptions() {
        var alertInfo = $scope.state.alertInfo;

        if (!$scope.validMobileOptions()) {
            throw {
                message: "Attempt to post alert info with invalid mobile options",
                alertInfo: alertInfo
            };
        }

        if (!(alertInfo.mobileCallable || alertInfo.mobileTextable)) {
            alertInfo.mobileCallable = true;
            alertInfo.mobileTextable = true;
        }
    }

}
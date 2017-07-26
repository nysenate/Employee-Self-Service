
angular.module('essMyInfo')
    .controller('EmergencyNotificationCtrl', ['$scope', '$timeout', '$filter', 'appProps', 'EmergencyNotificationInfoApi',
                                              emergencyNotificationCtrl])
    ;

function emergencyNotificationCtrl($scope, $timeout, $filter, appProps, eniApi) {

    $scope.telPattern = /^ *(\([0-9]{3}\)|[0-9]{3} *-?) *[0-9]{3} *-? *[0-9]{4} *$/;
    $scope.phoneErrorMsg = "Please enter a valid phone number";
    $scope.emailErrorMsg = "Please enter a valid email address";

    var initialState = {
        name: appProps.user.fullName,
        empId: appProps.user.employeeId,
        request: {},
        eni: null
    };

    /* --- Initialization --- */

    function init() {
        $scope.state = angular.extend(initialState);
        return getENI();
    }

    init();

    /* --- Display Methods --- */

    $scope.saveENI = saveENI;

    $scope.isLoading = function () {
        var loading = false;
        angular.forEach($scope.state.request, function (status) {
            loading = loading || status;
        });
        return loading;
    };

    /* --- Api Methods --- */

    /**
     * Retrieves the user's emergency notification info
     */
    function getENI() {
        var params = {
            empId: $scope.state.empId
        };
        $scope.state.request.loadingEni = true;
        return eniApi.get(params, onSuccess, $scope.handleErrorResponse)
            .$promise
            .finally(postRequest);

        function onSuccess(resp) {
            $scope.state.eni = resp.result;
            $scope.state.eni.homePhone = $filter('phoneNumber')($scope.state.eni.homePhone);
            $scope.state.eni.mobilePhone = $filter('phoneNumber')($scope.state.eni.mobilePhone);
            $scope.state.eni.alternatePhone = $filter('phoneNumber')($scope.state.eni.alternatePhone);
        }

        function postRequest() {
            $scope.state.request.loadingEni = false;
        }
    }

    /**
     * Saves the current ENI and reinitializes the page
     */
    function saveENI() {
        $scope.state.request.savingEni = true;

        return eniApi.save({}, $scope.state.eni, onSuccess, $scope.handleErrorResponse)
            .$promise
            .then(init)
            .then(setSaved)
            .finally(postRequest);

        function onSuccess() {
        }

        function postRequest() {
            $scope.state.request.savingEni = false;
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

}
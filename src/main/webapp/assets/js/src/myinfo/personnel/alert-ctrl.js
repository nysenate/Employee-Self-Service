
angular.module('essMyInfo')
    .controller('AlertCtrl', ['$scope', '$timeout', '$filter',
                              'appProps', 'modals', 'AlertInfoApi',
                              alertCtrl])
;

function alertCtrl($scope, $timeout, $filter, appProps, modals, alertInfoApi) {

    $scope.telPattern = /^ *(\([0-9]{3}\)|[0-9]{3} *-?) *[0-9]{3} *-? *[0-9]{4} *$/;
    /** Additional email pattern to standard html validation that requires a . in the host */
    $scope.emailPattern = /^.*@.*\.[A-z]{2,}$/;
    $scope.phoneErrorMsg = "Please enter a valid phone number";
    $scope.emailErrorMsg = "Please enter a valid email address";
    $scope.CONTACT_OPTIONS = {CALLS_ONLY: "Calls", TEXTS_ONLY: "Texts", EVERYTHING: "Both"};

    var phoneNumberFields = [
        'workPhone',
        'homePhone',
        'alternatePhone',
        'mobilePhone'
    ];

    var emailFields = [
        'workEmail',
        'personalEmail',
        'alternateEmail'
    ];

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

    /**
     * Checks for valid mobile options
     * @returns {boolean} true if at least one mobile option is selected
     */
    $scope.validMobileOptions = function () {
        var alertInfo = $scope.state.alertInfo;
        if (!alertInfo.mobilePhone) {
            return true;
        }

        return !alertInfo.mobilePhone || alertInfo.mobileCallable || alertInfo.mobileTextable;
    };

    /**
     * Checks for duplicate phone numbers.
     * Ignores non-numeric formatting.
     * @returns {boolean} true if there are no duplicate phone numbers
     */
    $scope.noDuplicatePhoneNumbers = function () {
        var phoneNumberSet = {};
        for (var i in phoneNumberFields) {
            if (!phoneNumberFields.hasOwnProperty(i)) {
                continue;
            }

            var phoneNumber = $scope.state.alertInfo[phoneNumberFields[i]];

            var formattedPhoneNumber = (phoneNumber || '').replace(/[^\d]+/g, '');

            if (!formattedPhoneNumber) {
                continue;
            }

            if (phoneNumberSet.hasOwnProperty(formattedPhoneNumber)) {
                return false;
            }

            phoneNumberSet[formattedPhoneNumber] = true;
        }
        return true;
    };

    /**
     * Checks for duplicate email addresses.
     * Case insensitive
     * @returns {boolean} true if there are no duplicate email addresses
     */
    $scope.noDuplicateEmails = function () {
        var emailSet = {};
        for (var i in emailFields) {
            if (!emailFields.hasOwnProperty(i)) {
                continue;
            }

            var email = $scope.state.alertInfo[emailFields[i]];

            var formattedEmail = (email || '').replace(/^\s+|\s+$/g, '').toLowerCase();

            if (!formattedEmail) {
                continue;
            }

            if (emailSet.hasOwnProperty(formattedEmail)) {
                return false;
            }

            emailSet[formattedEmail] = true;
        }
        return true;
    };

    /**
     * Performs overall validation for the given alert info,
     * returning true if it is valid
     */
    $scope.validAlertInfo = function () {
        return $scope.validMobileOptions() &&
            $scope.noDuplicatePhoneNumbers() &&
            $scope.noDuplicateEmails();
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

        ensureContactOptions();

        return alertInfoApi.save({}, $scope.state.alertInfo, onSuccess, onFail)
            .$promise
            .then(init)
            .then(setSaved)
            .finally(postRequest);

        function onSuccess() {
        }

        function onFail(resp) {
            if (resp.data.errorCode === 'INVALID_ALERT_INFO' &&
                resp.data.errorData.alertErrorCode === 'INVALID_EMAIL') {
                $scope.errorData = resp.data.errorData.alertErrorData;
                modals.open('invalid-email-dialog');
            } else {
                $scope.handleErrorResponse();
            }
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
    function ensureContactOptions() {
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
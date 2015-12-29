/*! Login Page */
var essApp = angular.module('ess');

essApp.factory('LoginApi', ['$resource', 'appProps', function ($resource, appProps) {
    return $resource(appProps.ctxPath + appProps.loginUrl, {}, {
        'login': {
            method: 'POST',
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/x-www-form-urlencoded'
            },
            withCredentials: true
        }
    });
}]);

essApp.controller('LoginController', ['$scope', '$http', '$window', 'appProps', 'LoginApi',
    function($scope, $http, $window, appProps, LoginApi) {
    $scope.credentials = {
        username: '',
        password: '',
        rememberMe: false
    };

    $scope.activeView = 1;
    $scope.showError = false;
    $scope.errorFields = {
        username: false,
        password: false,
        reset: function() {
            this.username = false;
            this.password = false;
        }
    };
    $scope.errorMessage = '';
    $scope.loginInProgress = false;

    $scope.isActiveView = function(index) {
        return this.activeView === index;
    };

    $scope.setActiveView = function(index) {
        this.activeView = index;
    };

    $scope.validate = function() {
        $scope.errorFields.reset();
        $scope.showError = false;
        if (!$scope.credentials.username) {
            $scope.errorMessage = "Please enter your username.";
            $scope.errorFields.username = true;
        }
        else if (!$scope.credentials.password) {
            $scope.errorMessage = "Please enter your password.";
            $scope.errorFields.password = true;
        }
        else {
            return true;
        }
        $scope.showError = true;
        return false;
    };

    $scope.login = function() {
        if ($scope.validate()) {
            $scope.loginInProgress = true;
            LoginApi.login($.param($scope.credentials), function(authResponse, status) {
                if (authResponse) {
                    if (authResponse.authenticated === true) {
                        $window.location.href = authResponse.redirectUrl;
                    }
                    else {
                        $scope.errorMessage = authResponse.message;
                        $scope.showError = true;
                        if (authResponse.status == 'UNKNOWN_ACCOUNT') {
                            $scope.errorFields.username = true;
                        }
                        else if (authResponse.status == 'INCORRECT_CREDENTIALS') {
                            $scope.errorFields.password = true;
                        }
                        $scope.loginInProgress = false;
                    }
                }
            }, function(data) {
                $scope.loginInProgress = false;
                alert("There was an issue logging in. Please try again.");
            });
        }
    }
}]);
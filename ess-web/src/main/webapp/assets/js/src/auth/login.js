/*! Login Page */
var essApp = angular.module('ess');

essApp.controller('LoginController', ['$scope', '$http', '$window', 'appProps',
    function($scope, $http, $window, appProps) {
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
        this.errorFields.reset();
        this.showError = false;
        if (!this.credentials.username) {
            this.errorMessage = "Please enter your username.";
            this.errorFields.username = true;
        }
        else if (!this.credentials.password) {
            this.errorMessage = "Please enter your password.";
            this.errorFields.password = true;
        }
        else {
            return true;
        }
        this.showError = true;
        return false;
    };

    $scope.login = function() {
        if ($scope.validate()) {
            var loginUrl = appProps.ctxPath + appProps.loginUrl;
            var credentialsParam = $.param($scope.credentials);
            var config = {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            };
            $scope.loginInProgress = true;
            $http.post(loginUrl, credentialsParam, config)
                .success(function(authResponse, status) {
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
                })
                .error(function(data, status, headers, config) {
                    $scope.loginInProgress = false;
                });
        }
    }
}]);
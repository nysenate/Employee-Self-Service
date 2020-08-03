var essApp = angular.module('ess');

essApp.directive('internalErrorModal', ['modals','ErrorReportApi','appProps',
function (modals,errorReportApi,appProps) {
    return {
        template:
        '<section id="internal-error-modal" class="error-modal" title="Internal Error">' +
            '<h1>Internal Error</h1>' +
            '<p class="error-description">' +
                'We are sorry to report that an error occurred on the ESS server while processing your request.<br/>' +
                'Please contact the STS Helpline at {{helplinePhoneNumber}} and notify us of this issue so that we can fix it!' +
            '</p>' +
            '<pre class="error-details" ng-show="showDetails">{{details | json}}</pre>' +
            '<p style="color: red" ng-show="showFailure">' +
                'Sorry, your report cannot be sent.  Please contact the STS Helpline at {{helplinePhoneNumber}}.' +
            '</p>' +
            '<div ng-show="sendingReport">' +
                '<h3 class="loading-text">Sending Error Report ...</h3>' +
                '<div loader-indicator class="sm-loader"></div>' +
            '</div>' +
            '<div class="button-container">' +
                '<input type="button" class="reject-button" value="{{showDetails ? \'Hide\' : \'Show\'}} Details"' +
                    'ng-click="showDetails = !showDetails" />' +
                '<input type="button" class="reject-button" value="Report Error"' +
                    'ng-click="report()" ng-disabled="sendingReport" />' +
                '<input type="button" class="reject-button" value="OK"' +
                    'ng-click="close()" ng-disabled="sendingReport" />' +
            '</div>' +
        '</section>',
        link: function ($scope, $element, $attrs) {
            $scope.showDetails = false;
            $scope.showFailure= false;
            $scope.sendingReport = false;
            $scope.details = modals.params().details;
            $scope.report = function () {
                var params = {
                    user:appProps.user.employeeId,
                    url:window.location.href,
                    details: $scope.details
                };
                $scope.sendingReport = true;
                errorReportApi.save(params,function (resp) {
                    modals.resolve();
                },function (resp) {
                    $scope.showFailure = true;
                }).$promise.finally(function () {
                    $scope.sendingReport = false;
                });
            };
            $scope.close = modals.reject;
        }
    };
}]);

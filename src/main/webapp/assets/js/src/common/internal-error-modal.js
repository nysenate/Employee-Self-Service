var essApp = angular.module('ess');

essApp.directive('internalErrorModal', ['modals','ErrorReportApi','appProps',
function (modals,errorReportApi,appProps) {
    return {
        template:
        '<section id="internal-error-modal" title="Internal Error">' +
            '<h1>Internal Error</h1>' +
            '<p class="internal-error-text">' +
                'We are sorry to report that an error occurred on the ESS server while processing your request.<br/>' +
                'Please contact the STS Helpline at (518) 455-2011 and notify us of this issue so that we can fix it!' +
            '</p>' +
        '<p style="color: red" ng-show="showFailure">Sorry, your report cannot be sent.  Please contact the STS Helpline at (518) 455-2011.</p>' +
        '<pre class="internal-error-details" ng-show="showDetails">{{details | json}}</pre>' +
            '<div class="button-container">' +
                '<input type="button" class="reject-button" ng-click="showDetails = !showDetails" value="{{showDetails ? \'Hide\' : \'Show\'}} Details"/>' +
                '<input type="button" class="reject-button" ng-click="report()" value="Report Error"/>' +
                '<input type="button" class="reject-button" ng-click="close()" value="OK"/>' +
            '</div>' +
        '</section>',
        link: function ($scope, $element, $attrs) {
            $scope.showDetails = false;
            $scope.showFailure= false;
            $scope.details = modals.params().details;
            $scope.report = function () {
                var params = {
                    user:appProps.user.employeeId,
                    url:window.location.href,
                    details: $scope.details
                };
                errorReportApi.get(params,function (resp) {
                    modals.resolve();
                },function (resp) {
                    $scope.showFailure = true;
                });
            };
            $scope.close = modals.reject;
        }
    };
}]);

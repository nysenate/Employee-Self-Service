angular.module('ess')
    .controller('ErrorModalTestCtrl', ['$scope', 'modals', errorModalTestCtrl]);

function errorModalTestCtrl ($scope, modals) {
    $scope.testData = {
        explanation: "This is a test of the error reporting system",
        someField: "this field would normally contain error data",
        obj: {
            anotherField: "Hi mom",
            yetAnotherField: 3
        }
    };

    console.log('opening error modal... ');
    modals.open('500', {action: 'test error modal', details: $scope.testData});
}
var essTravel = angular.module('essTravel');

essTravel.directive('dateValidator', function () {
    return {
        require: 'ngModel',
        link: function ($scope, elm, attrs, ctrl) {
            ctrl.$validators.dateValidator = function (modelValue, viewValue) {
                if (!modelValue) {
                    return false;
                }
                if (moment(modelValue, 'M/D/YY', true).isValid() || moment(modelValue, 'M/D/YYYY', true).isValid()) {
                    return true;
                }
            }
        }
    }
});
var essTravel = angular.module('essTravel');

/**
 * Validator for Method of Transportation select elements.
 * Requires that a method of transportation is selected.
 */
essTravel.directive('motValidator', function () {
    return {
        require: 'ngModel',
        link: function ($scope, elm, attrs, ctrl) {
            ctrl.$validators.motValidator = function (modelValue, viewValue) {
                if (modelValue && modelValue.methodOfTravel == null) {
                    return false;
                }
                return true;
            }
        }
    }
});
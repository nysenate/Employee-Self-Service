var essTravel = angular.module('essTravel');

/**
 * Validator for Method of Transportation select elements.
 * Requires that a method of transportation is selected.
 */
essTravel.directive('motValidator', function () {
    return {
        require: 'ngModel',
        link: function ($scope, elm, attrs, ctrl) {
            ctrl.$validators.motRequired = function (modelValue, viewValue) {
                return modelValue;
            };
        }
    }
});

/**
 * Simply requires a mot description.
 *
 * We use a custom validator instead of the 'required' attribute because the input element
 * this is used on are named dynamically (since there can be multiple segments) which makes it impossible
 * to detect an error with this field and display an error message for it.
 */
essTravel.directive('motDescriptionValidator', function () {
    return {
        require: 'ngModel',
        link: function ($scope, elm, attrs, ctrl) {
            ctrl.$validators.motDescription = function (modelValue, viewValue) {
                return modelValue;
            }
        }
    }
});
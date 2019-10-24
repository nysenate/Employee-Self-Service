var essTravel = angular.module('essTravel');

/**
 * Simple validators for the Purpose of travel page.
 *
 * These validators don't do any special validation, they are functionally the same as the required attribute.
 * However, using these validators will name fields on the form.$error object after them. This allows us
 * to display a custom error message dependent on which field is invalid.
 *
 * i.e an error with the eventType and eventName validators will show up on the form like this:
 * form.$error = {
 *     eventTypeRequired: [],
 *     eventNameRequired: []
 * }
 *
 * If we used the required attribute instead:
 * form.$error = {
 *     required: []
 * }
 *
 * Using the required attribute would make it hard to know which field/s had errors.
 */

essTravel.directive('eventTypeValidator', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.eventTypeRequired = function (modelValue, viewValue) {
                return modelValue;
            };
        }
    }
});

essTravel.directive('eventNameValidator', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.eventNameRequired = function (modelValue, viewValue) {
                return modelValue;
            };
        }
    }
});

essTravel.directive('additionalPurposeValidator', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$validators.additionalPurposeRequired = function (modelValue, viewValue) {
                return modelValue;
            };
        }
    }
});

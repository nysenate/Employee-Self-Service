var essSupply = angular.module('essSupply');

/**
 * Validator for entering custom order quantities.
 * Limits key input to number keys and navigation keys.
 */
essSupply.directive('orderQuantityValidator', [function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ngModel) {
            elm.bind("keydown", function (event) {
                // Allow backspace, tab and F5 keys to be pressed
                if (event.keyCode === 8 || event.keyCode === 9 || event.keyCode === 116) {
                    return;
                }
                // Allow 0-9 numbers from keyboard or numpad.
                if ((event.keyCode >= 48 && event.keyCode <=57) || (event.keyCode >= 96 && event.keyCode <= 105)) {
                    return;
                } else {
                    event.preventDefault();
                }
            });
        }
    }
}]);

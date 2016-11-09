var essSupply = angular.module('essSupply');

/**
 * Validator for entering custom order quantities.
 * Limits key input to number keys and navigation keys.
 */
essSupply.directive('orderQuantityValidator', [function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ngModel) {
            // Only allow numbers, backspace, tab, and F5 keys to be pressed.
            elm.bind("keydown", function (event) {
                if (event.keyCode === 8 || event.keyCode === 9 || event.keyCode === 116) {
                    return;
                }
                if (event.keyCode < 48 || event.keyCode > 57) {
                    event.preventDefault();
                }
            });
        }
    }
}]);

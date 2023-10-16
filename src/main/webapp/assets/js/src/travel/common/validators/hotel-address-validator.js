var essTravel = angular.module('essTravel');

/**
 * Validates address are selected from the google autocomplete.
 */
essTravel.directive('hotelAddressValidator', function () {
    return {
        require: 'ngModel',
        link: function ($scope, elm, attrs, ctrl) {
            elm.on('keydown', function (e) {
                // Reset address when manually edited.
                var address = $scope.perDiem.address; // Gets either the from or to address
                address.addr1 = '';
                address.addr2 = '';
                address.city = '';
                address.county = '';
                address.state = '';
                address.zip4 = '';
                address.zip5 = '';
                address.country = '';

            });
            ctrl.$validators.addressValidator = function (modelValue, viewValue) {

                // Parse the ng-model attribute to determine if we should validate the 'to' or 'from' address.
                var address = $scope.perDiem.address;

                if (!modelValue) {
                    return false;
                }
                if (!address.zip5) {
                    return false;
                }
                return true;
            }
        }
    }
});
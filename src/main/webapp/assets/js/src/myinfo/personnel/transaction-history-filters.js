var essMyInfo = angular.module('essMyInfo');

essMyInfo.filter('payTypeFilter', function(){
    return function(input) {
        switch (input) {
            case 'RA': return 'Regular Annual';
            case 'SA': return 'Special Annual';
            case 'TE': return 'Temporary Payroll';
        }
        return input;
    }
});
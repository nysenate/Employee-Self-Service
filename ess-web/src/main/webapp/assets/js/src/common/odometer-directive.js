var essApp = angular.module('ess');

/**
 * Odometer directive turns elements into scrolling numbers.
 * Usage:
 *
 *    <div odometer value={{someVal}}" ></div>
 *
 * where someVal is a property of the parent scope, e.g. $scope.someVal = 42;
 * @see http://github.hubspot.com/odometer/
 */
essApp.directive('odometer', [function(){
    return {
        restrict: 'AE',
        scope: {
            duration: '=odometerDuration',
            format: '=odometerFormat',
            value: "@value"
        },
        link: function($scope, element, attrs) {
            var od = new Odometer({
                el: element[0],
                value: $scope.value || 0,
                animation: 'count',
                duration: $scope.duration || 500,
                format: $scope.format || '(,ddd).dd'
            });

            attrs.$observe("value", function(val){
                od.update(val);
            });
        }
    }
}]);
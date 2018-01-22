/**
 * An attribute directive for an iframe
 * that allows a provided function to be executed when the iframe loads.
 * The callback function is passed the element object of the iframe
 */
angular.module('ess')
    .directive('iframeOnLoad', function (){
        return {
            restrict: 'A',
            scope: {
                callback: '&iframeOnLoad'
            },
            link: function ($scope, $elem, $attrs) {
                $elem.on('load', function () {
                    return $scope.callback({elem: $elem});
                })
            }
        };
    });

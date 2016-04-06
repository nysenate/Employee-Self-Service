var essApp = angular.module('ess');

essApp.directive('textAutoHeight', ['$timeout', function ($timeout) {
    return {
        restrict: 'A',
        scope: { text: '=' },
        link: function($scope, $elem, $attrs) {
            $timeout(function() {
                var minHeight = $elem[0].offsetHeight,
                    paddingLeft = $elem.css('paddingLeft'),
                    paddingRight = $elem.css('paddingRight');
                var $shadow = angular.element('<div></div>').css({
                    position: 'absolute',
                    top: -10000,
                    left: -10000,
                    width: $elem[0].offsetWidth - parseInt(paddingLeft || 0) - parseInt(paddingRight || 0),
                    fontSize: $elem.css('fontSize'),
                    fontFamily: $elem.css('fontFamily'),
                    lineHeight: $elem.css('lineHeight'),
                    resize: 'none'
                });
                angular.element(document.body).append($shadow);

                var update = function () {
                    var times = function (string, number) {
                        for (var i = 0, r = ''; i < number; i++) {
                            r += string;
                        }
                        return r;
                    };

                    var val = $elem.val().replace(/</g, '&lt;')
                        .replace(/>/g, '&gt;')
                        .replace(/&/g, '&amp;')
                        .replace(/\n$/, '<br/>&nbsp;')
                        .replace(/\n/g, '<br/>')
                        .replace(/\s{2,}/g, function (space) {
                            return times('&nbsp;', space.length - 1) + ' '
                        });
                    $shadow.html(val);

                    $elem.css('height', Math.max($shadow[0].offsetHeight + 10 /* the "threshold" */, minHeight) + 'px');
                };

                $elem.bind('keyup keydown keypress change', update);
                update();
            }, 50);
        }
    }
}]);

var essApp = angular.module('ess');

/**
 * Directive to handle UI interactions for the navigation menu.
 */
essApp.directive('essNavigation', ['$route', '$routeParams', '$location',
    function($route, $routeParams, $location) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {

            /** Apply active css when a main nav link is clicked */
            element.on("click", "ul li.main-topic", function(event) {
                if (!$(this).hasClass("active")) {
                    element.find(".main-topic.active").removeClass("active");
                    element.find(".sub-topic-list.active").removeClass("active");
                    $(this).addClass("active");
                    $(this).next("li").children("ul.sub-topic-list").addClass("active");
                }
            });

            /** React to route changes and set the active link that matches the url */
            scope.$on('$routeChangeStart', function(){
                // Remove existing 'active' classes
                element.find(".main-topic.active, .sub-topic-list.active, .sub-topic.active").removeClass("active");

                // Matches the last segment of a path
                var pathEndRe = /\/[^\/]*$/;

                var bestMatch = $location.$$path;

                // Strip the path down until a match is found or the path is empty.
                var $a;
                do {
                    $a = element.find(".sub-topic a[href='" + bestMatch + "']");
                    bestMatch = bestMatch.replace(pathEndRe, "");
                } while ($a.length < 1 && pathEndRe.test(bestMatch));

                // If a match was found, set 'active' classes on several levels
                if ($a.length === 1) {
                    var $subTopicLi = $a.parent();
                    var $subTopicUl = $subTopicLi.parent();
                    var $mainTopic = $subTopicUl.parent().prev();
                    $subTopicLi.add($subTopicUl).add($mainTopic).addClass("active");
                }
            });
        }
    }
}]);
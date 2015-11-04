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
                element.find(".main-topic.active, .sub-topic-list.active, .sub-topic.active").removeClass("active");
                var $a = element.find(".sub-topic a[href='" + $location.$$path + "']");
                if ($a.length == 1) {
                    var $subTopicLi = $a.parent();
                    var $subTopicUl = $subTopicLi.parent();
                    var $mainTopic = $subTopicUl.parent().prev();
                    $subTopicLi.add($subTopicUl).add($mainTopic).addClass("active");
                }
            });
        }
    }
}]);
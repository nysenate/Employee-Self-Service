/**
 * Use the err-src directive to give an alternative source when ng-src fails.
 * i.e. <img ng-src="item1040.png" err-src="defaultPic.png" /> Will load the item
 * specific image (item1040.png). If there are any errors loading that image it will load defaultPic instead.
 */
angular.module('ess').directive('errSrc', function () {
    return {
        link: function (scope, element, attrs) {
            element.bind('error', function () {
                if (attrs.src != attrs.errSrc) {
                    attrs.$set('src', attrs.errSrc);
                }
            });
        }
    }
});
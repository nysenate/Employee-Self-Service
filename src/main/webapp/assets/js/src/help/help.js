
angular.module('essHelp')
    .controller('HelpMainCtrl', ['$scope', '$window', 'appProps', helpCtrl])
;

/**
 * Parent controller for the Help section of ESS
 * @param $scope
 * @param $window
 * @param appProps
 */
function helpCtrl($scope, $window, appProps) {

    /** Url pointing to help pdf */
    $scope.helpPdfUrl = appProps.ctxPath + '/assets/pdf/ess-help.pdf';

    // Options for opening the help document in a new window
    var helpWindowName = 'helpWindow';
    var helpWindowOptions =
        'width=1024,height=768,location=no,menubar=no,personalbar=no,status=no,titlebar=no,toolbar=no';

    /**
     * Open the help document in a new window
     * @param $event
     */
    $scope.openHelpWindow = function ($event) {
        $window.open($scope.helpPdfUrl, helpWindowName, helpWindowOptions);
        $event.preventDefault();    // prevents following of displayed link
    }

}

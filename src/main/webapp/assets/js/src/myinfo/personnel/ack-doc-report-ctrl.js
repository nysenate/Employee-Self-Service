(function () {
    angular.module('essMyInfo')
        .controller('AckDocReportCtrl', ['$scope', '$q', 'appProps', 'modals', 'AckDocApi', 'AcknowledgmentYearApi',
                                         ackDocReportCtrl])
    ;

    function ackDocReportCtrl($scope, $q, appProps, modals, AckDocApi, AcknowledgmentYearApi) {
        var initialState = {
            documents: []
        };

        function init() {
        }

        init();

    }

})();
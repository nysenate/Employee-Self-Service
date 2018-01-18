(function () {

angular.module('essMyInfo')
    .controller('AckDocViewCtrl', ['$scope', '$routeParams', '$q', '$location',
                                   'appProps', 'modals', 'AckDocApi', 'AcknowledgementApi',
                                        acknowledgementCtrl]);

function acknowledgementCtrl($scope, $routeParams, $q, $location, appProps, modals, documentApi, ackApi) {

    $scope.ackDocPageUrl = appProps.ctxPath + '/myinfo/personnel/acknowledgement';

    var initialState = {
        docId: null,
        document: null,
        acknowledgements: {},
        acknowledged: false,
        ackTimestamp: null,
        docFound: false,

        request: {
            document: false,
            ackGet: false,
            ackPost: false
        }
    };

    function init() {
        $scope.state = angular.copy(initialState);
        $scope.state.docId = $routeParams.ackDocId;
        $q.all([
                   getDocument(),
                   getAcknowledgements()
               ]).then(processAcknowledgement);
    }

    /* --- Display methods --- */

    /**
     * Return true if any requests are currently in progress.
     * @return {boolean}
     */
    $scope.isLoading = function () {
        var loading = false;
        angular.forEach($scope.state.request, function (status) {
            loading = loading || status;
        });
        return loading;
    };

    $scope.acknowledgeDocument = function () {
        postAcknowledgement()
            .then(function () {
                $scope.updateAckBadge();
                return modals.open('acknowledge-success');
            })
            .then(function () {
                $location.url($scope.ackDocPageUrl)
            })
    };

    /* --- Request Methods --- */

    /**
     * Get a list of active documents from the server
     */
    function getDocument() {
        $scope.state.document = null;

        var params = {
            ackDocId: $scope.state.docId
        };

        $scope.state.request.document = true;
        return documentApi.get(params, onSuccess, onFail)
            .$promise.finally(function () {
                $scope.state.request.document = false;
            });

        function onSuccess(resp) {
            $scope.state.document = resp.document;
            $scope.state.docFound = true;
        }

        function onFail(resp) {
            $scope.state.docFound = false;
            if (resp && resp.data && resp.data.errorCode === 'ACK_DOC_NOT_FOUND') {
                console.warn("Couldn't find ack doc:", $scope.state.docId);
            } else {
                $scope.handleErrorResponse(resp);
            }
        }

    }

    /**
     * Get all acknowledgements for the currently authenticated employee.
     */
    function getAcknowledgements() {
        $scope.state.acknowledgements = {};

        var params = {
            empId: appProps.user.employeeId
        };

        $scope.state.request.ackGet = true;
        return ackApi.get(params, onSuccess, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.state.request.ackGet = false;
            });

        function onSuccess(resp) {
            angular.forEach(resp.acknowledgements, function (ack) {
                $scope.state.acknowledgements[ack.ackDocId] = ack;
            });
        }
    }

    function postAcknowledgement() {
        var params = {
            empId: appProps.user.employeeId,
            ackDocId: $scope.state.document.id
        };

        $scope.state.request.ackPost = true;
        return ackApi.save(params, {}, init, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.state.request.ackPost = false;
            });
    }

    /**
     * Determine whether the document was acknowledged based on received acknowledgements
     */
    function processAcknowledgement() {
        var acknowledgements = $scope.state.acknowledgements;
        var docId = $scope.state.document.id;
        if (acknowledgements.hasOwnProperty(docId)) {
            var ack = acknowledgements[docId];
            $scope.state.ackTimestamp = ack.timestamp;
            $scope.state.acknowledged = true
        } else {
            $scope.state.acknowledged = false
        }
    }

    init();
}

})();

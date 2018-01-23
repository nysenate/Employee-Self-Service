(function () {

angular.module('essMyInfo')
    .controller('AckDocViewCtrl', ['$scope', '$routeParams', '$q', '$location', 'bowser',
                                   'appProps', 'modals', 'AckDocApi', 'AcknowledgmentApi',
                                        acknowledgmentCtrl]);

function acknowledgmentCtrl($scope, $routeParams, $q, $location, bowser, appProps, modals, documentApi, ackApi) {

    $scope.ackDocPageUrl = appProps.ctxPath + '/myinfo/personnel/acknowledgments';

    var initialState = {
        docId: null,
        document: null,
        acknowledgments: {},
        acknowledged: false,
        ackTimestamp: null,
        docFound: false,
        docHeight: 500,
        docRead: false,

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
                   getAcknowledgments()
               ]).then(processAcknowledgment);
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

    /**
     * Post an acknowledgment that the doc has been read.
     * Reload the page if successful and prompt the user with navigation options.
     */
    $scope.acknowledgeDocument = function () {
        postAcknowledgment()
            .then(function () {
                $scope.updateAckBadge();
                return modals.open('acknowledge-success');
            })
            .then(function () {
                $location.url($scope.ackDocPageUrl)
            })
    };

    /**
     * Sets docRead to true to indicate that the document has been read
     */
    $scope.markDocRead = function () {
        $scope.state.docRead = true;
    };

    /**
     * Indicates if an iframe should be used instead of embed tag
     * Some browsers do not support embed, while others work better with embed vs iframe.
     * @return {*}
     */
    $scope.useIframe = function () {
        // Microsoft edge doesn't support embed
        return bowser.msedge;
    };

    /**
     * Indicates if an overlay should be placed over the embedded pdf.
     * In some browsers, you cannot scroll the parent container when th
     * @return {boolean|*}
     */
    $scope.useOverlay = function () {
        return bowser.chrome;
    };

    /**
     * Returns true if the embedded pdf document should be hidden.
     *
     * This is intended for IE, where embedded pdfs are always in front of all other elements,
     * causing the pdf to block modal windows.
     * This function will return true if a modal is open and the user is using IE.
     */
    $scope.hideEmbed = function () {
        return bowser.msie && modals.isOpen();
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
            $scope.state.docRead = false;
            setDocEmbedHeight();
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
     * Get all acknowledgments for the currently authenticated employee.
     */
    function getAcknowledgments() {
        $scope.state.acknowledgments = {};

        var params = {
            empId: appProps.user.employeeId
        };

        $scope.state.request.ackGet = true;
        return ackApi.get(params, onSuccess, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.state.request.ackGet = false;
            });

        function onSuccess(resp) {
            angular.forEach(resp.acknowledgments, function (ack) {
                $scope.state.acknowledgments[ack.ackDocId] = ack;
            });
        }
    }

    function postAcknowledgment() {
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
     * Determine whether the document was acknowledged based on received acknowledgments
     */
    function processAcknowledgment() {
        var acknowledgments = $scope.state.acknowledgments;
        var docId = $scope.state.document.id;
        if (acknowledgments.hasOwnProperty(docId)) {
            var ack = acknowledgments[docId];
            $scope.state.ackTimestamp = ack.timestamp;
            $scope.state.acknowledged = true
        } else {
            $scope.state.acknowledged = false
        }
    }

    function setDocEmbedHeight() {
        var document = $scope.state.document;
        var width = 840;
        var heightFactor = document.totalHeight / document.maxWidth;
        var pages = $scope.state.document.pageCount;
        $scope.state.docHeight = width * heightFactor;
    }

    init();
}

})();

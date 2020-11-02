(function () {

angular.module('essMyInfo')
    .controller('AcknowledgmentCtrl', ['$scope', '$q', 'appProps', 'modals', 'AllAckDocApi', 'AcknowledgmentApi',
                                   acknowledgmentCtrl]);

function acknowledgmentCtrl($scope, $q, appProps, modals, documentApi, ackApi) {

    var initialState = {
        documents: {
            unsorted: [],
            acknowledged: [],
            unacknowledged: []
        },
        acknowledgments: {},

        request: {
            documents: false,
            ack: false
        }
    };

    function init() {
        $scope.state = angular.copy(initialState);
        $q.all([
            getDocuments(),
            getAcknowledgments()
        ]).then(sortDocuments);

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
     * Get the acknowledged date of the given ack doc, if one exists
     * @param doc
     */
    $scope.getAcknowledgedDate = function (doc) {
        var acknowledgment = $scope.state.acknowledgments[doc.id];
        if (acknowledgment) {
            return acknowledgment.timestamp;
        } else {
            throw "No acknowledgment for doc " + doc.id + ": " + doc.title;
        }
    };

    /**
     * Return true if there are any ack docs, acknowledged or otherwise
     */
    $scope.anyAckDocs = function () {
        return $scope.state.documents.acknowledged.length > 0 ||
            $scope.state.documents.unacknowledged.length > 0;
    };

    /* --- Request Methods --- */

    /**
     * Get a list of active documents from the server
     */
    function getDocuments() {
        $scope.state.documents.unsorted = [];

        $scope.state.request.documents = true;
        return documentApi.get({}, onSuccess, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.state.request.documents = false;
        });

        function onSuccess(resp) {
            $scope.state.documents.unsorted = resp.documents;
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

        $scope.state.request.ack = true;
        return ackApi.get(params, onSuccess, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.state.request.ack = false;
        });

        function onSuccess(resp) {
            angular.forEach(resp.acknowledgments, function (ack) {
                $scope.state.acknowledgments[ack.ackDocId] = ack;
            });
        }
    }

    function sortDocuments() {
        var documents = $scope.state.documents;
        documents.acknowledged = [];
        documents.unacknowledged = [];

        while (documents.unsorted.length > 0) {
            var doc = documents.unsorted.shift();
            if ($scope.state.acknowledgments[doc.id]) {
                documents.acknowledged.push(doc);
            } else {
                documents.unacknowledged.push(doc);
            }
        }

        var activeUnacknowledgedDocs = [];
        while (documents.unacknowledged.length > 0) {
            var doc = documents.unacknowledged.shift();
            if ( doc.active === true ) {
                activeUnacknowledgedDocs.push(doc);
            }
        }
        documents.unacknowledged = activeUnacknowledgedDocs;
    }

    init();
}

})();

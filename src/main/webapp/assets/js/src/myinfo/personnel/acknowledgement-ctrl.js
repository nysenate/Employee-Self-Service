(function () {

angular.module('essMyInfo')
    .controller('AcknowledgementCtrl', ['$scope', '$q', 'appProps', 'modals', 'AckDocApi', 'AcknowledgementApi',
                                   acknowledgementCtrl]);

function acknowledgementCtrl($scope, $q, appProps, modals, documentApi, ackApi) {

    var initialState = {
        documents: {
            unsorted: [],
            acknowledged: [],
            unacknowledged: []
        },
        acknowledgements: {},

        request: {
            documents: false,
            ack: false
        }
    };

    function init() {
        $scope.state = angular.copy(initialState);
        $q.all([
            getDocuments(),
            getAcknowledgements()
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
        var acknowledgement = $scope.state.acknowledgements[doc.id];
        if (acknowledgement) {
            return acknowledgement.timestamp;
        } else {
            throw "No acknowledgement for doc " + doc.id + ": " + doc.title;
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
        console.log('hi');
        return documentApi.get({}, onSuccess, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.state.request.documents = false;
        });

        function onSuccess(resp) {
            $scope.state.documents.unsorted = resp.documents;
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

        $scope.state.request.ack = true;
        return ackApi.get(params, onSuccess, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.state.request.ack = false;
        });

        function onSuccess(resp) {
            angular.forEach(resp.acknowledgements, function (ack) {
                $scope.state.acknowledgements[ack.ackDocId] = ack;
            });
        }
    }

    function sortDocuments() {
        var documents = $scope.state.documents;
        documents.acknowledged = [];
        documents.unacknowledged = [];

        while (documents.unsorted.length > 0) {
            var doc = documents.unsorted.shift();
            if ($scope.state.acknowledgements[doc.id]) {
                documents.acknowledged.push(doc);
            } else {
                documents.unacknowledged.push(doc);
            }
        }
    }

    init();
}

})();

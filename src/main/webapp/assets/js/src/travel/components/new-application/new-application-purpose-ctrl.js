var essTravel = angular.module('essTravel');

essTravel.controller('NewApplicationPurposeCtrl', ['$scope', 'appProps', '$http', 'TravelApplicationByIdApi',
                                                      'TravelAttachmentDelete', purposeCtrl]);

function purposeCtrl($scope, appProps, $http, appApi, deleteAttachmentApi) {

    this.$onInit = function () {
        $scope.dirtyApp = angular.copy($scope.data.app);
    };

    $scope.next = function () {
        appApi.update({id: $scope.data.app.id}, {purposeOfTravel: $scope.dirtyApp.purposeOfTravel}, function (response) {
            $scope.data.app = response.result;
            $scope.nextState();
        }, $scope.handleErrorResponse)
    };

    /**
     * Attachment Code
     * TODO This is not currently used and probably needs fixes.
     **/

    var attachmentInput = angular.element("#addAttachment");
    attachmentInput.on('change', uploadAttachment);

    /**
     * This is the one place were a page directive directly updates the application.
     * This is because we need to upload the attachments while staying on the purpose page.
     */
    function uploadAttachment(event) {
        $scope.openLoadingModal();

        var files = attachmentInput[0].files;
        var formData = new FormData();
        for (var i = 0; i < files.length; i++) {
            formData.append("file", files[i]);
        }

        // Use $http instead of $resource because it can handle formData.
        $http.post(appProps.apiPath + '/travel/application/uncompleted/' + $scope.dirtyApp.id + '/attachment', formData, {
            // Allow $http to choose the correct 'content-type'.
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity
        }).then(function (response) {
            // Note, This creates a new local scope app, does not overwrite parent $scope.app.
            $scope.app = response.data.result;
        }).finally($scope.closeLoadingModal)
    }

    $scope.deleteAttachment = function (attachment) {
        deleteAttachmentApi.delete({id: $scope.app.id, attachmentId: attachment.id}, function (response) {
            console.log(response);
            $scope.app = response.result;
        })
    };
}

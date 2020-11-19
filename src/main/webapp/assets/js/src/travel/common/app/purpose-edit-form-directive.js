var essTravel = angular.module('essTravel');

essTravel.directive('essPurposeEditForm', ['appProps', 'TravelAttachmentDelete', purposeEditLink]);

function purposeEditLink(appProps, attachmentDeleteApi) {
    return {
        restrict: 'E',
        scope: {
            amendment: '<',         // The amendment being edited.
            title: '@',             // The title
            eventTypes: '<',         // Valid Purpose of Travel event types.
            positiveCallback: '&',  // Callback function called when continuing. Takes a travel app param named 'amendment'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a travel app param named 'amendment'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/purpose-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyAmendment = angular.copy(scope.amendment);

            scope.next = function () {
                scope.setInvalidFormElementsTouched(scope.purpose.form);
                if (scope.purpose.form.$valid) {
                    scope.positiveCallback({amendment: scope.dirtyAmendment});
                }
            };

            scope.cancel = function () {
                scope.negativeCallback({app: scope.dirtyAmendment});
            };

            /**
             * Attachment Code
             * TODO This is not currently used and probably needs fixes.
             **/

            // var attachmentInput = angular.element("#addAttachment");
            // attachmentInput.on('change', uploadAttachment);

            /**
             * This is the one place were a page directive directly updates the application.
             * This is because we need to upload the attachments while staying on the purpose page.
             */
            // function uploadAttachment(event) {
            //     $scope.openLoadingModal();
            //
            //     var files = attachmentInput[0].files;
            //     var formData = new FormData();
            //     for (var i = 0; i < files.length; i++) {
            //         formData.append("file", files[i]);
            //     }
            //
            //     // Use $http instead of $resource because it can handle formData.
            //     $http.post(appProps.apiPath + '/travel/application/uncompleted/' + $scope.dirtyAmendment.id + '/attachment', formData, {
            //         // Allow $http to choose the correct 'content-type'.
            //         headers: {'Content-Type': undefined},
            //         transformRequest: angular.identity
            //     }).then(function (response) {
            //         // Note, This creates a new local scope app, does not overwrite parent $scope.app.
            //         $scope.app = response.data.result;
            //     }).finally($scope.closeLoadingModal)
            // }
            //
            // $scope.deleteAttachment = function (attachment) {
            //     deleteAttachmentApi.delete({id: $scope.app.id, attachmentId: attachment.id}, function (response) {
            //         console.log(response);
            //         $scope.app = response.result;
            //     })
            // };
        }
    }
}

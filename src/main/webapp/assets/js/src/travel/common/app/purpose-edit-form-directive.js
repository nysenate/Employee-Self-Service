var essTravel = angular.module('essTravel');

essTravel.directive('essPurposeEditForm', ['$http', 'appProps', 'TravelAttachmentDelete', 'modals', purposeEditLink]);

function purposeEditLink($http, appProps, attachmentDeleteApi, modals) {
    return {
        restrict: 'E',
        scope: {
            dto: '<',               // The TravelAppEditDto being edited.
            eventTypes: '<',        // Valid Purpose of Travel event types.
            positiveCallback: '&',  // Callback function called when continuing. Takes a travel app param named 'amendment'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a travel app param named 'amendment'.
            negativeLabel: '@'      // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/purpose-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.dirtyAmendment = angular.copy(scope.dto.amendment);

            scope.next = function () {
                scope.setInvalidFormElementsTouched(scope.purpose.form);
                if (scope.purpose.form.$valid) {
                    scope.dto.amendment = scope.dirtyAmendment
                    scope.positiveCallback({dto: scope.dto});
                }
            };

            scope.cancel = function () {
                scope.negativeCallback({app: scope.dirtyAmendment});
            };

            /**
             * Attachment code
             */
            var attachmentInput = angular.element("#addAttachment");
            attachmentInput.on('change', uploadAttachment);

            function uploadAttachment(event) {
                scope.openLoadingModal();

                var files = attachmentInput[0].files;
                var formData = new FormData();
                for (var i = 0; i < files.length; i++) {
                    formData.append("file", files[i]);
                }

                // Use $http instead of $resource because it can handle formData.
                $http.post(appProps.apiPath + '/travel/unsubmitted/attachment', formData, {
                    // Allow $http to choose the correct 'content-type'.
                    headers: {'Content-Type': undefined},
                    transformRequest: angular.identity
                }).then(function (response) {
                    console.log(response);
                    // Update dirtyApp attachments
                    scope.dirtyAmendment.attachments = response.data.result.amendment.attachments;
                }).catch(function (res) {
                    modals.open("document-upload-error")
                        .then(scope.closeLoadingModal);
                }).finally(scope.closeLoadingModal)
            }

            scope.deleteAttachment = function (attachment) {
                attachmentDeleteApi.delete({filename: attachment.filename}, function (response) {
                    scope.dirtyAmendment.attachments = response.result.amendment.attachments;
                })
            };
        }
    }
}

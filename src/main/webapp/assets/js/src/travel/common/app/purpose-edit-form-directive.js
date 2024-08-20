var essTravel = angular.module('essTravel');

essTravel.directive('essPurposeEditForm', ['$http', 'appProps', 'TravelEventTypesApi',
                                           'AllowedTravelersApi', 'modals', purposeEditLink]);

function purposeEditLink($http, appProps, eventTypesApi, allowedTravelersApi, modals) {
    return {
        restrict: 'E',
        scope: {
            data: '<',               // The TravelAppEditDto being edited.
            positiveCallback: '&',  // Callback function called when continuing. Takes a draft param named 'draft'.
            negativeCallback: '&',  // Callback function called when canceling. Takes a draft param named 'draft'.
            negativeLabel: '@',     // Text to label the negative button. Defaults to 'Cancel'
        },
        controller: 'AppEditCtrl',
        templateUrl: appProps.ctxPath + '/template/travel/common/app/purpose-edit-form-directive',
        link: function (scope, elem, attrs) {

            scope.mode = scope.data.mode;
            scope.isLoading = true;
            scope.dirtyDraft = angular.copy(scope.data.draft);
            scope.eventTypes = [];

            (function () {
                // Init event types and allowed travelers.
                var eventTypesPromise = eventTypesApi.get().$promise.then(function (res) {
                    scope.eventTypes = res.result;
                });

                Promise.all([eventTypesPromise]).then(function () {
                    scope.isLoading = false;
                    scope.$apply();
                });
            })();

            scope.next = function () {
                scope.setInvalidFormElementsTouched(scope.purpose.form);
                if (scope.purpose.form.$valid) {
                    scope.positiveCallback({draft: scope.dirtyDraft});
                }
            };

            scope.save = function () {
                scope.setInvalidFormElementsTouched(scope.purpose.form);
                if (scope.purpose.form.$valid) {
                    scope.saveDraft(scope.dirtyDraft)
                        .then(function (draft) {
                            scope.dirtyDraft = draft;
                        })
                } else {
                    scope.purpose.form.$submitted = true;
                }
            }

            scope.cancel = function () {
                scope.negativeCallback({draft: scope.dirtyDraft});
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

                // Use $http because $resource can't handle formData.
                $http.post(appProps.apiPath + '/travel/drafts/attachment', formData, {
                    // Allow $http to choose the correct 'content-type'.
                    headers: {'Content-Type': undefined},
                    transformRequest: angular.identity
                }).then(function (response) {
                    console.log(response);
                    // Update dirtyApp attachments
                    scope.dirtyDraft.amendment.attachments = scope.dirtyDraft.amendment.attachments.concat(response.data.result);
                    // scope.dirtyAmendment.attachments = response.data.result.amendment.attachments;
                }).catch(function (res) {
                    modals.open("document-upload-error")
                        .then(scope.closeLoadingModal);
                }).finally(scope.closeLoadingModal)
            }

            scope.deleteAttachment = function (attachment) {
                var attachments = scope.dirtyDraft.amendment.attachments
                for (var i = 0; i < attachments.length; i++) {
                    var attch = attachments[i];
                    if (attch.filename === attachment.filename) {
                        attachments.splice(i, 1);
                    }
                }
            };

            scope.showDepartmentHead = function () {
                return !scope.dirtyDraft.traveler.isDepartmentHead
                    && scope.dirtyDraft.traveler.department.head != null;
            }
        }
    }
}

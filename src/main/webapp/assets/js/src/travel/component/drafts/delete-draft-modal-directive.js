angular.module('essTravel')
    .directive('deleteDraftModal', ['appProps', 'modals', deleteDraftModalDirective]);

function deleteDraftModalDirective(appProps, modals) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/component/drafts/delete-draft-modal',
        link: function ($scope, $elem, $attrs) {
            $scope.draft = modals.params().draft;

            $scope.onDelete = modals.resolve;
            $scope.onCancel = modals.reject;
        }
    };
}

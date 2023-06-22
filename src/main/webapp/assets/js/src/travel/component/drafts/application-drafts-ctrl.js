essTravel.controller('DraftsCtrl',
                     ['$scope', '$window', 'appProps', 'modals', 'LocationService', 'TravelDraftsApi',
                      'TravelDraftByIdApi', draftsController]);

function draftsController($scope, $window, appProps, modals, locationService, draftsApi, draftByIdApi) {

    var vm = this;
    vm.drafts = [];
    vm.draftsRequest = {};

    function init() {
        vm.drafts = [];
        vm.draftsRequest = draftsApi.get();
        vm.draftsRequest.$promise
            .then(function (res) {
                vm.drafts = res.result;
            })
            .catch($scope.handleErrorResponse)
    }

    vm.onRowClick = function (draft) {
        locationService.go("/travel/application/new/" + draft.id);
    };

    vm.onDelete = function (draft) {
        modals.open("draft-delete-confirmation", {draft: draft})
            .then(function () {
                draftByIdApi.delete({id: draft.id}).$promise
                    .then(function () {
                        init();
                    });
            });
    }

    init();
}

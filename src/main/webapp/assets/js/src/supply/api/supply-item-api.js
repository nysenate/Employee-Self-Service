angular.module('essSupply').factory('SupplyItemApi', [
    '$resource', 'appProps', 'modals', 'RestErrorService',
    function ($resource, appProps, modals, RestErrorService) {

        var itemsApi = $resource(appProps.apiPath + '/supply/items.json');
        var itemApi = $resource(appProps.apiPath + '/supply/items/:itemId.json', {itemId: '@itemId'});
        var itemsForLocApi = $resource(appProps.apiPath + '/supply/items/orderable/:locId.json', {locId: '@locId'});

        function item(itemId) {
            return itemApi.get({itemId: itemId}).$promise
                .then(returnResult)
                .catch(RestErrorService.handleErrorResponse)
        }

        function items() {
            return itemsApi.get().$promise
                .then(returnResult)
                .catch(RestErrorService.handleErrorResponse)
        }

        function itemsForLoc(locId) {
            // TODO: if locId is null/undefined?
            return itemsForLocApi.get({locId: locId}).$promise
                .then(returnResult)
                .catch(RestErrorService.handleErrorResponse);
        }

        /** --- Private functions --- */

        function returnResult(response) {
            return response.result;
        }

        return {
            item: item,
            items: items,
            itemsForLoc: itemsForLoc
        }
    }]);

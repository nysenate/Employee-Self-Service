var essSupply = angular.module('essSupply');

essSupply.factory('SupplyItemApi',
                  ['$resource', 'appProps', 'modals', supplyItemApi]);

function supplyItemApi($resource, appProps, modals) {

    var itemsApi = $resource(appProps.apiPath + '/supply/items.json');
    var itemApi = $resource(appProps.apiPath + '/supply/items/:itemId.json', {itemId: '@itemId'});
    var itemsForLocApi = $resource(appProps.apiPath + '/supply/items/orderable/:locId.json', {locId: '@locId'});

    function item(itemId) {
        return itemApi.get({itemId: itemId}).$promise
            .then(returnResult)
            .catch(apiError)
    }

    function items() {
        return itemsApi.get().$promise
            .then(returnResult)
            .catch(apiError)
    }

    function itemsForLoc(locId) {
        // TODO: if locId is null/undefined?
        return itemsForLocApi.get({locId: locId}).$promise
            .then(returnResult)
            .catch(apiError);
    }

    /** --- Private functions --- */

    function returnResult(response) {
        return response.result;
    }

    function apiError(response) {
        modals.open('500', {action: 'get valid order destinations', details: response})
    }

    return {
        item: item,
        items: items,
        itemsForLoc: itemsForLoc
    }
}

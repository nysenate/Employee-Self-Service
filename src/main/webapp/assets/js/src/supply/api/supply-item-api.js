var essSupply = angular.module('essSupply');

essSupply.factory('SupplyItemApi',
                  ['$resource', 'appProps', 'modals', supplyItemApi]);

function supplyItemApi($resource, appProps, modals) {

    var itemsApi = $resource(appProps.apiPath + '/supply/items.json');
    var itemsForLocApi = $resource(appProps.apiPath + '/supply/items/:locId.json', {locId: '@locId'});

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
        items: items,
        itemsForLoc: itemsForLoc
    }
}

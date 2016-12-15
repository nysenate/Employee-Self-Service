var essSupply = angular.module('essSupply');

essSupply.factory('SupplyItemApi',
                  ['$resource', 'appProps', 'modals', supplyItemApi]);

function supplyItemApi($resource, appProps, modals) {

    var itemsApi = $resource(appProps.apiPath + '/supply/items.json');
    var itemsForLocApi = $resource(appProps.apiPath + '/supply/items/:locId.json', {locId: '@locId'});

    function items() {
        return itemsApi.get().$promise
            .then(function (response) {
                return response.result;
            }).catch(modals.open('500', {action: 'get valid order destinations', details: response}))
    }

    function itemsForLoc(locId) {
        // TODO: if locId is null/undefined?
        return itemsForLocApi.get({locId: locId}).$promise
            .then(function (response) {
                return response.result;
            }).catch(function (response) {
                modals.open('500', {action: 'get valid order destinations', details: response})
            });
    }

    return {
        items: items,
        itemsForLoc: itemsForLoc
    }
}

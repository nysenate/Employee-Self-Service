angular.module('essTravel').factory('DelegationApi', [
    '$resource', 'appProps', 'RestErrorService',
    function ($resource, appProps, restErrorService) {

        var delegationPath = '/travel/delegation';
        var saveDelegationsApi = $resource(appProps.apiPath + delegationPath,
                                           {},
                                           {save: {method: 'POST', cancellable: true}});
        var searchDelegationsApi = $resource(appProps.apiPath + delegationPath);

        var delegationApi = {};

        delegationApi.saveDelegations = function (delegations) {
            return saveDelegationsApi.save({}, delegations);
        };

        delegationApi.findDelegationsByPrincipalId = function (principalId) {
            return searchDelegationsApi.get({principalId: principalId});
        };

        return delegationApi;
    }
]);

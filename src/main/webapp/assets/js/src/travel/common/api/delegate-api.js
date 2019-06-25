angular.module('essTravel').factory('DelegateApi', [
    '$resource', 'appProps', 'RestErrorService',
    function ($resource, appProps, restErrorService) {

        var saveDelegatesApi = $resource(appProps.apiPath + '/travel/delegate',
                                         {},
                                         {save: {method: 'POST', cancellable: true}});
        var searchDelegatesApi = $resource(appProps.apiPath + '/travel/delegate');

        var delegateApi = {};

        delegateApi.saveDelegates = function (delegates) {
            return saveDelegatesApi.save({}, delegates);
        };

        delegateApi.findDelegatesByPrincipalId = function (principalId) {
            return searchDelegatesApi.get({principalId: principalId});
        };

        return delegateApi;
    }
]);
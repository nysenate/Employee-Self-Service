/**
 * Fetches travel roles for the logged in user
 */
angular.module('essTravel').factory('TravelRoleService', [
    '$resource', 'appProps',
    function ($resource, appProps) {

        var userRoleApi = $resource(appProps.apiPath + '/travel/roles/:empId',
                                    {empId: '@empId'});

        var userRoles = [];
        var rolesFetched = false;

        var roleService = {};

        /**
         * Fetches user roles from the API if this is the first request.
         * On subsequent requests return the saved roles.
         * @return {Array}
         */
        roleService.roles = function () {
            if (rolesFetched) {
                return userRoles;
            }
            else {
                userRoleApi.get({empId: appProps.user.employeeId}).$promise
                    .then(function (response) {
                        userRoles = response.result;
                        rolesFetched = true;
                    })
            }
        };

        return roleService;
    }
]);

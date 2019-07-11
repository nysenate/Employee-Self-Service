/**
 * Fetches travel roles for the logged in user
 */
angular.module('essTravel').factory('TravelRoleService', [
    '$q', '$resource', 'appProps',
    function ($q, $resource, appProps) {

        var userRoleApi = $resource(appProps.apiPath + '/travel/roles/:empId',
                                    {empId: '@empId'});

        var userRoles = [];
        var rolesFetched = false;

        var roleService = {};

        /**
         * Fetches user roles from the API if this is the first request.
         * On subsequent requests return the saved roles.
         * @return Promise containing roles
         */
        roleService.roles = function () {
            return $q(function (resolve, reject) {
                if (rolesFetched) {
                    resolve(userRoles);
                } else {
                    userRoleApi.get({empId: appProps.user.employeeId}).$promise
                        .then(function (response) {
                            userRoles = response.result;
                            rolesFetched = true;
                            resolve(userRoles);
                        })
                }
            });

        };

        return roleService;
    }
]);

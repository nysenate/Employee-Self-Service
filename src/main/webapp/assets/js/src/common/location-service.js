angular.module('ess')
    .service('LocationService', ['$location', '$window', '$anchorScroll', '$http', '$route',
                                 'appProps', locationService]);

/**
 * A collection of utility functions that utilize $location
 */
function locationService($location, $window, $anchorScroll, $http, $route, appProps) {

    return {
        setSearchParam: setSearchParam,
        getSearchParam: getSearchParam,
        clearSearchParams: clearSearchParams,
        scrollToId: scrollToId,
        go: go,
        logout: logout
    };

    /**
     * Sets the search param with 'paramName' to 'paramValue'
     * @param paramName - name of param to set
     * @param paramValue - new value for the param
     * @param condition - the param will be set to null if this is exactly false
     * @param replace - the new url will replace the previous history entry unless this is exactly false
     */
    function setSearchParam(paramName, paramValue, condition, replace) {
        var search = $location.search(paramName, (condition !== false) ? paramValue : null);
        if (replace !== false) {
            search.replace();
        }
    }

    /**
     * Gets the search param by the given name if it exists
     */
    function getSearchParam(paramName) {
        return $location.search()[paramName];
    }

    /**
     * Clears all search params
     */
    function clearSearchParams() {
        $location.search({});
    }

    /**
     * Scrolls to the element with the given id
     */
    function scrollToId(id) {
        $location.hash(id);
        $anchorScroll();
    }

    /**
     * Goes to the specified path.
     */
    function go(path, reload, params) {
        console.log('navigating to', path, 'params=', params);
        $location.path(appProps.ctxPath + path).search((params) ? params : {});
        if (reload === true) {
            // Timeout is required for firefox to reload properly.
            setTimeout(function () {
                $window.location.reload()
            }, 0);
        }
    }

    /**
     * Logs the user out, with the option of saving the current url on re-login
     * @param saveLocation
     */
    function logout(saveLocation) {
        var logoutUrl = appProps.ctxPath + '/logout';
        if (saveLocation) {
            $http({
                method: 'HEAD',
                url: logoutUrl
            }).finally(function () {
                go($location.path(), true, $location.search());
            });
        } else {
            go(logoutUrl, true);
        }
    }

}
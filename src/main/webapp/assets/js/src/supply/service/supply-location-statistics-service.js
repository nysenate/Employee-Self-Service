var essSupply = angular.module('essSupply');

essSupply.service('SupplyLocationStatisticsService',
    ['$q', 'SupplyLocationStatisticsApi', supplyLocationStatisticsService]);

/**
 * Service for getting location statistics.
 * Remembers the last API call made.
 */
function supplyLocationStatisticsService($q, locationStatisticsApi) {

    /**
     * Contains location statistic information
     */
    function LocationStatistics(locationStatistcsMap) {
        this.locationStatisticsMap = locationStatistcsMap;

        /**
         * Get the total quantity of an item ordered at a given location.
         */
        this.getQuantityForLocationAndItem = function (location, item) {
            if (locationStatistcsMap[location])
                return locationStatistcsMap[location].itemQuantities[item];
        }
    }

    return {
        /**
         * Returns a promise containing a LocationStatistic object
         */
        calculateLocationStatisticsFor: function (year, month) {
            return $q(function (resolve, reject) {
                var params = {
                    year: year,
                    month: month
                };
                locationStatisticsApi.get(params, function (response) {
                    resolve(new LocationStatistics(response.result.items));
                })
            });
        }
    }
}

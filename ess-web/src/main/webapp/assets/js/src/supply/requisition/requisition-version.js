essSupply = angular.module('essSupply').factory('RequisitionHistory', [function () {

    function Version(modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        this.shipment = undefined;
        this.order = undefined;
    }

    Version.prototype.add = function (ver) {
        if (!this.shipment) {
            this.shipment = ver.shipment;
        }
        if (!this.order) {
            this.order = ver.order;
        }
    };

    /**
     * Order and shipment versions should be considered to have happened at the same
     * time if they occurred within VERSION_DATE_TIME_DELTA milliseconds of each other.
     * @type {number}
     */
    var VERSION_DATE_TIME_DELTA = 250;

    
    function RequisitionHistory(shipment) {
        this.versions = [];

        var orderDates = getDates(shipment.order.history.items);
        var shipmentDates = getDates(shipment.history.items);

        // add the first version
        var firstVersion = new Version(orderDates[0]);
        firstVersion.order = shipment.order.history.items[orderDates[0]];
        firstVersion.shipment = shipment.history.items[shipmentDates[0]];
        this.versions.push(firstVersion);

        // Remove first version dates to avoid edge cases, the first shipment and order versions
        // always occurred at the same time.
        orderDates.shift();
        shipmentDates.shift();

        // Add all Order Versions
        for (var i = 0; i < orderDates.length; i++) {
            var version = new Version(orderDates[i]);
            version.order = shipment.order.history.items[orderDates[i]];
            addVersion(this.versions, version);
        }

        // Add shipment version info
        for (var j = 0; j < shipmentDates.length; j++) {
            var ver = new Version(shipmentDates[j]);
            ver.shipment = shipment.history.items[shipmentDates[j]];
            addVersion(this.versions, ver);
        }
    }
    
    function getDates(version) {
        var dates = [];
        for (var key in version) {
            if (version.hasOwnProperty(key)) {
                dates.push(key);
            }
        }
        return dates;
    }

    function addVersion(versions, newVersion) {
        var matchingVersion = undefined;
        versions.forEach(function (version) {
            if (Math.abs(new Date(version.modifiedDateTime) - new Date(newVersion.modifiedDateTime)) < VERSION_DATE_TIME_DELTA) {
                matchingVersion = version;
            }
        });

        if (!matchingVersion) {
            versions.push(newVersion);
        }
        else {
            matchingVersion.add(newVersion);
        }
    }

    return RequisitionHistory;
}]);

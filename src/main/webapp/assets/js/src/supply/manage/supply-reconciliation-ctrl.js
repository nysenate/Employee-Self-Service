essSupply = angular.module('essSupply').controller('SupplyReconciliationController',
    ['$scope', 'SupplyRequisitionApi', 'SupplyReconciliationApi', 'LocationService', 'SupplyUtils', 'modals', '$window', '$timeout', supplyReconciliationController]);


function supplyReconciliationController($scope, requisitionApi, supplyReconciliationApi, locationService, supplyUtils, modals, $window, $timeout) {

    /** If a particular item is selected, displays information on all orders containing that item. */
    $scope.selectedItem = null;

    $scope.viewItems = [];
    $scope.reconcilableSearch = {
        matches: [],
        items: [],
        response: {},
        error: false,
        quantity: 0
    };
    $scope.currentPage = 1;


    /** Map of item id's to shipments containing that item. */
    $scope.reconcilableItemMap= {};


    /** Print*/
    $scope.print = function () {
        $timeout($window.print, 0);
    };

    function initItems() {
        // Get shipments approved today
        var params = {
            status: "APPROVED",
            from: moment().startOf('day').format(),
            to: moment().format(),
            dateField: "approved_date_time",
            reconciled: 'false',
            offset: 0,
            limit: 'ALL'
        };
        $scope.reconcilableSearch.response = requisitionApi.get(params, function(response) {
            $scope.reconcilableSearch.matches = response.result;
            $scope.reconcilableSearch.error = false;
            angular.forEach($scope.reconcilableSearch.matches, function(shipment) {
                angular.forEach(shipment.lineItems, function(lineItem) {
                    if ($scope.reconcilableItemMap.hasOwnProperty(lineItem.item.id)) {
                        $scope.reconcilableItemMap[lineItem.item.id].push(shipment);
                    }
                    else {
                        $scope.reconcilableItemMap[lineItem.item.id] = [];
                        $scope.reconcilableItemMap[lineItem.item.id].push(shipment);
                        lineItem.item.newQuantity =0;
                        $scope.reconcilableSearch.items.push(lineItem.item);
                    }
                })
            });
            $scope.reconcilableSearch.items = supplyUtils.alphabetizeItemsByCommodityCode($scope.reconcilableSearch.items);
        }, function(response) {
            $scope.reconcilableSearch.matches = [];
            $scope.reconcilableSearch.items = [];
            $scope.reconcilableSearch.error = true;
            $scope.handleErrorResponse(response);
        });

    }

    /**
     * Selecting an item will display a table containing information for all shipments that contain that item.
     * Clicking on an already selected item will hide the table.
     */
    $scope.setSelected = function(item) {
        // Clicking an expanded item should collapse it.
        if($scope.selectedItem == item) {
            $scope.selectedItem = null;
        }
        else {
            $scope.selectedItem = item;
        }
    };

    $scope.isItemSelected = function(item) {
        return $scope.selectedItem == item;
    };

    $scope.getShipmentsWithItem = function(item) {
        return $scope.reconcilableItemMap[item.id];
    };

    $scope.getOrderedQuantity = function(shipment, item) {
        var lineItems = shipment.lineItems;
        for(var i = 0; i < lineItems.length; i++) {
            if (lineItems[i].item.id === item.id) {
                return lineItems[i].quantity;
            }
        }
    };

    $scope.viewShipment = function(shipment){
        locationService.go("/supply/requisition/requisition-view", false, "requisition=" + shipment.requisitionId + "&fromPage=reconciliation");
    };

    $scope.init = function() {
        initItems();
    };

    $scope.setCurrentPage = function (page) {
        $scope.currentPage = page;
    };



    $scope.reconcile = function(){
        $scope.recOrder=[];
        for(var i =0; i < $scope.reconcilableSearch.items.length; i++){
            var order = {
                id: 0,
                quantity: 0
            };
            order.id = $scope.reconcilableSearch.items[i].id;
            order.quantity = $scope.reconcilableSearch.items[i].newQuantity;
            $scope.recOrder[i] = order;
        }

        //console.log($scope.reconcilableSearch.items);
        //console.log($scope.recOrder);



    }

    $scope.init();



}

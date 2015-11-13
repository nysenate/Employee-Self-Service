var essSupply = angular.module('essSupply');

essSupply.service('SupplyInventoryService', [function() {

    // TODO will eventually get from api dependency.
    // Canonical source of available products.
    var products = [
            {
                id: 1,
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0239035_sc7?$splssku$",
                name: "Pencils",
                description: "Number 2 yellow pencils",
                unitSize: 24,
                categoryId: 1,
                categoryName: 'Pencils',
                warnQuantity: 2,
                maxQuantity: 4
            },
            {
                id: 2,
                img: "http://www.staples-3p.com/s7/is/image/Staples/m002303302_sc7?$splssku$",
                name: "Mechanical Pencils",
                description: "0.7mm mechanical pencils",
                unitSize: 12,
                categoryId: 1,
                categoryName: 'Pencils',
                warnQuantity: 2,
                maxQuantity: 4
            },
            {
                id: 3,
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0381386_sc7?$std$",
                name: "Index Cards",
                description: "3x5 Lined Index Cards",
                unitSize: 100,
                categoryId: 3,
                categoryName: 'Index Cards',
                warnQuantity: 2,
                maxQuantity: 4
            },
            {
                id: 4,
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0240366_sc7?$std$",
                name: "Index Cards",
                description: "4x6 Lined Index Cards",
                unitSize: 100,
                categoryId: 3,
                categoryName: 'Index Cards',
                warnQuantity: 2,
                maxQuantity: 4
            },
            {
                id: 5,
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0240368_sc7?$std$",
                name: "Index Cards",
                description: "5x8 Lined Index Cards",
                unitSize: 100,
                categoryId: 3,
                categoryName: 'Index Cards',
                warnQuantity: 2,
                maxQuantity: 4
            },
            {
                id: 6,
                img: "http://www.staples-3p.com/s7/is/image/Staples/m002304304_sc7?$std$",
                name: "Blue Ballpoint Pens",
                description: "Blue ink, bold point",
                unitSize: 12,
                categoryId: 2,
                categoryName: 'Pens',
                warnQuantity: 2,
                maxQuantity: 4
            },
            {
                id: 7,
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0903749_sc7?$std$",
                name: "Black Ballpoint Pens",
                description: "Black ink, medium point",
                unitSize: 12,
                categoryId: 2,
                categoryName: 'Pens',
                warnQuantity: 2,
                maxQuantity: 4
            },
            {
                id: 8,
                img: "http://www.staples-3p.com/s7/is/image/Staples/m002304307_sc7?$std$",
                name: "Red Ballpoint Pens",
                description: "Red ink, fine point",
                unitSize: 12,
                categoryId: 2,
                categoryName: 'Pens',
                warnQuantity: 2,
                maxQuantity: 4
            },
            {
                id: 8,
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0630083_sc7?$std$",
                name: "Paper Clips",
                description: "Paper clips, smooth, jumbo size",
                unitSize: 100,
                categoryId: 4,
                categoryName: 'Clips',
                warnQuantity: 11,
                maxQuantity: 20
            },
            {
                id: 9,
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0165682_sc7?$std$",
                name: "Paper Clips",
                description: "Paper Clips, smooth, small",
                unitSize: 12,
                categoryId: 4,
                categoryName: 'Clips',
                warnQuantity: 11,
                maxQuantity: 20
            },
            {
                id: 10,
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0165669_sc7?$std$",
                name: "Binder Clips",
                description: "Binder Clips 3/4\"",
                unitSize: 12,
                categoryId: 4,
                categoryName: 'Clips',
                warnQuantity: 4,
                maxQuantity: 8
            },
            {
                id: 11,
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0165672_sc7?$std$",
                name: "Binder Clips",
                description: "Binder Clips 2\"",
                unitSize: 12,
                categoryId: 4,
                categoryName: 'Clips',
                warnQuantity: 4,
                maxQuantity: 8
            }
        ];

    function initInventory() {
        // TODO: will call api.
    }

    return {
        /** Return copy of products so this.products never gets altered by a 3rd party. */
        getCopyOfProducts: function() {
            if (products.length === 0) {
                initInventory();
            }
            return angular.copy(products);
        },

        /** Return an array of all int's from 1 to product's max quantity.
         * This represents allowable order quantities. */
        orderQuantityRange: function(product) {
            return Array.apply(null, Array(product.maxQuantity)).map(function (_, i) {return i + 1;})
        },

        /** Returns true if a given quantitiy is above the products warn quantity.
         * Signifies that the quantity is above the recommended order size and may require approval. */
        isWarningQuantity: function(product, quantity) {
            return typeof quantity !== 'undefined' && quantity >= product.warnQuantity;
        }
    }
}]);

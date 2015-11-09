var essSupply = angular.module('essSupply').controller('SupplyOrderController',
    ['$scope', 'appProps', 'modals', 'supplyCart', supplyOrderController]);

function supplyOrderController($scope, appProps, modals, supplyCart) {

    $scope.categories = [
        {
            id: 1,
            categoryName: "Pencils",
            selected: false
        },
        {
            id: 2,
            categoryName: "Pens",
            selected: false
        },
        {
            id: 3,
            categoryName: "Index Cards",
            selected: false
        },
        {
            id: 4,
            categoryName: "Clips",
            selected: false
        }
    ];

    var productsModel =
        [
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0239035_sc7?$splssku$",
                name: "Pencils",
                description: "Number 2 yellow pencils",
                unitSize: 24,
                categoryId: 1,
                warnQuantity: 2,
                maxQuantity: 4,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/m002303302_sc7?$splssku$",
                name: "Mechanical Pencils",
                description: "0.7mm mechanical pencils",
                unitSize: 12,
                categoryId: 1,
                warnQuantity: 2,
                maxQuantity: 4,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0381386_sc7?$std$",
                name: "Index Cards",
                description: "3x5 Lined Index Cards",
                unitSize: 100,
                categoryId: 3,
                warnQuantity: 2,
                maxQuantity: 4,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0240366_sc7?$std$",
                name: "Index Cards",
                description: "4x6 Lined Index Cards",
                unitSize: 100,
                categoryId: 3,
                warnQuantity: 2,
                maxQuantity: 4,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0240368_sc7?$std$",
                name: "Index Cards",
                description: "5x8 Lined Index Cards",
                unitSize: 100,
                categoryId: 3,
                warnQuantity: 2,
                maxQuantity: 4,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/m002304304_sc7?$std$",
                name: "Blue Ballpoint Pens",
                description: "Blue ink, bold point",
                unitSize: 12,
                categoryId: 2,
                warnQuantity: 2,
                maxQuantity: 4,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0903749_sc7?$std$",
                name: "Black Ballpoint Pens",
                description: "Black ink, medium point",
                unitSize: 12,
                categoryId: 2,
                warnQuantity: 2,
                maxQuantity: 4,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/m002304307_sc7?$std$",
                name: "Red Ballpoint Pens",
                description: "Red ink, fine point",
                unitSize: 12,
                categoryId: 2,
                warnQuantity: 2,
                maxQuantity: 4,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0630083_sc7?$std$",
                name: "Paper Clips",
                description: "Paper clips, smooth, jumbo size",
                unitSize: 100,
                categoryId: 4,
                warnQuantity: 11,
                maxQuantity: 20,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0165682_sc7?$std$",
                name: "Paper Clips",
                description: "Paper Clips, smooth, small",
                unitSize: 12,
                categoryId: 4,
                warnQuantity: 11,
                maxQuantity: 20,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0165669_sc7?$std$",
                name: "Binder Clips",
                description: "Binder Clips 3/4\"",
                unitSize: 12,
                categoryId: 4,
                warnQuantity: 4,
                maxQuantity: 8,
                inCart: false
            },
            {
                img: "http://www.staples-3p.com/s7/is/image/Staples/s0165672_sc7?$std$",
                name: "Binder Clips",
                description: "Binder Clips 2\"",
                unitSize: 12,
                categoryId: 4,
                warnQuantity: 4,
                maxQuantity: 8,
                inCart: false
            }
    ];

    $scope.quantity = 1;

    /** ----- Controller Logic ----- */

    $scope.init = function() {
        $scope.products = angular.extend({}, productsModel);
    };

    $scope.addToCart = function(product, qty) {
        supplyCart.addToCart(product, qty);
        product.inCart = true;
    };

    /** Return an array with range from 1 to products max quantity */
    $scope.orderSizeRange = function(product) {
        return Array.apply(null, Array(product.maxQuantity)).map(function (_, i) {return i + 1;})
    };

    /**
     * Called whenever a category is selected/deselected.
     * Updates displayed products to only display products belonging to selected category.
     * If no categories are selected, display all products.
     */
    $scope.filterByCategories = function() {
        $scope.products = [];
        angular.forEach($scope.categories, function(category) {
            if (category.selected === true) {
                angular.forEach(productsModel, function(product) {
                    if (category.id === product.categoryId) {
                        $scope.products.push(product);
                    }
                });
            }
        });
        // If no category selected, display all.
        if ($scope.products.length === 0) {
            $scope.products = angular.extend({}, productsModel);
        }
    };

    $scope.init();
}

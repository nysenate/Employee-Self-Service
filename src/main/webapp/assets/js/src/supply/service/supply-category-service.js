var essSupply = angular.module('essSupply');

/**
 * The SupplyCategoryService contains all distinct categories of items
 * that are available to be ordered in the supply app.
 *
 * When filtering the order page by categories, any category selected
 * on the navigation will have its 'selected' field set to true.
 *
 * Users of this service should call updateUrlParam after making changes
 * directly to a category so the url search param can be updated accordingly.
 */
essSupply.factory('SupplyCategoryService',
                  ['SupplyItemApi', 'SupplyOrderDestinationService', 'LocationService', supplyCategoryService]);

function supplyCategoryService(itemApi, destinationService, locationService) {

    function Category(name) {
        this.name = name;
        this.selected = false;
    }

    var categories = [];

    /** --- Public Methods --- */

    function getCategories() {
        if (categories === null || categories.length === 0) {
            initCategories();
        }
        return categories;
    }

    function getSelectedCategories() {
        var selected = [];
        angular.forEach(getCategories(), function (cat) {
            if (cat.selected === true) {
                selected.push(cat);
            }
        });
        return selected;
    }

    function getSelectedCategoryNames() {
        var names = [];
        getSelectedCategories().forEach(function (category) {
            names.push(category.name)
        });
        return names;
    }

    function setSelectedCategories(names) {
        getCategories().forEach(function (category) {
            category.selected = names.indexOf(category.name) !== -1;
        });
        updateUrlParam();
    }

    function clearSelections() {
        setSelectedCategories("");
        updateUrlParam();
    }

    function updateUrlParam() {
        locationService.setSearchParam("category", getSelectedCategoryNames(), true, false);
    }

    /** --- Private Methods --- */

    function initCategories() {
        // Only init if destination has been selected.
        if (!destinationService.isDestinationConfirmed()) {
            return;
        }
        itemApi.itemsForLoc(destinationService.getDestination().locId)
            .then(function (items) {
                // Create categories
                angular.forEach(items, function (item) {
                    if (isDistinctCategory(item.category)) {
                        categories.push(new Category(item.category));
                    }
                });
                // Alphabetize the categories.
                categories.sort(function (a, b) {
                    if (a.name < b.name) {
                        return -1;
                    }
                    if (a.name > b.name) {
                        return 1;
                    }
                    return 0;
                });

                // Initialize any categories set in url
                setSelectedCategories(locationService.getSearchParam("category") || []);
            });
    }

    /** Returns true if a category is not yet in the category array, false otherwise. */
    function isDistinctCategory(name) {
        var cats = $.grep(categories, function (cat) {
            return cat.name === name;
        });
        return !cats.length > 0;
    }

    return {
        getCategories: getCategories,
        getSelectedCategories: getSelectedCategories,
        getSelectedCategoryNames: getSelectedCategoryNames,
        setSelectedCategories: setSelectedCategories,
        clearSelections: clearSelections,
        updateUrlParam: updateUrlParam
    }
}

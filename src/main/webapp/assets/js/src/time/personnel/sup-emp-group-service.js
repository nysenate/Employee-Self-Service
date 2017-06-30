angular.module('essTime')
    .factory('supEmpGroupService',
             ['$filter', 'appProps', 'modals', 'SupervisorEmployeesApi', supEmpGroupService]);

/**
 * Provides utilities to retrieve and query
 * the logged in user's extended supervisor employee group
 *
 * @param $filter
 * @param appProps
 * @param modals
 * @param supEmployeeApi
 * @returns {{}}
 */
function supEmpGroupService($filter, appProps, modals, supEmployeeApi) {

    /** Stores extended supervisor employee group */
    var extendedSupEmpGroup = null;
    /** Stores a flat list of all supEmpGroups */
    var supEmpGroupList = [];
    /** A map of empId -> name for all employees in the extended sup emp group */
    var nameMap = {};
    /** A map of empId -> supId for all employees in the extended sup emp group */
    var supIdMap = {};

    /** A promise that is resolved when the exdended supervisor employee group is fully loaded */
    var empGroupPromise = null;

    init();

    return {
        init: init,
        getExtSupEmpGroup: getExtSupEmpGroup,
        getSupEmpGroupList: getSupEmpGroupList,
        getName: getName,
        getSupId: getSupId,
        getTier: getTier,
        getEmpInfos: getEmpInfos
    };

    /**
     * This function retrieves the user's extended supervisor employee group
     * and must finish before any other methods of the service are called.
     * @returns {*} a promise that is resolved once the service is initialized
     */
    function init() {
        if (!empGroupPromise) {
            empGroupPromise = loadSupEmpGroup();
        }
        return empGroupPromise;
    }

    /**
     * Get the extendedSupEmpGroup
     * @returns {*}
     */
    function getExtSupEmpGroup() {
        return extendedSupEmpGroup;
    }

    /**
     * Get a flat list of all sup emp groups in the extended sup emp group
     * @returns {Array}
     */
    function getSupEmpGroupList() {
        return supEmpGroupList;
    }

    /**
     * Return the name for the employee with the given empId if it exists
     * @param {Number} empId
     * @returns {Object} containing fields "firstName" and "lastName"
     */
    function getName(empId) {
        return nameMap[empId];
    }

    /**
     * Return the supervisor id for the employee with the given empId if it exists
     * @param {Number} empId
     * @returns {Object} containing fields "firstName" and "lastName"
     */
    function getSupId(empId) {
        return supIdMap[empId];
    }

    /**
     * Gets an employee's tier within the current sup emp group.
     * The tier number is the degree of separation between the employee
     * and the top level supervisor.
     * @param empId
     * @returns {number}
     */
    function getTier(empId) {
        var topSupId = extendedSupEmpGroup.supId;
        var visitedSupIds = {};
        visitedSupIds[topSupId] = true;

        var currentEmpId = empId;
        var tier = 0;

        while (currentEmpId && !visitedSupIds[currentEmpId]) {
            tier++;
            visitedSupIds[currentEmpId] = true;
            currentEmpId = getSupId(currentEmpId);
        }

        return tier;
    }

    /**
     * Return a list of employee info objects for the requested supEmp group
     * The returned list will contain all employees in the sup emp group
     *   ordered by primary employees, emp overrides, and then sup overrides
     * Senators will be excluded from the list if omitSenators is true
     * @param iSupEmpGroup
     * @param omitSenators
     * @returns []
     */
    function getEmpInfos(iSupEmpGroup, omitSenators) {
        if (iSupEmpGroup < 0 || iSupEmpGroup > supEmpGroupList.length) {
            throw "sup emp group index out of bounds: " + iSupEmpGroup;
        }
        var selEmpGroup = supEmpGroupList[iSupEmpGroup];

        var isUser = selEmpGroup.supId === extendedSupEmpGroup.supId;
        var empList = [];

        var primaryEmps = sortEmpInfos(selEmpGroup.primaryEmployees);

        // This lookup table maps empId -> last name in case it's needed for the supervisor overrides.
        // Add all the employees into a single collection to populate the drop down.
        angular.forEach(primaryEmps, function(emp) {
            empList.push(emp);
        });
        if (isUser) {
            angular.forEach(selEmpGroup.empOverrideEmployees, function (emp) {
                emp.empOverride = true;
                empList.push(emp);
            });
            angular.forEach(selEmpGroup.supOverrideEmployees.items, function (supGroup, supId) {
                angular.forEach(supGroup, function (emp) {
                    emp.supOverride = true;
                    empList.push(emp);
                });
            });
        }

        return empList.filter(function (emp) {
            return !(omitSenators && emp.senator);
        });
    }

    /* --- Internal Methods --- */

    /**
     * Request the user's sup emp group, returning a promise that resolves when it is loaded
     * @returns {*}
     */
    function loadSupEmpGroup () {
        var fromDateMoment = moment().subtract(2, 'years');
        var params = {
            supId: appProps.user.employeeId,
            fromDate: fromDateMoment.format('YYYY-MM-DD'),
            extended: true
        };

        function onSuccess (response) {
            extendedSupEmpGroup = response.result;
            setEmpMaps();
            setSupEmpGroups();
        }
        function onFail (response) {
            console.error('error retrieving sup emp group\nparams:', params, 'response:', response);
            modals.open('500', {details: response});
        }

        return supEmployeeApi.get(params, onSuccess, onFail).$promise;
    }

    /**
     * Stores all employee names in a map
     * Stores the supervisor of every employee in a map
     */
    function setEmpMaps() {
        // Get primary and override emp infos
        var primaryEmpInfos = extendedSupEmpGroup.primaryEmployees;
        var empOverrideInfos = extendedSupEmpGroup.empOverrideEmployees;
        var supOverrideInfos = Object.keys(extendedSupEmpGroup.supOverrideEmployees)
            .map(function (k) { return extendedSupEmpGroup.supOverrideEmployees[k] });

        var allEmpInfos = primaryEmpInfos.concat(empOverrideInfos).concat(supOverrideInfos);

        // Get indirect emp infos
        var empSupEmpGroupMap = extendedSupEmpGroup.employeeSupEmpGroups;
        angular.forEach(empSupEmpGroupMap, function (supEmpGroups) {
            angular.forEach(supEmpGroups, function (supEmpGroup) {
                allEmpInfos = allEmpInfos.concat(supEmpGroup.primaryEmployees);
            })
        });

        // Set emp info names in name map
        angular.forEach(allEmpInfos, function (empInfo) {
            nameMap[empInfo.empId] = {
                firstName: empInfo.empFirstName,
                lastName: empInfo.empLastName,
                fullName: empInfo.empFirstName + ' ' + empInfo.empLastName
            };

            supIdMap[empInfo.empId] = empInfo.supId;
        });

        nameMap[appProps.user.employeeId] = {
            firstName: appProps.user.firstName,
            lastName: appProps.user.lastName,
            fullName: appProps.user.firstName + ' ' + appProps.user.lastName
        };

    }

    /**
     * Generates a flattened list of all supervisor emp groups included in the extended supervisor emp group
     * List contains the user's sup emp group, followed by all others ordered by supervisor name
     */
    function setSupEmpGroups() {
        var empSupEmpGroups = [];

        angular.forEach(extendedSupEmpGroup.employeeSupEmpGroups, function (supEmpGroups) {
            angular.forEach(supEmpGroups, function (empGroup) {
                empGroup.supStartDate = empGroup.effectiveFrom;
                empGroup.supEndDate = empGroup.effectiveTo;
                empGroup.empFirstName = nameMap[empGroup.supId].firstName;
                empGroup.empLastName = nameMap[empGroup.supId].lastName;
                empSupEmpGroups.push(empGroup);
            });
        });

        empSupEmpGroups = $filter('orderBy')(empSupEmpGroups,
                [getSupEmpGroupTier, 'empLastName', 'empfirstName', 'supId', 'effectiveEndDate']);

        supEmpGroupList = [extendedSupEmpGroup].concat(empSupEmpGroups);
    }

    function getSupEmpGroupTier(supEmpGroup) {
        return getTier(supEmpGroup.supId);
    }

    /**
     * Sort a list of supervisor emp infos
     * Sort by last name, first name, empId, effective end date
     * @param empInfoList
     */
    function sortEmpInfos(empInfoList) {
        return $filter('orderBy')(empInfoList, ['empLastName', 'empFirstName', 'empId', 'effectiveEndDate']);
    }
}
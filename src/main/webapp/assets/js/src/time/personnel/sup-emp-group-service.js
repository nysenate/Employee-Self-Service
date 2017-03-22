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

    var extendedSupEmpGroup = null;
    var supEmpGroupList = [];
    var nameMap = {};
    var empGroupPromise = null;

    init();

    return {
        init: init,
        getExtSupEmpGroup: getExtSupEmpGroup,
        getSupEmpGroupList: getSupEmpGroupList,
        getName: getName,
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
     * Return a list of employee info objects for the requested supEmp group
     * The returned list will contain all employees in the sup emp group
     *   ordered by primary employees, emp overrides, and then sup overrides
     * @param iSupEmpGroup
     * @returns []
     */
    function getEmpInfos(iSupEmpGroup) {
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
            // todo remove
            // emp.group = 'Direct employees';
            // setAdditionalEmpData(emp);
            // todo /remove
            empList.push(emp);
        });
        if (isUser) {
            angular.forEach(selEmpGroup.empOverrideEmployees, function (emp) {
                // todo remove
                // emp.group = 'Override Employees';
                // setAdditionalEmpData(emp, true);
                // todo /remove
                emp.empOverride = true;
                empList.push(emp);
            });
            angular.forEach(selEmpGroup.supOverrideEmployees.items, function (supGroup, supId) {
                angular.forEach(supGroup, function (emp) {
                    // todo remove
                    // emp.group = nameMap[supId]
                    //     ? nameMap[supId].lastName + '\'s Employees'
                    //     : 'Sup Override Employees';
                    // setAdditionalEmpData(emp, true);
                    // todo /remove
                    emp.supOverride = true;
                    empList.push(emp);
                });
            });
        }

        return empList;
    }

    /* --- Internal Methods --- */

    /**
     * Request the user's sup emp group, returning a promise that resolves when it is loaded
     * @returns {*}
     */
    function loadSupEmpGroup () {
        var fromDateMoment = moment().subtract(2, 'years');
        var toDateMoment = moment();
        var params = {
            supId: appProps.user.employeeId,
            fromDate: fromDateMoment.format('YYYY-MM-DD'),
            toDate: toDateMoment.format('YYYY-MM-DD'),
            extended: true
        };

        function onSuccess (response) {
            extendedSupEmpGroup = response.result;
            setNameMap();
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
     */
    function setNameMap() {
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
                lastName: empInfo.empLastName
            };
        });

        nameMap[appProps.user.employeeId] = {
            firstName: appProps.user.firstName,
            lastName: appProps.user.lastName
        }
    }

    /**
     * Generates a flattened list of all supervisor emp groups included in the extended supervisor emp group
     * List contains the user's sup emp group, followed by all others ordered by supervisor name
     */
    function setSupEmpGroups() {
        var empSupEmpGroups = [];

        // todo remove
        // var supName = appProps.user.firstName + " " + appProps.user.lastName;
        // extendedSupEmpGroup.dropDownLabel = supName;
        // todo /remove

        angular.forEach(extendedSupEmpGroup.employeeSupEmpGroups, function (supEmpGroups) {
            angular.forEach(supEmpGroups, function (empGroup) {
                empGroup.supStartDate = empGroup.effectiveFrom;
                empGroup.supEndDate = empGroup.effectiveTo;
                empGroup.empFirstName = nameMap[empGroup.supId].firstName;
                empGroup.empLastName = nameMap[empGroup.supId].lastName;
                // todo remove
                // setAdditionalEmpData(empGroup);
                // empGroup.group = 'Supervisors Under ' + supName;
                // todo /remove
                empSupEmpGroups.push(empGroup);
            });
        });

        empSupEmpGroups = $filter('orderBy')(empSupEmpGroups,
                                             ['empLastName', 'empfirstName', 'supId', 'effectiveEndDate']);

        supEmpGroupList = [extendedSupEmpGroup].concat(empSupEmpGroups);
    }

    /**
     * Sort a list of supervisor emp infos
     * Sort by last name, first name, empId, effective end date
     * @param empInfoList
     */
    function sortEmpInfos(empInfoList) {
        return $filter('orderBy')(empInfoList, ['empLastName', 'empFirstName', 'empId', 'effectiveEndDate']);
    }

    // todo remove with dropdown directive
    // function setAdditionalEmpData(emp, override) {
    //     var startDate = override ? emp.effectiveStartDate : emp.supStartDate;
    //     var endDate = override ? emp.effectiveEndDate : emp.supEndDate;
    //
    //     var supStartMoment = moment(startDate || '1970-01-01');
    //     var supEndMoment = moment(endDate || '2999-12-31');
    //
    //     var name = emp.empLastName + ' ' + emp.empFirstName[0] + '.';
    //     var dateRange = supStartMoment.format('MMM YYYY') +
    //         (supStartMoment.isSame(supEndMoment, 'month')
    //                 ? '' // don't display two dates if they are the same month
    //                 : ' - ' + // Show 'present' as end date if the end date is today
    //          (supEndMoment.isBefore(moment(), 'day')
    //              ? supEndMoment.format('MMM YYYY') : 'Present')
    //         );
    //     emp.dropDownLabel = name + '(' + dateRange + ')';
    // }
}
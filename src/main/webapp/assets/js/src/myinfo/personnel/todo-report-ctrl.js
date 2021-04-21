(function () {
    angular.module('essMyInfo')
        .controller('TodoReportCtrl',
                    ['$scope', '$httpParamSerializer', 'TaskUtils', 'EmpPATSearchApi', 'PaginationModel', 'appProps', 'modals', 'UpdatePersonnelTaskAssignmentApi', todoCtrl]);

    function todoCtrl($scope, $httpParamSerializer, taskUtils, searchApi, pagination, appProps, modals, UpdatePersonnelTaskAssignmentApi) {

        var itemsPerPage = 10;

        var defaultPagination = angular.copy(pagination);
        defaultPagination.itemsPerPage = itemsPerPage;

        var defaultParams = {
            name: "",
            empActive: true,
            taskId: null,
            contServFrom: null,
            taskActive: true,
            completed: null,
            totalCompletion: null,
            respCtrHead: null,
            sort: []
        };

        $scope.contSrvDateValues = {
            any: {
                label: 'Any',
                getValue: function () {
                    return null;
                }
            },
            twoWeeks: {
                label: 'Past Two Weeks',
                getValue: function () {
                    return getDateStr(moment().subtract(2, 'weeks'));
                }
            },
            custom: {
                label: 'Custom Date',
                getValue: function () {
                    return getDateStr($scope.state.customContSrvDate);
                }
            }
        };
        $scope.contSrvDateOpts = Object.keys($scope.contSrvDateValues);

        var orderByMap = {
            name: ['NAME', 'OFFICE'],
            office: ['OFFICE', 'NAME'],
            completed: ['COMPLETED', 'NAME']
        };

        var defaultState = {
            taskList: null,
            taskMap: null,
            selTasks: null,
            selContSrvDateOpt: $scope.contSrvDateOpts[0],
            customContSrvDate: moment().subtract(2, 'weeks').format('MM/DD/Y'),
            selectedRCHS: {
                selection: []
            },
            orderBy: null,
            sortOrder: null,
            params: angular.copy(defaultParams),
            paramQueryString: '',
            pagination: angular.copy(defaultPagination),
            lastPageRequested: -1,
            results: null,
            iSelResult: null,
            request: {
                tasks: false,
                search: false
            }
        };

        var selectedEmpName = "";
        var selectedTaskName = "";
        var selectedTaskId = "";


        init();

        $scope.$watch('state.selTasks', updateSelTaskParams, true);
        $scope.$watch('state.params.taskActive', updateSelTaskParams);
        $scope.$watch('state.selectedRCHS', onSelRCHSChange, true);
        $scope.$watchGroup(['state.selContSrvDateOpt', 'state.customContSrvDate'], updateContSrvDateParam);
        $scope.$watchGroup(['state.orderBy', 'state.sortOrder'], onSortChange);
        $scope.$watch('state.params', onParamChange, true);
        $scope.$watch('state.pagination.currPage', onPageChange);

        /* --- Display Functions --- */

        $scope.selectResult = selectResult;
        $scope.getTaskTitle = getTaskTitle;
        $scope.clearSelectedTasks = clearSelectedTasks;
        $scope.getSortClass = getSortClass;
        $scope.toggleOrder = toggleOrder;
        $scope.overrideEmpTaskCompletion = overrideEmpTaskCompletion;
        $scope.getOverrideTaskEmpName = getOverrideTaskEmpName;
        $scope.getOverrideTaskTitle = getOverrideTaskTitle;
        $scope.submitTaskOverride = submitTaskOverride;
        $scope.rejectTaskOverride = rejectTaskOverride;

        function init() {
            $scope.state = angular.copy(defaultState);
            loadTasks();
        }

        /**
         * Load tasks
         */
        function loadTasks() {
            $scope.state.request.tasks = true;
            taskUtils.getAllTasks()
                .then(setTasks)
                .catch($scope.handleErrorResponse)
                .finally(function () {
                    $scope.state.request.tasks = false;
                })
            ;
        }

        /** Set tasks to state */
        function setTasks(tasks) {
            var taskList = $scope.state.taskList = [],
                taskMap = $scope.state.taskMap = {},
                selTasks = $scope.state.selTasks = {};
            tasks.forEach(function (task) {
                taskList.push(task);
                taskMap[task.taskId] = task;
                selTasks[task.taskId] = false;
            });
        }
        function getTaskTitle(taskId) {
            var taskMap = $scope.state.taskMap;
            if (taskId in taskMap) {
                return taskMap[taskId].title;
            }
            return '!? Unknown task #' + taskId + '';
        }

        /**
         * Update the search request params based on selected tasks.
         */
        function updateSelTaskParams() {
            if (!$scope.state.selTasks) {
                return;
            }
            $scope.state.params.taskId = Object.keys($scope.state.selTasks)
                .filter(function (taskId) {
                    var task = $scope.state.taskMap[taskId];
                    // Include the task if it is selected.
                    // Also ensure it is active if the active task filter is on.
                    return $scope.state.selTasks[taskId] &&
                        (!$scope.state.params.taskActive || task.active);
                });
        }

        /**
         * Clear all task selections
         */
        function clearSelectedTasks() {
            var selTasks = $scope.state.selTasks;
            for (var taskId in selTasks) {
                if (!selTasks.hasOwnProperty(taskId)) {
                    continue;
                }
                selTasks[taskId] = false;
            }
        }

        function updateContSrvDateParam() {
            var selection = $scope.contSrvDateValues[$scope.state.selContSrvDateOpt];
            $scope.state.params.contSrvFrom = selection.getValue();
        }

        /**
         * Perform a new search query, resetting pagination.
         */
        function onParamChange() {
            resetPagination();
            performSearch();
        }

        function performSearch() {
            if (!$scope.state.selTasks) {
                return;
            }
            unsetSearchResults();

            var pagination = $scope.state.pagination;
            $scope.state.lastPageRequested = $scope.state.pagination.currPage;

            var params = angular.copy($scope.state.params);
            params.limit = pagination.getLimit();
            params.offset = pagination.getOffset();

            $scope.state.request.search = true;
            searchApi.get(params).$promise
                .then(setSearchResults)
                .catch($scope.handleErrorResponse)
                .finally(function () {
                    $scope.state.request.search = false;
                })
            ;
            $scope.state.paramQueryString = generateReportQueryString();
        }

        function generateReportQueryString() {
            return $httpParamSerializer($scope.state.params);
        }

        function setSearchResults(resp) {
            $scope.state.iSelResult = null;
            $scope.state.results = resp.result.map(initializeResult);
            var pagination = $scope.state.pagination;
            pagination.setTotalItems(resp.total);
        }

        function unsetSearchResults() {
            $scope.state.iSelResult = null;
            $scope.state.results = [];
        }

        function initializeResult(result) {
            result.completedCount = result.tasks.filter(function (task) {
                return task.completed;
            }).length;
            return result;
        }

        function getDateStr(date) {
            return moment(date).format('Y-MM-DD');
        }

        function onSelRCHSChange() {
            var codes = $scope.state.params.respCtrHead = [];
            $scope.state.selectedRCHS.selection.forEach(function (rchs) {
                codes.push(rchs.code);
            });
        }

        function selectResult(index) {
            // If the given index is currently selected, unselect it.
            // Otherwise, set the selected index to the given index.
            if ($scope.state.iSelResult === index) {
                $scope.state.iSelResult = null;
            } else {
                $scope.state.iSelResult = index;
            }
        }

        /* --- Pagination --- */

        function onPageChange() {
            // Perform a search only if the page change wasn't already included in the last request.
            // This happens when the pagination is reset for a new query
            if ($scope.state.lastPageRequested !== $scope.state.pagination.currPage) {
                performSearch();
            }
        }

        /**
         * Attempt to reset the pagination to page 1.
         * Return true if the page was not already 1.
         * @return {boolean}
         */
        function resetPagination() {
            var pagination = $scope.state.pagination;
            if (pagination.currPage === 1) {
                return false;
            }
            pagination.currPage = 1;
            return true;
        }

        /* --- Sort --- */

        function onSortChange() {
            var sort = $scope.state.params.sort = [];
            var orderBys = orderByMap[$scope.state.orderBy];
            var sortOrder = $scope.state.sortOrder;
            for (var i in orderBys) {
                if (!orderBys.hasOwnProperty(i)) {
                    continue;
                }
                var orderBy = orderBys[i];
                sort.push(orderBy + ':' + (sortOrder || 'ASC'))
            }
        }

        function getSortClass(orderBy) {
            if ($scope.state.orderBy === orderBy) {
                if ($scope.state.sortOrder === 'ASC') {
                    return 'todo-report-sort-asc'
                }
                if ($scope.state.sortOrder === 'DESC') {
                    return 'todo-report-sort-desc'
                }
            }
            return '';
        }

        function toggleOrder(orderBy) {
            if ($scope.state.orderBy === orderBy) {
                if ($scope.state.sortOrder === 'ASC') {
                    $scope.state.sortOrder = 'DESC';
                } else {
                    $scope.state.orderBy = null;
                    $scope.state.sortOrder = null;
                }
            } else {
                $scope.state.orderBy = orderBy;
                $scope.state.sortOrder = 'ASC';
            }
        }

        function getOverrideTaskEmpName() {
            return selectedEmpName;
        }

        function getOverrideTaskTitle() {
            return selectedTaskName;
        }

        function overrideEmpTaskCompletion(taskId, taskName) {
            selectedEmpName = $scope.state.results[$scope.state.iSelResult].employee.fullName;
            selectedTaskName = taskName;
            selectedTaskId = taskId;
            modals.open('task-override-dialog');
        }

        function submitTaskOverride() {
            modals.resolve();

            var selectedEmpId = $scope.state.results[$scope.state.iSelResult].employee.employeeId;
            var tasks = $scope.state.results[$scope.state.iSelResult].tasks;
            var taskToUpdate;

            //Ensure task exists
            for (i = 0; i < tasks.length; i++ ) {
                if (tasks[i].taskId === selectedTaskId) {
                    taskToUpdate = tasks[i];
                }
            }
            // console.log(taskToUpdate);
            if (taskToUpdate == null) {
                resetTaskOverrideVars();
                return;
            }

            var params = {
                empId: selectedEmpId,
                taskId: selectedTaskId,
                updateEmpID: appProps.user.employeeId
            };

            UpdatePersonnelTaskAssignmentApi.get(params).$promise.catch($scope.handleErrorResponse);

            resetTaskOverrideVars();
            init();
        }

        function rejectTaskOverride() {
            modals.reject();
            resetTaskOverrideVars();
        }

        function resetTaskOverrideVars() {
            selectedEmpName = "";
            selectedTaskName = "";
            selectedTaskId = "";
        }

    }
})();
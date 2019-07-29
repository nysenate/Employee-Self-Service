(function () {
    angular.module('essMyInfo')
        .controller('TodoReportCtrl',
                    ['$scope', 'TaskUtils', 'EmpPATSearchApi', 'PaginationModel', todoCtrl]);

    function todoCtrl($scope, taskUtils, searchApi, pagination) {
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
            customContSrvDate: moment().format('Y-MM-DD'),
            selectedRCHS: {
                selection: []
            },
            orderBy: null,
            sortOrder: null,
            params: angular.copy(defaultParams),
            pagination: angular.copy(defaultPagination),
            lastPageRequested: -1,
            results: null,
            iSelResult: null,
            request: {
                tasks: false,
                search: false
            }
        };

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
                var idStr = task.taskIdStr = getTaskIdStr(task.taskId);
                taskList.push(task);
                taskMap[idStr] = task;
                selTasks[idStr] = false;
            });
        }

        /**
         * Get a string version of a task id object.
         * @param taskId
         * @return {string}
         */
        function getTaskIdStr(taskId) {
            return taskId.taskType + "-" + taskId.taskNumber;
            return taskId.taskType + "-" + taskId.taskNumber;
        }

        function getTaskTitle(taskId) {
            var taskIdStr = getTaskIdStr(taskId);
            var taskMap = $scope.state.taskMap;
            if (taskIdStr in taskMap) {
                return taskMap[taskIdStr].title;
            }
            return '!? Unknown task "' + taskIdStr + '"';
        }

        /**
         * Update the search request params based on selected tasks.
         */
        function updateSelTaskParams() {
            if (!$scope.state.selTasks) {
                return;
            }
            $scope.state.params.taskId = Object.keys($scope.state.selTasks)
                .filter(function (taskIdStr){
                    var task = $scope.state.taskMap[taskIdStr];
                    // Include the task if it is selected.
                    // Also ensure it is active if the active task filter is on.
                    return $scope.state.selTasks[taskIdStr] &&
                        (!$scope.state.params.taskActive || task.active);
                });
        }

        /**
         * Clear all task selections
         */
        function clearSelectedTasks() {
            var selTasks = $scope.state.selTasks;
            for (var taskIdStr in selTasks) {
                if (!selTasks.hasOwnProperty(taskIdStr)) {
                    continue;
                }
                selTasks[taskIdStr] = false;
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
            Object.assign(params, {limit: pagination.getLimit(), offset: pagination.getOffset()});
            $scope.state.request.search = true;
            searchApi.get(params).$promise
                .then(setSearchResults)
                .catch($scope.handleErrorResponse)
                .finally(function () {
                    $scope.state.request.search = false;
                })
            ;
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
            result.completedCount = result.tasks.filter(function (task) {return task.completed;}).length;
            return result;
        }

        function getDateStr(date) {
            return moment(date).format('Y-MM-DD');
        }

        function onSelRCHSChange() {
            var codes = $scope.state.params.respCtrHead = [];
            $scope.state.selectedRCHS.selection.forEach(function(rchs) {
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

    }
})();
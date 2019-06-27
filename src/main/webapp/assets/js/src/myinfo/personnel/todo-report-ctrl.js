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
            empActive: null,
            taskId: null,
            contServFrom: null,
            taskActive: true
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

        var defaultState = {
            taskMap: null,
            selTasks: null,
            selContSrvDateOpt: $scope.contSrvDateOpts[0],
            customContSrvDate: moment().format('Y-MM-DD'),
            params: angular.copy(defaultParams),
            pagination: angular.copy(defaultPagination),
            results: null
        };

        init();

        $scope.$watch('state.selTasks', updateSelTaskParams, true);
        $scope.$watchGroup(['state.selContSrvDateOpt', 'state.customContSrvDate'], updateContSrvDateParam);
        $scope.$watch('state.params', performSearch, true);
        $scope.$watch('state.pagination.currPage', performSearch);

        function init() {
            $scope.state = angular.copy(defaultState);
            loadTasks();
        }

        /**
         * Load tasks
         */
        function loadTasks() {
            taskUtils.getAllTasks()
                .then(setTasks)
                .catch($scope.handleErrorResponse);
        }

        /** Set tasks to state */
        function setTasks(tasks) {
            var taskMap = $scope.state.taskMap = {},
                selTasks = $scope.state.selTasks = {};
            tasks.forEach(function (task) {
                var idStr = task.taskIdStr = getTaskIdStr(task.taskId);
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
                    return $scope.state.selTasks[taskIdStr];
                });
        }

        function updateContSrvDateParam() {
            var selection = $scope.contSrvDateValues[$scope.state.selContSrvDateOpt];
            $scope.state.params.contSrvFrom = selection.getValue();
        }

        function performSearch() {
            if (!$scope.state.selTasks) {
                return;
            }
            var pagination = $scope.state.pagination;
            var params = angular.copy($scope.state.params);
            Object.assign(params, {limit: pagination.getLimit(), offset: pagination.getOffset()});

            searchApi.get(params).$promise
                .then(setSearchResults)
                .catch($scope.handleErrorResponse);
        }

        function setSearchResults(resp) {
            $scope.state.results = resp.result.map(initializeResult);
            var pagination = $scope.state.pagination;
            pagination.setTotalItems(resp.total);
        }

        function initializeResult(result) {
            result.completedCount = result.tasks.filter(function (task) {return task.completed;}).length;
            return result;
        }

        function getDateStr(date) {
            return moment(date).format('Y-MM-DD');
        }

    }
})();
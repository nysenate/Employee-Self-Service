(function () {

    angular.module('essMyInfo')
        .service('TaskUtils', ['PersonnelTasksForEmpApi', 'PersonnelAssignedTaskApi', 'appProps', 'RestErrorService',
                               taskUtils]);

    function taskUtils(tasksForEmpApi, patApi, appProps, restErrorService) {

        AcknowledgmentTask.prototype = new PersonnelTask();
        MoodleTask.prototype = new PersonnelTask();

        return {
            parseTask: parseTask,
            getEmpTasks: getEmpTasks,
            getPersonnelAssignedTask: getPersonnelAssignedTask
        };

        /**
         * Class defining personnel tasks
         */
        function PersonnelTask(task) {
            // Use same fields as personnel task view
            for (var prop in task) {
                if (task.hasOwnProperty(prop)) {
                    this[prop] = task[prop];
                }
            }

            /**
             * Produce a url to a page where the employee can act on the task
             */
            this.getActionUrl = function () {
                throw 'No URL for this task'
            };

            /**
             * Defines action link click behavior.  Defaults to open in same tab.
             */
            this.getActionUrlTarget = function () {
                return "_self";
            };

            /**
             * Verb describing task action.  Default "Complete".
             */
            this.getActionVerb = function () {
                return "Complete";
            };

            /**
             * Get the past tense of this task's action verb.
             */
            this.getActionVerbPastTense = function () {
                var verb = this.getActionVerb();
                if (verb[verb.length - 1] === 'e') {
                    return verb + 'd';
                }
                return verb + 'ed';
            };

            /**
             * Get an icon to represent this task
             */
            this.getIconClass = function () {
                return '.icon-warning';
            };
        }

        function AcknowledgmentTask(task) {
            var ackBaseUrl = appProps.ctxPath + "/myinfo/personnel/todo/acknowledgment/";

            PersonnelTask.apply(this, arguments);

            this.getActionUrl = function () {
                return ackBaseUrl + task.taskId.taskNumber;
            };

            this.getIconClass = function () {
                return 'icon-text-document';
            };

            this.getActionVerb = function () {
                return "Acknowledge"
            };
        }

        function MoodleTask(task) {
            PersonnelTask.apply(this, arguments);

            this.getActionUrl = function () {
                // fixme this only works for the legethics course
                return appProps.ctxPath + "/myinfo/personnel/todo/legethics";
            };

            this.getCourseUrl = function () {
                return task.taskDetails.url;
            };

            this.getActionUrlTarget = function () {
                return "_blank";
            };

            this.getIconClass = function () {
                return 'icon-graduation-cap';
            }
        }

        /**
         * Parse the task json into the appropriate PersonnelTask
         *
         * @param task
         * @return PersonnelTask
         */
        function parseTask(task) {
            var taskType = task.taskId.taskType;
            switch (taskType) {
                case 'DOCUMENT_ACKNOWLEDGMENT':
                    return new AcknowledgmentTask(task);
                case 'MOODLE_COURSE':
                    return new MoodleTask(task);
                default:
                    console.error("Unknown task type '" + taskType + "'!");
                    return new PersonnelTask(task);
            }
        }

        /**
         * Get all tasks assigned to the given employee.
         *
         * @param empId
         * @param detail
         * @return a promise that passes the task list to the callback.
         */
        function getEmpTasks(empId, detail) {
            var params = {
                empId: empId,
                detail: detail
            };

            return tasksForEmpApi.get(params).$promise
                .then(processTasks);

            function processTasks(resp) {
                return resp.tasks.map(parseTask);
            }
        }

        /**
         * Get a single personnel assigned task.
         *
         * @param empId
         * @param taskType
         * @param taskNumber
         * @return a promise that passes the loaded task to the callback.
         */
        function getPersonnelAssignedTask(empId, taskType, taskNumber) {
            var params = {
                empId: empId,
                taskType: taskType,
                taskNumber: taskNumber
            };

            return patApi.get(params).$promise
                .then(processTask)
                .catch(onError);

            function processTask(resp) {
                return parseTask(resp.task);
            }

            function onError(resp) {
                var errorCode = ((resp || {}).data || {}).errorCode;
                if (errorCode === 'PERSONNEL_ASSIGNED_TASK_NOT_FOUND') {
                    console.warn("Could not find personnel assigned task:", empId, taskType, taskNumber);
                } else {
                    restErrorService.handleErrorResponse(resp)
                }
                throw resp;
            }
        }
    }
})();

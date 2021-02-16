(function () {

    angular.module('essMyInfo')
        .service('TaskUtils', ['PersonnelTaskApi', 'PersonnelAssignmentsForEmpApi', 'PersonnelAssignmentApi', 'appProps', 'RestErrorService',
                               taskUtils]);

    function taskUtils(taskApi, assignmentsForEmpApi, assignmentApi, appProps, restErrorService) {

        AcknowledgmentTask.prototype = new PersonnelTask();
        MoodleTask.prototype = new PersonnelTask();
        VideoCodeTask.prototype = new PersonnelTask();

        return {
            getEmpAssignments: getEmpAssignments,
            getPersonnelTaskAssignment: getPersonnelTaskAssignment,
            getAllTasks: getAllTasks
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
                return ackBaseUrl + task.taskId;
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
                return appProps.ctxPath + "/myinfo/personnel/todo/legethics/" + task.taskId;
            };

            this.getCourseUrl = function () {
                return task.url;
            };

            this.getIconClass = function () {
                return 'icon-graduation-cap';
            }
        }

        function VideoCodeTask(task) {
            PersonnelTask.apply(this, arguments);

            this.getActionUrl = function () {
                return appProps.ctxPath + "/myinfo/personnel/todo/video/" + task.taskId;
            };

            this.getActionVerb = function () {
                return "Watch";
            };

            this.getIconClass = function () {
                return "icon-video";
            }

        }

        function EverfiCourse(task) {
            PersonnelTask.apply(this, arguments);

            this.getActionUrl = function () {
                // fixme URL in app props
                return 'https://admin.fifoundry.net/en/new-york-senate/sign_in';
            };

            this.getCourseUrl = function () {
                return task.url;
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
            var taskType = task.taskType;
            switch (taskType) {
                case 'DOCUMENT_ACKNOWLEDGMENT':
                    return new AcknowledgmentTask(task);
                case 'MOODLE_COURSE':
                    return new MoodleTask(task);
                case 'VIDEO_CODE_ENTRY':
                    return new VideoCodeTask(task);
                case 'EVERFI_COURSE':
                    return new EverfiCourse(task);
                default:
                    console.error("Unknown task type '" + taskType + "'!");
                    return new PersonnelTask(task);
            }
        }

        /**
         * Parse a task assignment by replacing the contained task with an enhanced version.
         * @param assignment
         * @return {*}
         */
        function parseAssignment(assignment) {
            if (!assignment.task) {
                return assignment;
            }
            var parsedTask = parseTask(assignment.task);
            assignment.task = parsedTask;
            return assignment;
        }

        /**
         * Get all tasks assigned to the given employee.
         *
         * @param empId
         * @param detail
         * @return a promise that passes the task list to the callback.
         */
        function getEmpAssignments(empId, detail) {
            var params = {
                empId: empId,
                detail: detail
            };

            return assignmentsForEmpApi.get(params).$promise
                .then(processTasks);

            function processTasks(resp) {
                return resp.assignments.map(parseAssignment);
            }
        }

        /**
         * Get a single personnel assigned task.
         *
         * @param empId
         * @param taskId
         * @return a promise that passes the loaded task to the callback.
         */
        function getPersonnelTaskAssignment(empId, taskId) {
            var params = {
                empId: empId,
                taskId: taskId
            };

            return assignmentApi.get(params).$promise
                .then(processAssignment)
                .catch(onError);

            function processAssignment(resp) {
                return parseAssignment(resp.task);
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

        /**
         * Get a list of all personnel tasks.
         */
        function getAllTasks() {
            return taskApi.get().$promise
                .then(processTasks);

            function processTasks(resp) {
                return resp.tasks.map(parseTask);
            }
        }
    }
})();

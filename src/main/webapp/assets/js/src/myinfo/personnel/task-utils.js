angular.module('essMyInfo')
    .service('TaskUtils', ['PersonnelTaskEmpApi', 'appProps', function (empTaskApi, appProps) {

        AcknowledgmentTask.prototype = new PersonnelTask();
        MoodleTask.prototype = new PersonnelTask();

        return {
            parseTask: parseTask,
            getEmpTasks: getEmpTasks
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
            var ackBaseUrl = appProps.ctxPath + "/myinfo/personnel/acknowledgments/";

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
            var moodleBaseUrl = appProps.ctxPath + "/idk/";

            PersonnelTask.apply(this, arguments);

            this.getActionUrl = function () {
                return moodleBaseUrl;
            };

            this.getIconClass = function () {
                return 'icon-graduation-cap';
            }
        }

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

        function getEmpTasks(empId, detail) {
            var params = {
                empId: empId,
                detail: detail
            };

            return empTaskApi.get(params).$promise
                .then(processTasks);

            function processTasks(resp) {
                return resp.tasks.map(parseTask);
            }
        }
    }]);

angular.module('ess')
    .service('promiseUtils',['$q', promiseUtilsService]);

function promiseUtilsService($q){
    return {
        serial: serial
    };

    /**
     * Sequentially executes the given promises
     * @param: tasks   an array of functions that return a promise
     **/
    function serial(tasks) {
        var prevPromise = $q.when();
        angular.forEach(tasks, function (task) {
            //First task
            if (!prevPromise) {
                prevPromise = task();
            } else {
                prevPromise = prevPromise.then(task);
            }
        });
        return prevPromise;
    }
}

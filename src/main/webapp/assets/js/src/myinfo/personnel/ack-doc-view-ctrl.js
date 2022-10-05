(function () {

angular.module('essMyInfo')
    .controller('AckDocViewCtrl', ['$scope', '$routeParams', '$q', '$location', '$window', '$timeout', '$sce',
                                   'appProps', 'modals', 'TaskUtils', 'AcknowledgmentApi',
                                        acknowledgmentCtrl]);

function acknowledgmentCtrl($scope, $routeParams, $q, $location, $window, $timeout, $sce,
                            appProps, modals, taskUtils, ackApi) {

    $scope.todoPageUrl = appProps.ctxPath + '/myinfo/personnel/todo';

    /** Flag indicating that the window scroll handler was bound */
    var windowScrollBound = false;

    var pdfjsLib = window['pdfjs-dist/build/pdf'];
    pdfjsLib.GlobalWorkerOptions.workerSrc =  appProps.ctxPath + '/assets/js/dest/pdf.worker.min.js';

    var initialState = {
        taskId: null,
        document: null,
        assignment: null,
        assignmentFound: false,
        acknowledged: false,
        ackTimestamp: null,
        docFound: false,
        docReady: false,
        docRead: false,
        pages: [],

        request: {
            document: false,
            ackGet: false,
            ackPost: false
        }
    };

    function init() {
        bindWindowScrollHandler();
        $scope.state = angular.copy(initialState);
        $scope.state.taskId = parseInt($routeParams.ackDocId);
        getTaskAssignment()
            .then(processAcknowledgment)
            .then(showPdf)
            .catch($scope.handleErrorResponse)
        ;
    }

    /* --- Display methods --- */

    /**
     * Return true if any requests are currently in progress.
     * @return {boolean}
     */
    $scope.isLoading = function () {
        var loading = false;
        angular.forEach($scope.state.request, function (status) {
            loading = loading || status;
        });
        return loading;
    };

    /**
     * Initiate the acknowledgement process: (each event requires the previous to occur successfully)
     * - Display the acknowledge prompt
     * - Post an acknowledgment that the doc has been read if selected.
     * - Reload the page if successful and prompt the user with navigation options.
     * - Go back to the acknowledgements page if the user clicked the return button
     */
    $scope.acknowledgeDocument = function () {
        modals.open('acknowledge-prompt')
            .then(function () {
                return postAcknowledgment()
            })
            .then(function () {
                $scope.updatePersonnelTaskBadge();
                return modals.open('acknowledge-success');
            })
            .then(function () {
                $location.url($scope.todoPageUrl)
            })
    };

    $scope.getDocUrl = function () {
        var baseUrl = $scope.ctxPath + $scope.state.document.path;
        var adobeArgs = '#view=fit&toolbar=0&statusbar=0&messages=0&navpanes=0';
        var url = baseUrl + adobeArgs;
        return $sce.trustAsResourceUrl(url);
    };

    /* --- Request Methods --- */

    /**
     * Get the assignment that corresponds to this ack doc
     */
    function getTaskAssignment() {
        $scope.state.request.ackGet = true;
        $scope.state.assignmentFound = false;
        var empId = appProps.user.employeeId,
            taskId = $scope.state.taskId;
        return taskUtils.getPersonnelTaskAssignment(empId, taskId)
            .then(setAckTask)
            .finally(function () {
                $scope.state.request.ackGet = false;
            });

        function setAckTask(assignment) {
            $scope.state.assignment = assignment;
            $scope.state.document = assignment.task;

            $scope.state.assignmentFound = true;
            $scope.state.docFound = true;
            $scope.state.docRead = false;
            // Potentially set the doc as read if the doc happens to fit in the window
            $timeout(checkIfDocRead, 500);
        }
    }

    function postAcknowledgment() {
        var params = {
            empId: appProps.user.employeeId,
            taskId: $scope.state.taskId
        };

        $scope.state.request.ackPost = true;
        return ackApi.save(params, {}, init, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.state.request.ackPost = false;
            });
    }

    /**
     * Determine whether the document was acknowledged based on received task
     */
    function processAcknowledgment() {
        var assignment = $scope.state.assignment;
        if (assignment && assignment.hasOwnProperty('completed')) {
            $scope.state.acknowledged = assignment.completed;
            $scope.state.ackTimestamp = assignment.timestamp;
        } else {
            console.warn('No assignment found for ack doc');
            throw 'No corresponding assignment for ack doc!';
        }
    }

    /**
     * Bind an event handler to detect when the window has scrolled to the bottom.
     * Also bind a $destroy handler to remove the bind when this controller is done.
     */
    function bindWindowScrollHandler() {
        if (windowScrollBound) {
            return;
        }
        angular.element($window).on('scroll', checkIfDocRead);
        $scope.$on('$destroy', function () {
            angular.element($window).off('scroll', checkIfDocRead);
        });
        windowScrollBound = true;
    }

    /**
     * Checks the window scroll to see if the bottom is in view.
     *
     * Sets docRead = true if the window is scrolled all the way down.
     */
    function checkIfDocRead () {
        if ($scope.state.docReady && windowAtBottom()) {
            console.log('Window is scrolled');
            $scope.state.docRead = true;
            $scope.$apply();
        }
    }

    /**
     * Returns true if the browser window is scrolled to the bottom
     */
    function windowAtBottom () {
        var windowHeight = "innerHeight" in window ? window.innerHeight : document.documentElement.offsetHeight;
        console.log("window height " + windowHeight);
        var body = document.body, html = document.documentElement;
        console.log("body " + body);
        var docHeight = Math.max(body.scrollHeight, body.offsetHeight,
                                 html.clientHeight,  html.scrollHeight, html.offsetHeight);
        console.log("docHeight " + docHeight);
        var windowBottom = windowHeight + window.pageYOffset;
        console.log("wwindowBottom " + windowBottom);
        console.log("windowBottom >= docHeight " + windowBottom >= docHeight);
        return windowBottom >= docHeight;
    }

    /**
     * Renders the pdf from the loaded document
     */
    function showPdf() {
        var url = $scope.ctxPath + $scope.state.document.path;
        $scope.state.docReady = false;
        return loadPdf(url)
            .then(renderPdf)
            .then(function () {
                $scope.state.docReady = true;
            })
    }

    /**
     * Requests the pdf from the server
     * @param url
     * @return promise resolved when the pdf is loaded
     */
    function loadPdf(url) {
        var deferred = $q.defer();
        pdfjsLib.getDocument(url).promise
            .then(deferred.resolve, deferred.reject);
        return deferred.promise;
    }

    /**
     * Initializes pdf rendering by generating canvas elements for each page.
     * Then render each page on its own canvas.
     * @param pdf
     * @return {*|PromiseLike<T>|Promise<T>}
     */
    function renderPdf(pdf) {
        var numPages = pdf.numPages;
        return setPages(numPages)
            .then(getRenderAllPagesFn(pdf))
    }

    /**
     * Returns a function that kicks off rendering of all pages for the given pdf.
     * @param pdf
     * @return function returning promise that is resolved when all pages are rendered.
     */
    function getRenderAllPagesFn(pdf) {
        return function () {
            var promises = [];
            for (var pageNum = 1; pageNum <= pdf.numPages; pageNum++) {
                var deferred = $q.defer();
                pdf.getPage(pageNum)
                    .then(getPageRenderFn(pageNum))
                    .then(deferred.resolve, deferred.reject);
                promises.push(deferred.promise);
            }
            return $q.all(promises);
        }
    }

    /**
     * Get a function for rendering a page with the given number.
     * @param pageNum
     * @return {function(*): *} starts rendering the page and returns a promise when rendering is done.
     */
    function getPageRenderFn(pageNum) {
        return function (page) {
            var canvasId = 'ack-pdf-page-' + pageNum;
            var canvas = document.getElementById(canvasId);

            // Get a viewport scaled to the canvas width;
            var defaultVp = page.getViewport({scale: 1});
            var scale = canvas.width / defaultVp.width;
            var viewport = page.getViewport({scale: scale});

            var context = canvas.getContext('2d');
            canvas.height = viewport.height;
            canvas.width = viewport.width;
            var renderContext = {
                canvasContext: context,
                viewport: viewport
            };
            return page.render(renderContext).promise
        }
    }

    /**
     * Set pages according to the number of pages.
     *
     * These pages are used to generate canvas elements.
     * @param numPages
     * @return {f}
     */
    function setPages(numPages) {
        var deferred = $q.defer();
        $scope.state.pages = [];
        for (var pageNum = 1; pageNum <= numPages; pageNum++) {
            $scope.state.pages.push(pageNum)
        }
        // $scope.apply(function () {
            deferred.resolve();
        // });
        return deferred.promise;
    }

    init();
}

})();

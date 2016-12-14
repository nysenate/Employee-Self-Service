essCore.factory('httpTimeoutChecker', ['appProps', 'modals', '$rootScope', function (appProps, modals, $rootScope) {
    var isPingInitialized = false;
    var isTimeoutModalOpen = false;
    var pingRate = 30;
    var idleTime = 0;
    var pingTolerance = 10; // after pinging 10 times failed, consider as session problem, and redirect user to front page
    /**
     * Cant use Timeout Api due to circular dependency issues.
     * essApi depends on essCore which depends on this factory.
     */
    function pingServer() {
        $.ajax({
                   type: "GET",
                   url: appProps.apiPath + '/timeout/ping.json?idleTime=' + idleTime,
                   success: function (data) {
                       if (data["message"] > 0 && isTimeoutModalOpen === false) {
                           modals.open('timeout');
                           isTimeoutModalOpen = true;
                           $rootScope.$digest();
                       }
                   },
                   error: function (data) {// after pinging 10 times failed, consider as network problem, and redirect user to front page
                       pingTolerance = pingTolerance-1;
                    if (pingTolerance < 0) {
                        window.location.replace(appProps.loginUrl);
                        window.location.reload(true);
                    }
                   }
               }
        );
    }

    return {
        request: function (request) {
            if (globalProps.timeoutExempt == "true") {
                return request;
            }
            if (!isPingInitialized) {
                var inactivityCheck = setInterval(function () {
                    idleTime += pingRate;
                    pingServer();
                }, pingRate * 1000);

                window.onbeforeunload = function () {
                    clearInterval(inactivityCheck);
                };

                $(document).on('change click keydown keypress keyup load resize scroll select submit', function () {
                    idleTime = 0;
                });

                isPingInitialized = true;
            }
            return request;
        },

        /**
         * Called from the timeout modal to notify this factory that the modal has been closed.
         */
        modalClosed: function () {
            isTimeoutModalOpen = false;
        }
    };
}]);

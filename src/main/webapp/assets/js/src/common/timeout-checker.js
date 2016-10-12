essCore.factory('httpTimeoutChecker', ['appProps', 'modals', '$rootScope', function (appProps, modals, $rootScope) {
    var isPingInitialized = false;
    var isTimeoutModalOpen = false;
    var pingRate = 5;
    var idleTime = 0;

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

/**
 * Defines functions that ping the server to keep the session alive and warn the user of impending timeout.
 * These functions are then set to run on an interval.
 */
angular.module('ess').run([
    '$window', '$document', 'appProps', 'modals', 'LocationService', 'TimeoutApi',
    function ($window, $document, appProps, modals, LocationService, TimeoutApi) {
        var timeoutModalName = 'timeout';
        var pingRate = 30;
        // Will warn the user if the remaining inactivity is under the warning threshold
        var warningThreshold = 70;
        // Set to true if the user was active since the last ping
        // Init as true from login
        var active = true;
        // This many consecutive non-401 ping errors will be tolerated before the user is logged out
        var pingFailTolerance = 10;
        var failedPings = 0;

        function logout () {
            LocationService.go('/logout', true);
        }

        /**
         * Send a ping to the server
         */
        function pingServer() {
            var params = {
                active: active
            };
            TimeoutApi.save(params, {}, onPingSuccess, onPingFail);
        }

        /**
         * Process a successful ping by opening the timeout modal if the user is nearing timeout.
         * @param resp
         */
        function onPingSuccess(resp) {
            failedPings = 0;
            var remainingInactivity = resp["remainingInactivity"];
            if (remainingInactivity < 0) {
                logout();
            }
            else if (remainingInactivity <= warningThreshold && !modals.isOpen(timeoutModalName)) {
                modals.open(timeoutModalName, {remainingInactivity: remainingInactivity});
            }
            // reset active flag
            active = false;
        }

        /**
         * Handle an error response from the ping endpoint
         * @param resp
         */
        function onPingFail(resp) {
            console.log(resp);
            var errorCode = resp.status;
            failedPings++;
            // If the ping reported that the user was unauthenticated or too many failed pings occurred, logout
            if (errorCode === 401 || failedPings >= pingFailTolerance) {
                logout();
            }
        }

        // Send an initial ping
        pingServer();
        // Set interval to ping periodically
        $window.setInterval(function () {
            pingServer();
        }, pingRate * 1000);

        // register activity when the user performs actions
        $document.on('change click keydown keypress keyup load resize scroll select submit', function () {
            if (!active && !modals.isOpen(timeoutModalName)) {
                active = true;
            }
        });

    }]);

var essTime = angular.module('essMyInfo');

/**
 * The wrapping controller that is the parent of the nav menu and view content.
 */
essApp.controller('MyInfoMainCtrl', ['$scope', '$q', 'appProps', 'badgeService', 'AckDocApi', 'AcknowledgmentApi',
   function($scope, $q, appProps, badgeService, ackDocApi, ackApi) {

       $scope.updateAckBadge = function () {
           var docs = [];
           var acks = {};

           var params = {empId: appProps.user.employeeId};

           var requests =
               [
                  ackDocApi.get({}, setDocs, $scope.handleErrorResponse).$promise,
                  ackApi.get(params, setAcks, $scope.handleErrorResponse).$promise
               ];

           $q.all(requests).then(setCount);

           function setAcks(resp) {
               angular.forEach(resp.acknowledgments, function (ack) {
                   acks[ack.ackDocId] = ack;
               });
           }

           function setDocs(resp) {
               docs = resp.documents;
           }

           function setCount() {
               var count = 0;
               angular.forEach(docs, function (doc) {
                   if (!acks.hasOwnProperty(doc.id)) {
                       count++;
                   }
               });
               badgeService.setBadgeValue('unacknowledgedDocuments', count);
           }
       };

       $scope.updateAckBadge();
   }
]);

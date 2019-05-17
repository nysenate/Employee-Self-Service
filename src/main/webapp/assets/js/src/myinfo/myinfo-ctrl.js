var essTime = angular.module('essMyInfo');

/**
 * The wrapping controller that is the parent of the nav menu and view content.
 */
essApp.controller('MyInfoMainCtrl', ['$scope', '$q', 'appProps', 'badgeService', 'PersonnelTasksForEmpApi',
                                     function($scope, $q, appProps, badgeService, empTaskApi) {

       $scope.updatePersonnelTaskBadge = function () {
           var params = {
               empId: appProps.user.employeeId,
               detail: true
           };

           return empTaskApi.get(params, setCount, $scope.handleErrorResponse);

           function setCount(resp) {
               var count = resp.tasks
                   .filter(function (task) {
                       return task.hasOwnProperty('completed') && !task.completed
                   })
                   .length;
               badgeService.setBadgeValue('incompleteTasks', count);
           }
       };

       $scope.updatePersonnelTaskBadge();
   }
]);

var essApp = angular.module('ess');

essApp.directive('supervisorRecordList', ['appProps', 'modals', function (appProps, modals) {
    return {
        scope: {
            records: '=',           // a list of records to display
            selectedIndices: '=?'   // a map of selected record indices, where the indices are
                                    // stored as object properties with a value of true
        },
        templateUrl: appProps.ctxPath + '/template/time/record/supervisor-record-list',
        link: link
    };

    function link($scope, $elem, $attr) {


        /** --- Display Methods --- */

        /**
         * Displays a record detail modal for the supplied record
         */
        $scope.showDetails = function(record) {
            var params = {
                record: record,
                employee: record.employee
            };
            modals.open('record-details', params, true);
        };

        /**
         * Removes the given index from selectedIndices if it already exists there, adds it if it doesn't
         */
        $scope.toggleSelected = function(index) {
            if ($scope.selectedIndices) {
                return $scope.setSelected(index, !$scope.selectedIndices[index]);
            }
            return false;
        };

        $scope.setSelected = function(index, isSelected) {
            if ($scope.selectedIndices) {
                if (!isSelected) {
                    delete $scope.selectedIndices[index];
                    return false;
                } else {
                    $scope.selectedIndices[index] = true;
                    return true;
                }
            }
            return false;
        };

        $scope.toggleRecsForEmp = function(record) {
            var desiredState = null;
            if ($scope.selectedIndices) {
                angular.forEach($scope.records, function(rec, i) {
                    if (rec.employeeId === record.employeeId) {
                        if (desiredState === null) {
                            desiredState = $scope.toggleSelected(i);
                        }
                        else {
                            $scope.setSelected(i, desiredState);
                        }
                    }
                });
            }
        }
    }
}]);
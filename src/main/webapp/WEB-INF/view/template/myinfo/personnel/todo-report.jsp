<section ng-controller="TodoReportCtrl" id="todoReportCtrl">
  <div class="my-info-hero">
    <h2>Personnel To-Do Reporting</h2>
  </div>

  <div class="content-container todo-report">

    <div class="todo-search-facets">
      <h3>Training Filters</h3>
      <div loader-indicator class="loader" ng-show="state.request.tasks"></div>
      <div ng-hide="state.request.tasks" class="training-facet">
        <label>
          <input type="checkbox"
                 ng-true-value="null"
                 ng-false-value="true"
                 ng-model="state.params.taskActive">
          Include inactive trainings
        </label>
        <hr>
        <div>
          <label ng-repeat="task in state.taskList | filter:{'active':true}">
            <input type="checkbox"
                   ng-model="state.selTasks[task.taskIdStr]">
            {{task.title}}
          </label>
        </div>
        <div ng-hide="state.params.taskActive">
          <hr>
          <label ng-repeat="task in state.taskList | filter:{'active':false}">
            <input type="checkbox"
                   ng-model="state.selTasks[task.taskIdStr]">
            {{task.title}}
          </label>
        </div>
      </div>

      <label>
        Training Completion Status<br>
        <select ng-model="state.params.totalCompletion">
          <option ng-value="null">Any</option>
          <option value="ALL_INCOMPLETE">All Incomplete</option>
          <option value="SOME_INCOMPLETE">Some Incomplete</option>
          <option value="SOME_COMPLETE">Some Complete</option>
          <option value="ALL_COMPLETE">All Complete</option>
        </select>
      </label>

      <h3>Employee Filters</h3>

      <label>
        Continuous Service Start Date<br>
        <select ng-model="state.selContSrvDateOpt"
                ng-options="contSrvDateValues[opt].label for opt in contSrvDateOpts"></select>
      </label>
      <label ng-show="state.selContSrvDateOpt === 'custom'">
        Custom Continuous Service Date<br>
        <input datepicker ng-model="state.customContSrvDate">
      </label>

      <label>
        Offices
        <rch-picker resp-ctr-heads="state.selectedRCHS"></rch-picker>
      </label>

    </div>

    <div class="todo-search-result-container">
      <label>
        Filter by employee name<br>
        <input type="text"
               ng-model="state.params.name"
               ng-model-options="{debounce: 300}"
        >
      </label>
      <label>
        <input type="checkbox" ng-model="state.params.empActive" ng-false-value="null">
        Show only active employees
      </label>

      <div ng-show="state.request.search" loader-indicator class="loader"></div>

      <div ng-hide="state.request.search" class="todo-search-results">

        <a ng-href="{{ctxPath}}/api/v1/personnel/task/emp/search/report{{$scope.queryString}}" target="_blank">
          Download results as CSV</a>

        <div>
          <dir-pagination-controls class="text-align-center"
                                   pagination-id="todo-report-pagination"
                                   boundary-links="true" max-size="10"></dir-pagination-controls>
        </div>
        <!-- A hidden div with dir-paginate to make the dir-pagination controls work... -->
        <div class="dir-pagination-controls-only-hack">
           <span
               dir-paginate="result in state.results | itemsPerPage: state.pagination.itemsPerPage"
               current-page="state.pagination.currPage"
               pagination-id="todo-report-pagination"
               total-items="state.pagination.totalItems">
           </span>
        </div>

        <table class="todo-search-result-table">
          <thead>
          <tr>
            <th>Completed/<br>Assigned</th>
            <th>Name</th>
            <th>Office</th>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat-start="result in state.results"
              ng-click="selectResult($index)"
              class="todo-report-result"
              ng-class="{'todo-report-result-selected': state.iSelResult === $index}">
            <td>
              <span class="completion-icon icon-check"
                    ng-show="result.completedCount === result.tasks.length">
              </span>
              <span class="completion-icon icon-minus"
                    ng-show="result.completedCount > 0 && result.completedCount < result.tasks.length">
              </span>
              <span class="completion-icon icon-cross"
                    ng-show="result.completedCount === 0">
              </span>
              {{result.completedCount}}/{{result.tasks.length}}
            </td>
            <td>
              {{result.employee.lastName}},
              {{result.employee.firstName}},
              {{result.employee.initial}}
            </td>
            <td>{{result.employee.respCtr.respCenterHead.name}}</td>
          </tr>
          <tr ng-repeat-end
              ng-if="state.iSelResult === $index"
              class="todo-report-result-details">
            <td colspan="3">
              <span class="bold-text">Email:</span>{{result.employee.email}}<br>
              <span class="bold-text">Cont. Service From:</span>{{result.employee.contServiceDate | moment:'ll'}}<br>
              <div ng-show="result.completedCount < result.tasks.length">
                <span class="bold-text">
                  Incomplete Trainings:<br>
                </span>
                <ul>
                  <li ng-repeat="task in result.tasks | filter:{'completed': false}">
                    {{getTaskTitle(task.taskId)}}
                  </li>
                </ul>
              </div>
              <div ng-show="result.completedCount > 0">
                <span class="bold-text">
                  Completed Trainings<br>
                </span>
                <ul>
                  <li ng-repeat="task in result.tasks | filter:{'completed': true}">
                    {{getTaskTitle(task.taskId)}}<br>
                    <span class="todo-result-completed-date">completed {{task.timestamp | moment:'ll'}}</span>
                  </li>
                </ul>
              </div>
            </td>
          </tr>
          </tbody>
        </table>

        <div>
          <dir-pagination-controls class="text-align-center"
                                   pagination-id="todo-report-pagination"
                                   boundary-links="true" max-size="10"></dir-pagination-controls>
        </div>
      </div>
    </div>

  </div>

</section>

<section ng-controller="TodoReportAssign">
  <div class="my-info-hero">
    <h2>Personnel To-Do Assignment</h2>
  </div>

  <div class="content-container todo-report">

    <div class="todo-search-facets">
      <h3>All Active Trainings</h3>
      <div loader-indicator class="loader" ng-show="state.request.tasks"></div>
      <div ng-hide="state.request.tasks" class="training-facet">

        <a href ng-click="clearSelectedTasks()">Clear selected trainings</a>
        <hr>
        <div>
        <label ng-repeat="task in state.taskList | filter:{'active':true}">
          <input type="checkbox"
                 ng-model="state.selTasks[task.taskId]">
          {{task.title}}
        </label>
        </div>
      </div>
      <hr>
      <h3>Employee Filters</h3>

      <label class="todo-search-facet">
        <input type="checkbox" ng-model="state.params.isSenator" ng-false-value="false" ng-true-value="true">
        Exclude Members
      </label>

      <label class="todo-search-facet">
        Offices
        <rch-picker resp-ctr-heads="state.selectedRCHS"></rch-picker>
      </label>



    </div>

    <div class="todo-search-result-container">
      <label class="todo-search-bar">
        Search by employee name<br>
        <input type="text"
               ng-model="state.params.name"
               ng-model-options="{debounce: 300}"
        >
      </label>

      <div ng-show="state.request.search" loader-indicator class="loader"></div>

      <div ng-hide="state.request.search" class="todo-search-results">

        <p class="todo-search-match-info">
          <span class="bold-text">{{state.pagination.totalItems}} Matching Employees</span>
          <a ng-href="{{ctxPath}}/api/v1/personnel/task/emp/search/report?{{state.paramQueryString}}"
             target="_blank">
            Download results as CSV
          </a>
        </p>

        <div class="todo-report-pagination-controls">
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
            <th class="{{getSortClass('completed')}}"
                ng-click="toggleOrder('completed')">
              Unassigned/<br>Active
            </th>
            <th class="{{getSortClass('name')}}"
                ng-click="toggleOrder('name')">
              Name
            </th>
            <th class="{{getSortClass('office')}}"
                ng-click="toggleOrder('office')">
              Office
            </th>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat-start="result in state.results"
              ng-click="selectResult($index)"
              class="todo-report-result"
              ng-class="{'todo-report-result-selected': state.iSelResult === $index}">
            <td>

              <span class="completion-icon icon-minus"
                    ng-show="result.diff.length > 0 && result.diff.length < result.activeCount">
              </span>
              <span class="completion-icon icon-cross"
                    ng-show="result.diff.length === result.activeCount">
              </span>
              <span class="completion-icon icon-check"
                    ng-show="result.diff.length === 0">
              </span>
              {{result.diff.length}}/{{result.activeCount}}
            </td>
            <td>
              {{result.employee.lastName}},
              {{result.employee.firstName}}{{result.employee.initial ? ',' : ''}}
              {{result.employee.initial}}
            </td>
            <td>{{result.employee.respCtr.respCenterHead.name}}</td>
          </tr>
          <tr ng-repeat-end
              ng-if="state.iSelResult === $index"
              class="todo-report-result-details">
            <td colspan="3">
              <span class="todo-report-result-details-label">Email:</span>
              {{result.employee.email}}<br>
              <span class="todo-report-result-details-label">Cont. Service From:</span>
              {{result.employee.contServiceDate | moment:'ll'}}<br>

              <div ng-show="result.diff.length > 0">
                <span class="todo-report-result-details-label">
                  Unassigned Trainings:<br>
                </span>
                <ul>
                  <li ng-repeat="d in result.diff"
                      class="todo-report-result-details-training">
                    {{getTaskTitle(d)}}
                    <br>
                    <button style="margin-top: 10px; margin-bottom: 15px;" ng-click="overrideEmpTaskCompletion(d, getTaskTitle(d),
                    principal.getEmployeeId())">Assign task
                    </button>
                  </li>
                </ul>
              </div>
            </td>

          </tr>
          </tbody>
        </table>

        <div class="todo-report-pagination-controls">
          <dir-pagination-controls class="text-align-center"
                                   pagination-id="todo-report-pagination"
                                   boundary-links="true" max-size="10"></dir-pagination-controls>
        </div>
      </div>
    </div>

  </div>

  <div modal-container>


    <modal modal-id="task-override-dialog">
      <div class="confirm-modal">
        <h3 class="content-info">
          Personnel Task Assignment
        </h3>
        <div class="confirmation-message">
          <p>
            Warning: You are attempting to assign a task to an employee
            <br>
            {{getOverrideTaskEmpName()}}
            <br> for task <br>
            {{getOverrideTaskTitle()}}
          </p>
        </div>
        <hr/>
        <div class="todo-input-container">
          <input ng-click="submitTaskAssignment(false)" class="submit-button" type="button" value="Proceed"/>
          <input ng-click="rejectTaskAssignment()" class="reject-button" type="button" value="Cancel"/>
        </div>
      </div>
    </modal>
  </div>

</section>

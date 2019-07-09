<section ng-controller="TodoReportCtrl">
  <div class="my-info-hero">
    <h2>Personnel To-Do Reporting</h2>
  </div>

  <div class="content-container todo-report">

    <div class="todo-search-facets">
      <h3>Trainings</h3>
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

      <h3>Employee</h3>
      <label>
        Continuous Service Start Date<br>
        <select ng-model="state.selContSrvDateOpt"
                ng-options="contSrvDateValues[opt].label for opt in contSrvDateOpts"></select>
      </label>
      <label ng-show="state.selContSrvDateOpt == 'custom'">
        Custom Continuous Service Date<br>
        <input datepicker ng-model="state.customContSrvDate">
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

        <a href="#">Download results as CSV</a>

        <table>
          <thead>
          <tr>
            <th>Completed/<br>Assigned</th>
            <th>Name</th>
            <th>Office</th>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat="result in state.results">
            <td>{{result.completedCount}}/{{result.tasks.length}}</td>
            <td>
              {{result.employee.lastName}},
              {{result.employee.firstName}},
              {{result.employee.initial}}
            </td>
            <td>{{result.employee.respCtr.respCenterHead.name}}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

  </div>

</section>

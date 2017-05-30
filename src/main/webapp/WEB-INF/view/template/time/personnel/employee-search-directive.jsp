
<section class="employee-search-directive">

  <div loader-indicator class="loader" ng-show="loadingEmps"></div>

  <div class="content-container" ng-hide="loadingEmps">
    <div class="content-info" ng-hide="selectedEmp">
      <input type="search" class="employee-search-bar"
             ng-model="search.fullName"
             ng-model-options="{debounce: 300}"
             placeholder="Search for an employee">

      <div class="employee-search-results">
        <ul>
          <li ng-repeat="emp in activeEmps | filter:search"
              class="search-result"
              ng-click="selectEmp(emp)"
              ng-bind-html="emp.fullName | highlight:search.fullName">
          </li>
        </ul>
      </div>

    </div>

    <div class="content-info selected-employee" ng-show="selectedEmp">
      <p>
        <span class="bold">
          Selected:
        </span>
        {{selectedEmp.fullName}}
      </p>

      <input type="button" class="time-neutral-button"
             value="Select Another Employee"
             ng-click="clearSelectedEmp()">

    </div>
  </div>

</section>

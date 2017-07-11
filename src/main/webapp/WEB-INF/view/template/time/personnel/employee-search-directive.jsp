
<section class="employee-search-directive">

  <div class="content-container">
    <div class="content-info" ng-hide="selectedEmp">
      <input type="search" class="employee-search-bar"
             ng-model="searchTerm"
             ng-model-options="{debounce: 300}"
             placeholder="Search for an employee">

      <div loader-indicator class="loader employee-search-loader"
           ng-class="{'visibility-hidden': !loadingEmps}">
      </div>

      <div class="employee-search-results" ng-if="searchResultsExist()">
        <ul infinite-scroll="getNextSearchResults()" infinite-scroll-parent>
          <li ng-repeat="emp in searchResults | filter:search"
              class="search-result"
              ng-click="selectEmp(emp)"
              ng-bind-html="emp.fullName | highlight:searchTerm">
          </li>
        </ul>
      </div>

    </div>

    <div class="content-info selected-employee" ng-show="selectedEmp">
      <div>
        <table>
          <tr>
            <th>Selected</th>
            <td>
              {{selectedEmp.fullName}}
            </td>
          </tr>
          <tr>
            <th>Emp. Id</th>
            <td>{{empInfo.employeeId}}</td>
          </tr>
          <tr>
            <th>Pay Type</th>
            <td>{{empInfo.payType}}<td>
          </tr>
        </table>
      </div>

      <div>
        <div ng-show="loadingEmpInfo" loader-indicator class="sm-loader"></div>
        <div ng-hide="loadingEmpInfo">
          <table>
            <tr>
              <th>Work Phone</th>
              <td>{{empInfo.workPhone}}</td>
            </tr>
            <tr>
              <th>Email</th>
              <td>{{empInfo.email}}</td>
            </tr>
            <tr>
              <th>Resp. Ctr.</th>
              <td>{{empInfo.respCtr.respCenterHead.name}}</td>
            </tr>
          </table>
        </div>
      </div>

      <div class="select-another">
        <input type="button" class="time-neutral-button"
               value="Select Another Employee"
               ng-click="clearSelectedEmp()">
      </div>

    </div>
  </div>

</section>

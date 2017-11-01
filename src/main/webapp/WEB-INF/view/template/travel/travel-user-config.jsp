<div ng-controller="UserConfigCtrl">
  <div class="travel-hero">
    <h2>User Configuration</h2>
  </div>

  <div>
    <div class="content-container content-controls">
      <p class="content-info">Grant another supervisor application privileges to review
        and/or approve your travel needs.</p>
      <div class="padding-10">
        <table class="simple-table">
          <thead>
          <tr>
            <th>#</th>
            <th>Supervisor</th>
            <th>Status</th>
            <th>Start Date</th>
            <th>End Date</th>
          </tr>
          </thead>
          <tbody>
            <tr>
              <td>1</td>
              <td>
                <select ng-init="selectedGrantee = grantees[0]"
                      ng-model="selectedGrantee"
                      ng-options="fullName for fullName in grantees | orderBy:'toString()'"></select>
              </td>
              <td></td>
              <td ng-class="{'half-opacity': selectedGrantee !== ''}">
                <div class="horizontal-input-group">
                  <input id="grant-start-date-{{$index}}" type="checkbox" />
                  <label for="grant-start-date-{{$index}}">Set Start Date</label>
                  <input style="width:100px" type="text" datepicker/>
                </div>
              </td>
              <td ng-class="{'half-opacity': selectedGrantee !== ''}">
                <div class="horizontal-input-group">
                  <input id="grant-end-date-{{$index}}" ng-checked="grantee.grantEnd" type="checkbox" />
                  <label for="grant-end-date-{{$index}}">Set End Date</label>
                  <input style="width:100px" type="text" datepicker/>
                </div>
              </td>
            </tr>
          </tbody>
        </table>


        <p>You picked: {{selectedGrantee}}</p>
      </div>
    </div>
  </div>
</div>

<div class="content-container text-align-center">
  <div>
    <h1 class="content-info">Review</h1>

    <div class="grid grid-pad">

      <%--Purpose--%>
      <div class="col-4-12 bold margin-top-20">
        Purpose of Travel
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.PURPOSE)" class="icon-edit pointer" title="Edit purpose of travel"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        {{reviewApp.purposeOfTravel}}
      </div>

      <%--Origin--%>
      <div class="col-4-12 margin-top-20 bold">
        Departure (Origin)
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ORIGIN)" class="icon-edit pointer" title="Edit origin"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        <div class="travel-origin-display-box">
          <div>
            {{reviewApp.origin.addr1}}<br/>
            <span ng-if="reviewApp.origin.addr2.length > 0">{{reviewApp.origin.addr2}}<br/></span>
            {{reviewApp.origin.city}} {{reviewApp.origin.state}} {{reviewApp.origin.zip5}}
          </div>
        </div>
      </div>

      <%--Destinations--%>
      <div ng-repeat="dest in reviewApp.destinations">
        <div ng-if="$index === 0" class="col-4-12 margin-top-20 bold">
          Destination (To)
          <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.DESTINATION)" class="icon-edit pointer" title="Edit destinations"></span>
        </div>
        <div ng-if="$index !== 0" class="col-4-12 margin-top-20">
          &nbsp
        </div>
        <div class="col-8-12 margin-top-20">
          <travel-destination-directive destination="dest"></travel-destination-directive>
        </div>
      </div>

      <%--Allowances--%>
      <div class="col-4-12 margin-top-20 bold">
        Estimated Expenses
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ALLOWANCES)" class="icon-edit pointer" title="Edit expenses"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        <div class="col-5-12 margin-5">
          Meals: {{reviewApp.mealAllowance | currency}}
          <span ng-if="reviewApp.mealAllowance > 0"
                class="icon-info pointer"
                ng-click="displayMealDetails()"
                title="View detailed meal expense info">
          </span>
        </div>
        <div class="col-5-12 margin-5">
          Lodging: {{reviewApp.lodgingAllowance | currency}}
          <span ng-if="reviewApp.lodgingAllowance > 0"
                class="icon-info pointer"
                ng-click="displayLodgingDetails()"
                title="View detailed lodging expense info">
          </span>
        </div>
        <div class="col-5-12 margin-5">
          Mileage: {{reviewApp.route.mileageAllowance | currency}}
          <span ng-if="reviewApp.mileageAllowance > 0"
                class="icon-info pointer"
                ng-click="displayMileageDetails()"
                title="View detailed mileage expense info">
          </span>
        </div>
        <div class="col-5-12 margin-5">
          Tolls: {{reviewApp.tollsAllowance | currency}}
        </div>
        <div class="col-5-12 margin-5">
          Parking: {{reviewApp.parkingAllowance | currency}}
        </div>
        <div class="col-5-12 margin-5">
          Taxi/Bus/Subway: {{reviewApp.alternateAllowance | currency}}
        </div>
        <div class="col-5-12 margin-5">
          Registration Fee: {{reviewApp.registrationAllowance | currency}}
        </div>
        <div class="col-5-12 margin-5">

        </div>
      </div>
    </div>

    <hr class="width-90 margin-20">

    <%--Google Map--%>
    <div id="map" class="margin-top-20" style="width: 650px; height: 375px; margin-left: auto; margin-right: auto;"></div>

  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Back"
           ng-click="reviewCallback(ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="submitConfirmModal()">
  </div>
</div>

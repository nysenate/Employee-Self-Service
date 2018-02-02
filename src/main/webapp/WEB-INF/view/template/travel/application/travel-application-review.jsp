
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
        {{app.purposeOfTravel}}
      </div>

      <%--Origin--%>
      <div class="col-4-12 margin-top-20 bold">
        Departure (Origin)
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ORIGIN)" class="icon-edit pointer" title="Edit origin"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        <div class="travel-origin-display-box">
          <div>
            {{app.itinerary.origin.addr1}}<br/>
            <span ng-if="app.itinerary.origin.addr2.length <= 0">{{app.itinerary.origin.addr2}}<br/></span>
            {{app.itinerary.origin.city}} {{app.itinerary.origin.state}} {{app.itinerary.origin.zip5}}
          </div>
        </div>
      </div>

      <%--Destinations--%>
      <div ng-repeat="dest in app.itinerary.destinations.items">
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
          Meals: {{app.allowances.meals.total | currency}}
        </div>
        <div class="col-5-12 margin-5">
          Lodging: {{app.allowances.lodging.total | currency}}
          <span class="icon-info"
                ng-click="displayLodgingDetails()"></span>
        </div>
        <div class="col-5-12 margin-5">
          Mileage: {{app.allowances.mileage.total | currency}}
        </div>
        <div class="col-5-12 margin-5">
          Tolls: {{app.allowances.tolls | currency}}
        </div>
        <div class="col-5-12 margin-5">
          Parking: {{app.allowances.parking | currency}}
        </div>
        <div class="col-5-12 margin-5">
          Taxi/Bus/Subway: {{app.allowances.alternate | currency}}
        </div>
        <div class="col-5-12 margin-5">
          Registration Fee: {{app.allowances.registrationFee | currency}}
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
           ng-click="reviewCallback(ACTIONS.NEXT)">
  </div>
</div>

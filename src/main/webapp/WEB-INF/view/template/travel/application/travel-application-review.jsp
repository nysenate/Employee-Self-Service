
<div class="content-container text-align-center padding-bottom-10">
  <div>
    <h4 class="content-info">Review</h4>

    <div class="grid grid-pad">

      <%--Purpose--%>
      <div class="col-4-12">
        Purpose of Travel
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.PURPOSE)" class="icon-edit pointer" title="Edit purpose of travel"></span>
      </div>
      <div class="col-8-12">
        {{app.purposeOfTravel}}
      </div>

      <%--Origin--%>
      <div class="col-4-12 margin-top-20">
        Departure (Origin)
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ORIGIN)" class="icon-edit pointer" title="Edit origin"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        <div class="travel-location-div">
          <div>
            {{app.itinerary.origin.addr1}}<br/>
            <span ng-if="app.itinerary.origin.addr2.length <= 0">{{app.itinerary.origin.addr2}}<br/></span>
            {{app.itinerary.origin.city}} {{app.itinerary.origin.state}} {{app.itinerary.origin.zip5}}
          </div>
        </div>
      </div>

      <%--Destinations--%>
      <div ng-repeat="dest in app.itinerary.destinations.items">
        <div ng-if="$index === 0" class="col-4-12 margin-top-20">
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
      <div class="col-4-12 margin-top-20">
        Mileage
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ALLOWANCES)" class="icon-edit pointer" title="Edit allowances"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        {{app.allowances.mileage.total | currency}}
      </div>
      <div class="col-4-12 margin-top-20">
        Meals
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ALLOWANCES)" class="icon-edit pointer" title="Edit allowances"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        {{app.allowances.meals.total | currency}}
      </div>
      <div class="col-4-12 margin-top-20">
        Lodging
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ALLOWANCES)" class="icon-edit pointer" title="Edit allowances"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        {{app.allowances.lodging.total | currency}}
      </div>
      <div class="col-4-12 margin-top-20">
        Tolls
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ALLOWANCES)" class="icon-edit pointer" title="Edit allowances"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        {{app.allowances.tolls | currency}}
      </div>
      <div class="col-4-12 margin-top-20">
        Parking
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ALLOWANCES)" class="icon-edit pointer" title="Edit allowances"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        {{app.allowances.parking | currency}}
      </div>
      <div class="col-4-12 margin-top-20">
        Taxi/Bus/Subway
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ALLOWANCES)" class="icon-edit pointer" title="Edit allowances"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        {{app.allowances.alternate | currency}}
      </div>
      <div class="col-4-12 margin-top-20">
        Registration Fee
        <span ng-click="reviewCallback(ACTIONS.EDIT, STATES.ALLOWANCES)" class="icon-edit pointer" title="Edit allowances"></span>
      </div>
      <div class="col-8-12 margin-top-20">
        {{app.allowances.registrationFee | currency}}
      </div>
    </div>

  </div>

  <div class="margin-top-20">
    <input type="button" class="neutral-button" value="Back"
           ng-click="reviewCallback(ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="reviewCallback(ACTIONS.NEXT)">
  </div>
</div>
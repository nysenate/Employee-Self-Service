
<div class="content-container text-align-center padding-bottom-10">
  <div>
    <h4 class="content-info">Review</h4>

    <div class="grid grid-pad">

      <%--Purpose--%>
      <div class="col-4-12">
        Purpose of Travel
      </div>
      <div class="col-8-12">
        {{app.purposeOfTravel}}
      </div>

      <%--Origin--%>
      <div class="col-4-12">
        Departure (Origin)
      </div>
      <div class="col-8-12">
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
        <div ng-if="$index === 0" class="col-4-12">
          Destination (To)
        </div>
        <div ng-if="$index !== 0" class="col-4-12">
          &nbsp
        </div>
        <div class="col-8-12">
          <div class="travel-location-div">
            <div style="float: left; font-size: 0.8em;">
              Arrival Date: {{dest.arrivalDate | date: 'shortDate'}}<br/>
              Departure Date: {{dest.departureDate | date: 'shortDate'}}
            </div>

            <div>
              {{dest.address.addr1}}<br/>
              <span ng-if="dest.address.addr2.length <= 0">{{dest.address.addr2}}<br/></span>
              {{dest.address.city}} {{dest.address.state}} {{dest.address.zip5}}
            </div>

            <div style="float: right; font-size: 0.8em;">
              Mode of Transportation: <br/>
              {{dest.modeOfTransportation}}
            </div>
          </div>
        </div>
      </div>

      <%--Allowances--%>
      <div class="col-4-12">
        Mileage
      </div>
      <div class="col-8-12">
        {{app.allowances.mileage | currency}}}
      </div>
      <div class="col-4-12">
        Meals
      </div>
      <div class="col-8-12">
        {{app.allowances.gsa.meals | currency}}
      </div>
      <div class="col-4-12">
        Lodging
      </div>
      <div class="col-8-12">
        {{app.allowances.gsa.lodging | currency}}
      </div>
      <div class="col-4-12">
        Tolls
      </div>
      <div class="col-8-12">
        {{app.allowances.tolls | currency}}
      </div>
      <div class="col-4-12">
        Parking
      </div>
      <div class="col-8-12">
        {{app.allowances.parking | currency}}
      </div>
      <div class="col-4-12">
        Taxi/Bus/Subway
      </div>
      <div class="col-8-12">
        {{app.allowances.alternate | currency}}
      </div>
      <div class="col-4-12">
        Registration Fee
      </div>
      <div class="col-8-12">
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
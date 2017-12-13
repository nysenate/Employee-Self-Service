<div class="content-container text-align-center padding-bottom-10">
  <div>
    <h4 class="content-info">Estimated Expenses</h4>

    <p>
      Select requested reimbursements for each destination.
    </p>

    <div ng-repeat="dest in destinations">
      <div class="inline-block" style="width: 550px;">
        <%--TODO Make destination box into directive--%>
        <div class="travel-location-div width-100">
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

      <span class="margin-10">
        <label>Mileage <input type="checkbox" ng-model="dest.requestMileage"></label>
      </span>
      <span class="inline-block margin-10">
        <label>Meals <input type="checkbox" ng-model="dest.requestMeals"></label>
      </span>
      <span class="inline-block margin-10">
        <label>Lodging <input type="checkbox" ng-model="dest.requestLodging"></label>
      </span>
    </div>


    <hr class="width-90">
    <p class="margin-top-40">
      Other expenses.
    </p>
    <div class="grid padding-10">
      <div class="col-6-12 padding-bottom-10">
        <label class="travel-allowance-label">Tolls:</label>
        <input ng-model="allowances.tolls" type="number" step="0.01" min="0">
      </div>
      <div class="col-6-12 padding-bottom-10">
        <label class="travel-allowance-label">Parking:</label>
        <input ng-model="allowances.parking" type="number" step="0.01" min="0">
      </div>
      <div class="col-6-12">
        <label class="travel-allowance-label">Taxi/Bus/Subway:</label>
        <input ng-model="allowances.alternate" type="number" step="0.01" min="0">
      </div>
      <div class="col-6-12">
        <label class="travel-allowance-label">Registration Fee:</label>
        <input ng-model="allowances.registrationFee" type="number" step="0.01" min="0">
      </div>
    </div>

  </div>

  <div class="margin-top-20">
    <input type="button" class="neutral-button" value="Back"
           ng-click="allowancesCallback(destinations, allowances, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="allowancesCallback(destinations, allowances, ACTIONS.NEXT)">
  </div>
</div>
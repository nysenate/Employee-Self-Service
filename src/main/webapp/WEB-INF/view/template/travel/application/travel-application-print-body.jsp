<div>
  <div class="text-align-center">
    <h2 class="margin-5 bold">NEW YORK STATE SENATE</h2>
    <h3 class="margin-5">Secretary of the Senate</h3>
    <h2 class="margin-5">APPLICATION FOR TRAVEL APPROVAL</h2>
  </div>

  <div class="margin-20 travel-print-info-container">
    <div class="grid">

      <div class="col-12-12 row">
        <div class="grid">
          <div class="col-2-12 travel-print-label">
            Date:
          </div>
          <div class="col-6-12">
            {{(app.submittedDateTime | date:'shortDate') || NOT_AVAILABLE}}
          </div>

          <div class="col-2-12 travel-print-label">
            NYS EMPLID#:
          </div>
          <div class="col-2-12">
            {{(app.traveler.nid) || NOT_AVAILABLE}}
          </div>
        </div>
      </div>

      <div class="col-12-12 row">
        <div class="grid">
          <div class="col-2-12 travel-print-label">
            Name/Title:
          </div>
          <div class="col-6-12">
            {{(app.traveler.fullName) || NOT_AVAILABLE}} - {{(app.traveler.jobTitle) || NOT_AVAILABLE}}
          </div>

          <div class="col-2-12 travel-print-label">
            Phone:
          </div>
          <div class="col-2-12">
            {{(app.traveler.workPhone) || NOT_AVAILABLE}}
          </div>
        </div>
      </div>

      <div class="col-12-12 row">
        <div class="grid">
          <div class="col-2-12 travel-print-label">
            Office:
          </div>
          <div class="col-6-12">
            {{((app.traveler.respCtr.respCenterHead.name) || NA) || NOT_AVAILABLE}}
          </div>

          <div class="col-2-12 travel-print-label">
            Agency Code:
          </div>
          <div class="col-2-12">
            {{(app.traveler.respCtr.agencyCode) || NOT_AVAILABLE}}
          </div>
        </div>
      </div>

      <div class="col-12-12 row">
        <div class="grid">
          <div class="col-2-12 travel-print-label">
            Office Address:
          </div>
          <div class="col-10-12">
            {{(app.traveler.workAddress.formattedAddress) || NOT_AVAILABLE}}
          </div>
        </div>
      </div>

      <div class="col-12-12 row width-100" style="border-bottom: 4px solid grey; width: 100%;">
      </div>

      <div class="col-12-12 row">
        <div class="grid">
          <div class="col-2-12 travel-print-label">
            Departure:
          </div>
          <div class="col-10-12">
            {{(app.route.origin.formattedAddressWithCounty) || NOT_AVAILABLE}}
          </div>
        </div>
      </div>

      <div class="col-12-12 row">
        <div class="grid">
          <span ng-repeat="dest in app.accommodations.destinations" style="font-weight: normal;">
          <div class="col-2-12 travel-print-label">
            <span ng-if="$first">Destination:</span>
            <span ng-if="!$first">&nbsp;</span>
          </div>
          <div class="col-10-12 float-left">
            {{(dest.address.formattedAddressWithCounty) || NOT_AVAILABLE}}
          </div>
        </span>
        </div>
      </div>

      <div class="col-12-12 row">
        <div class="grid">
          <div class="col-2-12 travel-print-label">
            Dates of Travel:
          </div>
          <div class="col-10-12">
            {{(app.startDate | date:'shortDate') || NOT_AVAILABLE}}
            to {{(app.endDate | date:'shortDate') || NOT_AVAILABLE}}
          </div>
        </div>
      </div>

      <div class="col-12-12 row">
        <div class="grid">
          <div class="col-2-12 travel-print-label">
            Purpose:
          </div>
          <div class="col-10-12">
            {{(app.purposeOfTravel) || NOT_AVAILABLE}}
          </div>
        </div>
      </div>

    </div>
  </div>

  <div style="overflow: auto;">
    <div class="travel-print-mot-box">
      <h4 style="margin: 0px 0px 10px 0px;">Mode of Transportation</h4>
      <div ng-repeat="mode in modeOfTransportations"
           ng-if="app.route" <%--Only evaluate this once $scope.app has been set by async request--%>
           style="display: inline;">
        <label>{{mode.displayName}} </label><input type="checkbox" ng-checked="containsMot(mode)"
                                                   onclick="return false;">
        <span ng-if="!$last"><br/></span>
      </div>
    </div>

    <div class="travel-print-allowances-box">
      <h4 style="margin: 0px 0px 10px 0px;">Estimated Travel Costs</h4>
      <label>Transportation</label><span>{{(app.transportationAllowance | currency) || NOT_AVAILABLE}}</span><br/>
      <label>Food</label><span>{{(app.mealAllowance.totalMealAllowance | currency) || NOT_AVAILABLE}}</span><br/>
      <label>Lodging</label><span>{{(app.lodgingAllowance.totalLodgingAllowance | currency) || NOT_AVAILABLE}}</span><br/>
      <label>Parking/Tolls</label><span>{{(tollsAndParking() | currency) || NOT_AVAILABLE}}</span><br/>
      <label>Taxi/Bus/Subway</label><span>{{(app.alternateAllowance | currency) || NOT_AVAILABLE}}</span><br/>
      <label>Registration Fee</label><span>{{(app.registrationAllowance | currency) || NOT_AVAILABLE}}</span><br/>
      <label>TOTAL</label><span>{{(app.totalAllowance | currency) || NOT_AVAILABLE}}</span><br/>
    </div>
  </div>

  <div class="margin-20">
    <div class="width-100" style="border-bottom: 4px solid grey; width: 100%;">
    </div>
  </div>

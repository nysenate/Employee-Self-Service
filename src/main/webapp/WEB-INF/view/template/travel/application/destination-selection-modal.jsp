<div class="padding-10">
  <h3 class="content-info">Enter destination info</h3>
  <div class="content-info">
    <div class="padding-10">
      <label>Destination: <input travel-address-autocomplete
                                 callback="addressCallback(address)"
                                 address="{{destination.address.formatted_address}}"
                                 placeholder="200 State St, Albany NY 12210" type="text" size="30"></label>
    </div>
    <div class="padding-10">
      <label>Arrival Date: <input datepicker ng-model="destination.arrivalDate" size="13"></label>
      <label>Departure Date: <input datepicker ng-model="destination.departureDate" size="13"></label>
    </div>
    <div class="padding-10">
      <label>Mode of Transportation:
        <select ng-model="destination.modeOfTransportation"
                ng-options="mode for mode in MODES_OF_TRANSPORTATION"></select>
      </label>
    </div>
    <div>
      <label>Waypoint </label>
      <span title="Waypoints are temporary stops on the way to your primary destination. Waypoints are not eligible for meal or lodging reimbursements, but are eligible for transportation reimbursements"
            class="icon-help-with-circle"></span>
      <input type="checkbox">
    </div>
  </div>
  <div class="padding-top-10 text-align-center">
    <input type="button" class="neutral-button"
           value="Cancel"
           ng-click="cancel()">
    <span class="padding-left-10">
      <input type="button" class="submit-button"
             value="Select"
             ng-disabled="!allFieldsEntered()"
             ng-click="submit()">
    </span>
  </div>

</div>

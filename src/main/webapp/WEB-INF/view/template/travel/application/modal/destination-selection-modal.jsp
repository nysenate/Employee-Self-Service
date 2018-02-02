<div class="content-container no-top-margin text-align-center">
  <h3 class="content-info">Enter destination info</h3>
    <div class="margin-20">
      <label>Destination:
        <input travel-address-autocomplete
               callback="addressCallback(address)"
               address="{{destination.address.formatted_address}}"
               placeholder="200 State St, Albany NY 12210" type="text" size="30"></label>
    </div>
    <div class="margin-20">
      <label>Arrival Date: <input datepicker ng-model="destination.arrivalDate" size="13"></label>
      <label>Departure Date: <input datepicker ng-model="destination.departureDate" size="13"></label>
    </div>
    <div class="margin-20">
      <label>Mode of Transportation:
        <select ng-model="destination.modeOfTransportation"
                ng-options="mode for mode in MODES_OF_TRANSPORTATION"></select>
      </label>
    </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button"
           value="Cancel"
           ng-click="cancel()">
    <input type="button" class="submit-button"
           value="Select"
           ng-disabled="!allFieldsEntered()"
           ng-click="submit()">
  </div>

</div>

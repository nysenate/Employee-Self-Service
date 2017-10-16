<div>
  <h4 class="content-info">Enter origin and destination(s)</h4>
  <div class="grid text-align-center">
    <div class="col-6-12">
      <h4>Departure (From)</h4>
      <div class="padding-10">
        <input travel-autocomplete callback="setAddress(address)" placeholder="Enter Origin Address" type="text" size="30">
      </div>
    </div>
    <div class="col-6-12">
      <h4>Destination (To)</h4>
      <div class="padding-10">
        <input type="button" class="submit-button"
               value="Set Destination"
               ng-click="addDestination()">
      </div>
    </div>
  </div>

  <div modal-container>
    <modal modal-id="origin-selection-modal">
      <div origin-selection-modal></div>
    </modal>
  </div>
</div>

<div>
  <h2 class="content-info">Origin and Destination</h2>
  <div class="grid text-align-center">
    <div class="col-6-12">
      <h4>Origin</h4>
      <div class="padding-10">
        <input type="button" class="submit-button"
               value="Set Origin"
               ng-click="setOrigin()">
      </div>
    </div>
    <div class="col-6-12">
      <h4>Destination(s)</h4>
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

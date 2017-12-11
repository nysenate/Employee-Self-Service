<div class="content-container text-align-center padding-bottom-10">
  <div>
    <h4 class="content-info">Departure (From)</h4>
    <p>
      Enter your departing address.
    </p>
    <div class="margin-10">
      <input travel-address-autocomplete callback="setOrigin(address)" placeholder="200 State St, Albany NY 12210" type="text" size="45">
    </div>
  </div>
  <div class="margin-top-20">
    <input type="button" class="neutral-button" value="Back"
           ng-click="originCallback(origin, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="originCallback(origin, ACTIONS.NEXT)">
  </div>
</div>
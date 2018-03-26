<div class="content-container text-align-center">
  <div>
    <h1 class="content-info">Departure (From)</h1>
    <p class="margin-top-20">
      Enter your departing address.
    </p>
    <div class="margin-10">
      <%-- This ng-if forces the travel-address-autocomplte to be reinitialized whenever returning to the origin page. --%>
      <%-- Reinitialization is necessary for input element to be set with the current origin address. --%>
      <input travel-address-autocomplete
             ng-if="pageState === STATES.ORIGIN"
             callback="setOrigin(address)"
             address="{{origin.formattedAddress}}"
             placeholder="200 State St, Albany NY 12210"
             type="text"
             size="45">

    </div>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Back"
           ng-click="originCallback(origin, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="originCallback(origin, ACTIONS.NEXT)">
  </div>
</div>
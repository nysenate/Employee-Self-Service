<div class="content-container text-align-center">
  <div>
    <h1 class="content-info">Destination (To)</h1>
    <p class="margin-top-20">
      Add all destinations for your trip.
    </p>
    <div ng-repeat="dest in destinations" class="margin-10">
      <div style="width: 80%; display:inline-block;">
        <travel-destination-directive destination="dest"></travel-destination-directive>
      </div>
      <span ng-click="editDestination(dest)" class="icon-edit margin-10" title="Edit destination"></span>
      <span ng-click="removeDestination(dest)" class="icon-trash margin-10" title="Delete destination"></span>
    </div>

    <div class="clearfix width-100 margin-top-20">
      <input type="button" class="submit-button"
             value="Add Destination"
             ng-click="addDestination()">
    </div>
  </div>

  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Back"
           ng-click="destinationCallback(destinations, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="destinationCallback(destinations, ACTIONS.NEXT)">
  </div>
</div>
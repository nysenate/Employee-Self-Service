<div class="content-container text-align-center padding-bottom-10">
  <div>
    <h4 class="content-info">Destination (To)</h4>
    <p>
      Add all destinations of your trip.
    </p>
    <div ng-repeat="dest in destinations">
      <div style="width: 80%; margin: 20px; display:inline-block;">
        <travel-destination-directive destination="dest"></travel-destination-directive>
      </div>
      <span ng-click="editDestination(dest)" class="icon-edit margin-10" title="Edit destination"></span>
      <span ng-click="removeDestination(dest)" class="icon-trash margin-10" title="Delete destination"></span>
    </div>

    <div class="clearfix width-100">
      <input type="button" class="submit-button"
             value="Add Destination"
             ng-click="addDestinationOnClick()">
    </div>
  </div>

  <div class="margin-top-20">
    <input type="button" class="neutral-button" value="Back"
           ng-click="destinationCallback(destinations, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="destinationCallback(destinations, ACTIONS.NEXT)">
  </div>
</div>
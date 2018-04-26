<div class="content-container text-align-center">
  <div>
    <h1 class="content-info">Purpose of Travel</h1>
    <p class="margin-top-20">
      Enter the purpose of your travel.
    </p>
    <textarea ng-model="purposeOfTravel" cols="80" rows="6" placeholder="Why will you be traveling?"></textarea>
  </div>

  <div class="margin-10">
    <form method="POST" enctype="multipart/form-data">
      <input type="file" id="file" name="file" multiple>
      <input type="submit" ng-click="save()">
    </form>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Cancel"
           ng-click="purposeCallback(purposeOfTravel, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-disabled="purposeOfTravel.length == 0"
           ng-click="purposeCallback(purposeOfTravel, ACTIONS.NEXT)">
  </div>
</div>
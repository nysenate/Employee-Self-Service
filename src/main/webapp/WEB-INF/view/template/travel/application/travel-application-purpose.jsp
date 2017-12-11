<div class="content-container text-align-center padding-bottom-10">
  <div>
    <h4 class="content-info">Purpose of Travel</h4>
    <p>
      Enter the purpose of your travel.
    </p>
    <textarea ng-model="purposeOfTravel" cols="80" rows="6" placeholder="Why will you be traveling?"></textarea>
  </div>
  <div class="margin-top-20">
    <input type="button" class="neutral-button" value="Back"
           ng-click="purposeCallback(purposeOfTravel, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="purposeCallback(purposeOfTravel, ACTIONS.NEXT)">
  </div>
</div>
<div>

  <div class="travel-card">

  <ess-app-form-body app="app"></ess-app-form-body>

    <div class="travel-card-item">
      <h1 class="">Driving Route</h1>
      <div id="map" class="margin-top-20"
           style="width: 650px; height: 375px; margin-left: auto; margin-right: auto;"></div>
    </div>

    <div class="travel-button-container">
      <button type="button" class="travel-primary-btn"
              ng-click="back()">
        Back
      </button>
      <button class="travel-neutral-btn"
              ng-if="mode === 'EDIT' || mode === 'RESUBMIT'"
              type="button"
              ng-value="::negativeLabel || 'Cancel'"
              ng-click="cancel()">
        {{::negativeLabel || 'Cancel'}}
      </button>
      <button class="travel-teal-btn"
              ng-if="mode === 'NEW'"
              type="button"
              ng-click="save()">
        Save
      </button>
      <button type="submit" class="travel-submit-btn"
              ng-click="next()">
        {{::positiveBtnLabel}}
      </button>
    </div>

  </div>
</div>

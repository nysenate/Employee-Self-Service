<div class="content-container">
  <p class="travel-content-info travel-text-bold" ng-bind="::title"></p>

  <form name="overridesForm" id="overridesForm"
        ng-submit="overridesForm.$valid && next()" novalidate>

    <div ng-show="overridesForm.$submitted && !overridesForm.$valid" class="margin-10">
      <ess-notification level="error"
                        message="Error. Overrides must be >= 0 and be in increments of 0.01"></ess-notification>
    </div>

    <ess-travel-inner-container title="Expense Overrides">
      <p class="travel-text" style="magin-bottom: 10px;">
        If you wish to override the automatically calculated expenses, enter a value here.
      </p>
      <div class="text-align-center">
        <div style="display: flex; flex-direction: column;">

          <div class="perdiem-overrides-edit-form-row">
            <div class="perdiem-overrides-edit-form-calculated">
              Calculated Mileage: {{::dirtyAmendment.mileagePerDiems.totalPerDiem | currency}}
            </div>
            <div class="perdiem-overrides-edit-form-override">
              <label>Mileage Override $</label>
              <input ng-model="dirtyAmendment.mileagePerDiems.overrideRate"
                     type="number" step="0.01" min="0">
            </div>
          </div>

          <div class="perdiem-overrides-edit-form-row">
            <div class="perdiem-overrides-edit-form-calculated">
              Calculated Meals: {{::dirtyAmendment.mealPerDiems.totalPerDiem | currency}}
            </div>
            <div class="perdiem-overrides-edit-form-override">
              <label>Meals Override $</label>
              <input ng-model="mealOverrideRate" type="number" step="0.01" min="0">
            </div>
          </div>

          <div class="perdiem-overrides-edit-form-row">
            <div class="perdiem-overrides-edit-form-calculated">
              Calculated Lodging: {{::dirtyAmendment.lodgingPerDiems.totalPerDiem | currency}}
            </div>
            <div class="perdiem-overrides-edit-form-override">
              <label>Lodging Override $</label>
              <input ng-model="lodgingOverrideRate" type="number" step="0.01" min="0">
            </div>
          </div>

        </div>
      </div>
    </ess-travel-inner-container>

    <div class="travel-button-container">
      <input type="button" class="neutral-button" ng-value="::negativeLabel || 'Cancel'"
             ng-click="cancel()">
      <input type="button" class="travel-neutral-button" value="Back"
             title="Back"
             ng-click="back()">
      <input type="submit" class="submit-button"
             title="Save Overrides and Continue" value="Save Overrides">
    </div>
  </form>
</div>
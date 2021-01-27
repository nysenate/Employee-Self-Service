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
        You may override the automatically calculated expenses below.
      </p>

      <div>
        <div ng-repeat="perdiem in perdiems"
             class="margin-10"
             style="display:flex; justify-content: center; align-items: center;">
          <input type="checkbox"
                 ng-model="perdiem.isOverridden"
          ng-change="onCheckboxChange(perdiem)">
          <label style="width: 110px; margin-left: 5px;">Override {{perdiem.name}}</label>
          <span style="width: 10px;">$</span>
          <input type="number" step="0.01", min="0"
                 ng-class="{'disabled': !perdiem.isOverridden}"
                 ng-disabled="!perdiem.isOverridden"
                 ng-model="perdiem.overrideRate"
                 style="width: 80px;">
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
             title="Save Overrides and Continue" value="Next">
    </div>
  </form>
</div>
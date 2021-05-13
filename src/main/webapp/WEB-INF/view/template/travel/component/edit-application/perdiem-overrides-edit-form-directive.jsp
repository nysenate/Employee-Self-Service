<div>

  <div ng-show="overridesForm.$submitted && !overridesForm.$valid" class="margin-10">
    <ess-notification level="error"
                      message="Error. Overrides must be >= 0 and be in increments of 0.01"></ess-notification>
  </div>

  <div class="travel-card">
    <form name="overridesForm" id="overridesForm"
          ng-submit="overridesForm.$valid && next()" novalidate>
      <div class="travel-card-item">
        <h3 class="travel-title">Expense Overrides</h3>
        <span class="travel-instructions">You may override the automatically calculated expenses below.</span>
        <div>
          <div ng-repeat="perdiem in perdiems"
               class="margin-10"
               style="display:flex; justify-content: center; align-items: center;">
            <input type="checkbox"
                   class="travel-input"
                   ng-model="perdiem.isOverridden"
                   ng-change="onCheckboxChange(perdiem)">
            <label style="width: 110px; margin-left: 5px;">Override {{perdiem.name}}</label>
            <span style="width: 10px;">$</span>
            <input type="number" step="0.01" , min="0"
                   class="travel-input"
                   ng-class="{'disabled': !perdiem.isOverridden}"
                   ng-disabled="!perdiem.isOverridden"
                   ng-model="perdiem.overrideRate"
                   style="width: 80px;">
          </div>
        </div>
      </div>

      <div class="travel-button-container">
        <button type="button" class="travel-neutral-btn"
               ng-click="cancel()">
          {{::negativeLabel || 'Cancel'}}
        </button>
        <button type="button" class="travel-neutral-btn"
               ng-click="back()">
          Back
        </button>
        <button type="submit" class="travel-primary-btn">
          Next
        </button>
      </div>
    </form>
  </div>
</div>
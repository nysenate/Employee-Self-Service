<div class="step-indicator">
  <span class="step completed"
        ng-class="{navigable: stateService.isPurposeNavigable()}"
        ng-click="stateService.isPurposeNavigable() && stateService.setPurposeState()">
    Purpose
  </span>
  <span class="step"
        ng-class="{completed: stateService.isOutboundState() || stateService.isOutboundNavigable(), navigable: stateService.isOutboundNavigable()}"
        ng-click="stateService.isOutboundNavigable() && stateService.setOutboundState()">
    Route
  </span>
  <span class="step"
        ng-class="{completed: stateService.isAllowancesState() || stateService.isAllowancesNavigable(), navigable: stateService.isAllowancesNavigable()}"
        ng-click="stateService.isAllowancesNavigable() && stateService.setAllowancesState()">
    Expenses
  </span>
  <span class="step"
        ng-class="{completed: stateService.isReviewState()}">
    Overrides
  </span>
</div>

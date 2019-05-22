<div class="step-indicator">
  <span class="step completed"
        ng-class="{navigable: stateService.isPurposeNavigable()}"
        ng-click="stateService.isPurposeNavigable() && stateService.setPurposeState()">
    Purpose
  </span>
  <span class="step"
        ng-class="{completed: stateService.isOutboundState() || stateService.isOutboundNavigable(), navigable: stateService.isOutboundNavigable()}"
        ng-click="stateService.isOutboundNavigable() && stateService.setOutboundState()">
    Outbound
  </span>
  <span class="step"
        ng-class="{completed: stateService.isReturnState() || stateService.isReturnNavigable(), navigable: stateService.isReturnNavigable()}"
        ng-click="stateService.isReturnNavigable() && stateService.setReturnState()">
    Return
  </span>
  <span class="step"
        ng-class="{completed: stateService.isAllowancesState() || stateService.isAllowancesNavigable(), navigable: stateService.isAllowancesNavigable()}"
        ng-click="stateService.isAllowancesNavigable() && stateService.setAllowancesState()">
    Expenses
  </span>
  <span class="step"
        ng-class="{completed: stateService.isReviewState()}">
    Review
  </span>
</div>

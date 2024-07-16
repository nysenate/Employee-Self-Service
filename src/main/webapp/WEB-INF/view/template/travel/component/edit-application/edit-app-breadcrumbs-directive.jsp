<div class="step-indicator">
  <span class="step"
        ng-class="{active: stateService.isPurposeState(),
                   completed: stateService.isOutboundState() || stateService.isOutboundNavigable(),
                   navigable: stateService.isPurposeNavigable()}"
        ng-click="stateService.isPurposeNavigable() && stateService.setPurposeState()">
    Purpose
  </span>
  <span class="step"
        ng-class="{active: stateService.isOutboundState(),
                   completed: stateService.isReturnState() || stateService.isReturnNavigable(),
                   navigable: stateService.isOutboundNavigable()}"
        ng-click="stateService.isOutboundNavigable() && stateService.setOutboundState()">
    Outbound
  </span>
  <span class="step"
        ng-class="{active: stateService.isReturnState(),
                   completed: stateService.isAllowancesState() || stateService.isAllowancesNavigable(),
                   navigable: stateService.isReturnNavigable()}"
        ng-click="stateService.isReturnNavigable() && stateService.setReturnState()">
    Return
  </span>
  <span class="step"
        ng-class="{active: stateService.isAllowancesState(),
                   completed: stateService.isOverridesState() || stateService.isOverridesNavigable(),
                   navigable: stateService.isAllowancesNavigable()}"
        ng-click="stateService.isAllowancesNavigable() && stateService.setAllowancesState()">
    Expenses
  </span>
  <span class="step"
        ng-class="{active: stateService.isOverridesState(),
                   completed: stateService.isReviewState() || stateService.isReviewNavigable(),
                   navigable: stateService.isOverridesNavigable()}"
        ng-click="stateService.isOverridesNavigable() && stateService.setOverridesState()">
    Overrides
  </span>
  <span class="step"
        ng-class="{active: stateService.isReviewState()}">
    Review
  </span>
</div>

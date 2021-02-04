<div ng-controller="ReviewTravelAppCtrl as vm">
  <div>
    <div class="travel-hero">
      <h2>Review Travel Applications</h2>
    </div>
    <div class="content-container content-controls">
      <h4 class="travel-content-info travel-text-bold" style="margin: 0;">The following travel applications require your
        review.</h4>
      <h4 style="display: inline;">Active Role:</h4>
      <select style="margin: 10px;"
              ng-model="vm.activeRole"
              ng-options="role.label for role in vm.userRoles"
              ng-change="vm.onActiveRoleChange()">hello</select>
    </div>
  </div>

  <div ng-if="vm.isLoading === false">
    <div ng-if="vm.reviews.toReview.length === 0">
      <div class="content-container">
        <div class="content-info">
          <h2 class="dark-gray">No Applications to Review.</h2>
        </div>
      </div>
    </div>

    <div ng-if="vm.reviews.toReview.length > 0">
      <ess-app-review-summary-table
          title="Applications to Review"
          reviews="vm.reviews.toReview"
          active-role="vm.activeRole"
          on-row-click="vm.onRowClick(review)">
      </ess-app-review-summary-table>
    </div>
  </div>

  <div ng-if="vm.isLoading === false">
    <div ng-if="vm.reviews.shared.length > 0 && vm.activeRole.canViewShared">
      <ess-app-review-summary-table
          title="Shared Applications"
          reviews="vm.reviews.shared"
          active-role="vm.activeRole"
          on-row-click="vm.onRowClick(review)">
      </ess-app-review-summary-table>
    </div>
  </div>

  <div modal-container>
    <modal modal-id="app-review-action-modal">
      <div app-review-action-modal></div>
    </modal>

    <modal modal-id="app-review-approve-confirm-modal">
      <div app-review-approve-confirm-modal></div>
    </modal>

    <modal modal-id="app-review-disapprove-confirm-modal">
      <div app-review-disapprove-confirm-modal></div>
    </modal>
  </div>

</div>

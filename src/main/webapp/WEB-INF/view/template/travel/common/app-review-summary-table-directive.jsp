<div class="content-container">
  <div ng-show="reviews.length > 0">
    <h1 ng-if="title" style="border-color: #ccc;">
      {{title}}
    </h1>

    <div class="padding-10">
      <table class="travel-table travel-hover">
        <thead>
        <tr>
          <th>Travel Date</th>
          <th>Employee</th>
          <th>Destination</th>
          <th>Allotted Funds</th>
          <th ng-if="options.showAction">Last Action</th>
        </tr>
        </thead>

        <tbody>
        <tr dir-paginate="review in reviews | orderBy: 'travelApplication.startDate' | itemsPerPage : 10"
            pagination-id="travel-table-pagination"
            ng-click="onRowClick({review: review})"
            ng-class="{'shared-review': (review.isShared && activeRole.name && activeRole.name !== review.nextReviewerRole) }">

          <td ng-bind="::review.travelApplication.activeAmendment.startDate | date:'M/d/yyyy'"></td>
          <td ng-bind="::review.travelApplication.traveler.fullName"></td>
          <td ng-bind="::review.travelApplication.activeAmendment.destinationSummary"></td>
          <td ng-bind="::review.travelApplication.activeAmendment.totalAllowance | currency"></td>
          <td ng-if="options.showAction" ess-review-action-status="userAction(review)"></td>

        </tr>
        </tbody>
      </table>

      <div>
        <dir-pagination-controls class="text-align-center" pagination-id="travel-table-pagination"
                                 boundary-links="true" max-size="10"></dir-pagination-controls>
      </div>

    </div>
  </div>
</div>

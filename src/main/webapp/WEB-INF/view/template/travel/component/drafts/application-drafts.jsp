<div ng-controller="DraftsCtrl as vm">
  <div class="travel-hero">
    <h2>Travel Application Drafts</h2>
  </div>
  <div class="content-container travel-content-controls">
    <h4 class="travel-content-info travel-text-bold">
      Continue work on a saved draft.
    </h4>
  </div>

  <div class="content-container">
    <div loader-indicator ng-if="vm.draftsRequest.$resolved === false"></div>

    <div ng-if="vm.draftsRequest.$resolved === true">
      <div class="content-info" ng-show="vm.drafts.length == 0">
        <h2 class="dark-gray">
          No drafts found.
        </h2>
      </div>
      <div ng-show="vm.drafts.length > 0">
        <div class="padding-10">

          <table class="travel-table travel-hover">
            <thead>
            <tr>
              <th>Travel Date</th>
              <th>Traveler</th>
              <th>Destination</th>
              <th>Allotted Funds</th>
              <th>Updated Date Time</th>
              <th></th>
            </tr>
            </thead>

            <tbody>
            <tr dir-paginate="draft in vm.drafts | orderBy: '-updatedDateTime' | itemsPerPage : 10"
                pagination-id="travel-table-pagination">

              <td ng-bind="::draft.amendment.startDate || 'N/A' | date:'M/d/yyyy'" ng-click="vm.onRowClick(draft)"></td>
              <td ng-bind="::draft.traveler.fullName" ng-click="vm.onRowClick(draft)"></td>
              <td ng-bind="::draft.amendment.destinationSummary || 'N/A'" ng-click="vm.onRowClick(draft)"></td>
              <td ng-bind="::draft.amendment.totalAllowance | currency" ng-click="vm.onRowClick(draft)"></td>
              <td ng-bind="::draft.updatedDateTime | date:'M/d/yyyy h:mm a'" ng-click="vm.onRowClick(draft)"></td>
              <td class="icon-trash" ng-click="vm.onDelete(draft)"></td>
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
  </div>

  <div modal-container>
    <modal modal-id="draft-delete-confirmation">
      <div delete-draft-modal></div>
    </modal>
  </div>
</div>
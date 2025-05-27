<div class="confirm-modal">
  <h3 class="content-info">
    Confirm Deletion
  </h3>
  <div class="confirmation-message">
    <h4>
      Delete this Draft?
    </h4>
    <div class="padding-top-10">
      <table class="">
        <body>
        <tr>
          <td>Travel Date</td>
          <td ng-bind="::draft.amendment.startDate || 'N/A' | date:'M/d/yyyy'"></td>
        </tr>
        <tr>
          <td>Traveler</td>
          <td ng-bind="::draft.traveler.fullName"></td>
        </tr>
        <tr>
          <td>Destination</td>
          <td ng-bind="::draft.amendment.destinationSummary || 'N/A'"></td>
        </tr>
        <tr>
          <td>Allotted Funds</td>
          <td ng-bind="::draft.amendment.totalAllowance | currency"></td>
        </tr>
        <tr>
          <td style="padding-right: 20px;">Updated Date Time</td>
          <td ng-bind="::draft.updatedDateTime | date:'M/d/yyyy h:mm a'"></td>
        </tr>
        </body>
      </table>
    </div>
    <div style="padding-top: 20px;">
      <input type="button"
             class="travel-reject-btn"
             value="Delete"
             ng-click="onDelete()">
      <input type="button"
             class="travel-neutral-btn"
             value="Cancel"
             ng-click="onCancel()">
    </div>
  </div>
</div>
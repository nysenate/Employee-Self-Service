<div ng-controller="TravelReviewCtrl">
  <div>
    <div class="travel-hero">
      <h2>Review Travel Applications</h2>
    </div>
    <div class="content-container content-controls">
      <div class="padding-10 text-align-center">
        The following travel applications require your review.
      </div>
    </div>
  </div>

  <div ng-if="data.apiRequest.$resolved === true">
    <div ng-if="data.apps.length === 0">
      <div class="content-container">
        <div class="content-info">
          <h2 class="dark-gray">No Applications to Review.</h2>
        </div>
      </div>
    </div>

    <div ng-if="data.apps.length > 0">
      <travel-application-table
          apps="data.apps"
          on-row-click="viewApplicationForm(app)">>
      </travel-application-table>
    </div>
  </div>

  <div modal-container>
    <modal modal-id="travel-form-modal">
      <div travel-form-modal></div>
    </modal>
  </div>

</div>

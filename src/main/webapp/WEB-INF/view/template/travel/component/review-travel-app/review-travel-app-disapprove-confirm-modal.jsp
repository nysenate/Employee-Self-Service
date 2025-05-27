<div class="content-container no-top-margin padding-top-5">
  <h3 class="content-info">
    Disapproval Confirmation
  </h3>

  <form name="disapprovalConfirmForm" id="disapprovalConfirmForm"
        ng-submit="disapprovalConfirmForm.$valid && disapprove()" novalidate>
    <div style="margin-left: 20px; margin-right: 20px;">
      <p>
        You are about to disapprove a Travel Application for
        <span ng-bind="::appReview.travelApplication.traveler.fullName"></span>
        <span ng-show="isSingleDayTravel">
          on <span ng-bind="::appReview.travelApplication.activeAmendment.startDate | date: 'shortDate'"></span>.
        </span>
        <span ng-show="!isSingleDayTravel">
      from <span ng-bind="::appReview.travelApplication.activeAmendment.startDate | date: 'shortDate'"></span>
      to <span ng-bind="::appReview.travelApplication.activeAmendment.endDate | date: 'shortDate'"></span>.
      </span>
      </p>

      <p>
        Please explain the disapproval to the traveler.
      </p>

      <div ng-show="disapprovalConfirmForm.$submitted && !disapprovalConfirmForm.$valid" class="margin-20">
        <ess-notification level="error" message="A note is required when disapproving."></ess-notification>
      </div>

      <div class="margin-top-20">
        <label>Disapproval Reason:
          <textarea class="travel-input" ng-model="notes" cols="40" rows="3" required></textarea>
        </label>
      </div>
    </div>

    <div class="travel-button-container">
      <button type="submit" class="travel-reject-btn"
             title="Disapprove Application">
        Disapprove
      </button>
      <button type="button" class="travel-neutral-btn"
             title="Cancel Disapproval" ng-click="cancel()">
        Cancel
      </button>
    </div>
  </form>
</div>

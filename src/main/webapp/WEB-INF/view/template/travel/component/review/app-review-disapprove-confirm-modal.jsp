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
      on <span ng-bind="::appReview.travelApplication.startDate | date: 'shortDate'"></span>.
      </span>
        <span ng-show="!isSingleDayTravel">
      from <span ng-bind="::appReview.travelApplication.startDate | date: 'shortDate'"></span>
      to <span ng-bind="::appReview.travelApplication.endDate | date: 'shortDate'"></span>.
      </span>
      </p>

      <div ng-show="disapprovalConfirmForm.$submitted && !disapprovalConfirmForm.$valid" class="margin-20">
        <ess-notification level="error" message="A note is required when disapproving."></ess-notification>
      </div>

      <div class="margin-top-20">
        <label>Disapproval Notes:
          <textarea ng-model="notes" cols="40" rows="3" required></textarea>
        </label>
      </div>
    </div>

    <div class="travel-button-container">
      <input type="submit" class="reject-button" value="Disapprove Application"
             title="Disapprove Application">
      <input type="button" class="neutral-button" value="Cancel Disapproval"
             title="Cancel Disapproval" ng-click="cancel()">
    </div>
  </form>
</div>

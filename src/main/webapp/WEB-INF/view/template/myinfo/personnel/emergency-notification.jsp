<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="EmergencyNotificationCtrl" id="emergency-notification-page">

  <div class="my-info-hero">
    <h2>Emergency Notification Info</h2>
  </div>

  <div loader-indicator class="loader" ng-show="isLoading()"></div>

  <div class="content-container" ng-if="!isLoading()">
    <p class="content-info">
      The following contact information will be used to reach you in the event of a Senate wide emergency.
    </p>
    <h3 class="eni-saved-message" ng-class="{'eni-saved': state.saved}">
      Emergency Notification Info Saved
    </h3>
    <form class="eni-form grid" name="eniForm">
      <h3 class="col-1-4">Phone</h3>
      <div class="content-controls col-9-12">
        <label>Work</label>
        <span class="immutable-value" ng-bind="state.eni.workPhone"></span>

        <label for="home-phone">Home</label>
        <input type="text" id="home-phone" autocomplete="home tel-national"
               ng-pattern="telPattern" ng-model="state.eni.homePhone">
        <p class="eni-error-text" ng-bind="phoneErrorMsg"></p>

        <label for="alternate-phone">Alternate</label>
        <input type="text" id="alternate-phone" autocomplete="off"
               ng-pattern="telPattern" ng-model="state.eni.alternatePhone">
        <p class="eni-error-text" ng-bind="phoneErrorMsg"></p>

        <label for="mobile-phone">Mobile</label>
        <input type="text" id="mobile-phone" autocomplete="mobile tel-national"
               ng-pattern="telPattern" ng-model="state.eni.mobilePhone">
        <p class="eni-error-text" ng-bind="phoneErrorMsg"></p>

        <div class="check-box-container">
          <input type="checkbox" id="text-alerts"
                 ng-model="state.eni.smsSubscribed"
                 ng-disabled="!state.eni.mobilePhone">
          <label for="text-alerts">Receive Texts on Mobile</label>
        </div>
      </div>

      <h3 class="col-1-4">Email</h3>
      <div class="content-controls col-9-12">
        <label>Work</label>
        <span class="immutable-value" ng-bind="state.eni.workEmail"></span>

        <label for="personal-email">Personal</label>
        <input type="email" id="personal-email" autocomplete="mobile email"
               ng-model="state.eni.personalEmail">
        <p class="eni-error-text" ng-bind="emailErrorMsg"></p>

        <label for="alternate-email">Alternate</label>
        <input type="email" id="alternate-email" autocomplete="off"
               ng-model="state.eni.alternateEmail">
        <p class="eni-error-text" ng-bind="emailErrorMsg"></p>
      </div>
      <div class="input-container push-1-4 col-9-12">
        <input type="button" class="submit-button"
               title="Save Emergency Notification Info" value="Save"
               ng-click="saveENI()"
               ng-disabled="eniForm.$invalid || eniForm.$pristine">
      </div>
    </form>
  </div>

  <div modal-container></div>
</section>

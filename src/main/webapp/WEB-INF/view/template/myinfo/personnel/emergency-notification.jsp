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
    <form class="eni-form" name="eniForm">
      <h3>Phone</h3>
      <div class="content-controls">
        <label>Work</label>
        <span class="immutable-value" ng-bind="state.eni.workPhone"></span>

        <label for="home-phone">Home</label>
        <input type="text" id="home-phone"
               ng-pattern="telPattern" ng-model="state.eni.homePhone">

        <label for="alternate-phone">Alternate</label>
        <input type="text" id="alternate-phone"
               ng-pattern="telPattern" ng-model="state.eni.alternatePhone">

        <label for="mobile-phone">Mobile</label>
        <input type="text" id="mobile-phone"
               ng-pattern="telPattern" ng-model="state.eni.mobilePhone">

        <div></div>

        <div>
          <label for="text-alerts">Receive Texts on Mobile</label>
          <input type="checkbox" id="text-alerts" ng-model="state.eni.smsSubscribed">
        </div>
      </div>

      <h3>Email</h3>
      <div class="content-controls">
        <label>Work</label>
        <span class="immutable-value" ng-bind="state.eni.workEmail"></span>

        <label for="personal-email">Personal</label>
        <input type="email" id="personal-email" ng-model="state.eni.personalEmail">

        <label for="alternate-email">Alternate</label>
        <input type="email" id="alternate-email" ng-model="state.eni.alternateEmail">
      </div>
      <div class="input-container">
        <input type="button" class="submit-button"
               title="Save Emergency Notification Info" value="Save"
               ng-click="saveENI()"
               ng-disabled="eniForm.$invalid || eniForm.$pristine">
      </div>
    </form>
  </div>

  <div modal-container></div>
</section>

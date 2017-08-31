<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AlertCtrl" id="alert-info-page">

  <div class="my-info-hero">
    <h2>Emergency Alert Info</h2>
  </div>

  <div loader-indicator class="loader" ng-show="isLoading()"></div>

  <div class="content-container" ng-if="!isLoading()">
    <p class="content-info">
      The following contact information will be used to reach you in the event of a Senate-wide emergency.
    </p>
    <h3 class="alert-info-saved-message" ng-class="{'alert-info-saved': state.saved}">
      Alert Info Saved
    </h3>
    <form class="alert-info-form grid" name="alertInfoForm">
      <h3 class="col-1-4">Phone</h3>
      <div class="content-controls col-9-12">
        <label>Work</label>
        <span class="immutable-value" ng-bind="state.alertInfo.workPhone"></span>

        <label for="home-phone">Home</label>
        <input type="text" id="home-phone" autocomplete="home tel-national"
               ng-pattern="telPattern" ng-model="state.alertInfo.homePhone">
        <p class="alert-info-error-text" ng-bind="phoneErrorMsg"></p>

        <label for="alternate-phone">Alternate</label>
        <input type="text" id="alternate-phone" autocomplete="off"
               ng-pattern="telPattern" ng-model="state.alertInfo.alternatePhone">
        <p class="alert-info-error-text" ng-bind="phoneErrorMsg"></p>

        <label for="mobile-phone">Mobile</label>
        <input type="text" id="mobile-phone" autocomplete="mobile tel-national"
               ng-pattern="telPattern" ng-model="state.alertInfo.mobilePhone">
        <p class="alert-info-error-text" ng-bind="phoneErrorMsg"></p>

        <label></label>
        <div class="check-box-container"
             ng-class="{'ng-invalid': !validMobileOptions()}">
          <input type="checkbox" id="mobile-callable"
                 ng-model="state.alertInfo.mobileCallable"
                 ng-disabled="!state.alertInfo.mobilePhone">
          <label for="mobile-callable">Receive Calls on Mobile</label>
          <br>
          <input type="checkbox" id="mobile-textable"
                 ng-model="state.alertInfo.mobileTextable"
                 ng-disabled="!state.alertInfo.mobilePhone">
          <label for="mobile-textable">Receive Texts on Mobile</label>
        </div>
        <p class="alert-info-error-text">
          You must receive calls and/or texts<br>
          if a mobile number is provided.
        </p>
      </div>

      <div class="push-1-4 col-9-12 alert-info-error-container">
        <p ng-class="{'visibility-hidden': noDuplicatePhoneNumbers()}">
          Please remove duplicate phone numbers.
        </p>
      </div>

      <h3 class="col-1-4">Email</h3>
      <div class="content-controls col-9-12">
        <label>Work</label>
        <span class="immutable-value" ng-bind="state.alertInfo.workEmail"></span>

        <label for="personal-email">Personal</label>
        <input type="email" id="personal-email" autocomplete="mobile email"
               ng-model="state.alertInfo.personalEmail">
        <p class="alert-info-error-text" ng-bind="emailErrorMsg"></p>

        <label for="alternate-email">Alternate</label>
        <input type="email" id="alternate-email" autocomplete="off"
               ng-model="state.alertInfo.alternateEmail">
        <p class="alert-info-error-text" ng-bind="emailErrorMsg"></p>
      </div>

      <div class="push-1-4 col-9-12 alert-info-error-container">
        <p ng-class="{'visibility-hidden': noDuplicateEmails()}">
          Please remove duplicate email addresses.
        </p>
      </div>

      <div class="input-container push-1-4 col-9-12">
        <input type="button" class="submit-button"
               title="Save Alert Info" value="Save"
               ng-click="saveAlertInfo()"
               ng-disabled="alertInfoForm.$invalid || alertInfoForm.$pristine || !validAlertInfo()">
      </div>
    </form>
  </div>

  <div modal-container></div>
</section>

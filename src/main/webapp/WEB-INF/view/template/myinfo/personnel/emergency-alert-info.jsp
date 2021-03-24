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
        <input type="text" name="homePhone" id="home-phone" autocomplete="home tel-national"
               ng-pattern="telPattern" ng-model="state.alertInfo.homePhone">
        <div ng-class="{'visibility-hidden': !alertInfoForm.homePhone.$error.pattern}"
             class="alert-info-error-text" ng-bind="phoneErrorMsg"></div>
        <label></label>

        <label for="alternate-phone">Alternate</label>
        <input type="text" name="alternatephone" id="alternate-phone" autocomplete="off"
               ng-pattern="telPattern" ng-model="state.alertInfo.alternatePhone">

        <div class="dropdown">
          <label for="alternateOptions"></label>
          <select id="alternateOptions"
                  ng-model="state.alertInfo.alternateOptions"
                  ng-disabled="!state.alertInfo.alternatePhone">
            <option ng-repeat="contactOption in CONTACT_OPTIONS">{{contactOption}}</option>
          </select>
        </div>
        <div ng-class="{'visibility-hidden': !alertInfoForm.alternatephone.$error.pattern}"
             class="alert-info-error-text" ng-bind="phoneErrorMsg"></div>

        <label for="mobile-phone">Mobile</label>
        <input type="text" name="mobilePhone" id="mobile-phone" autocomplete="mobile tel-national"
               ng-pattern="telPattern" ng-model="state.alertInfo.mobilePhone">

        <div class="dropdown">
        <label for="mobileOptions"></label>
            <select id="mobileOptions"
                    ng-model="state.alertInfo.mobileOptions"
                    ng-disabled="!state.alertInfo.mobilePhone">
              <option ng-repeat="contactOption in CONTACT_OPTIONS">{{contactOption}}</option>
            </select>
        </div>
        <div ng-class="{'visibility-hidden': !alertInfoForm.mobilePhone.$error.pattern}"
             class="alert-info-error-text" ng-bind="phoneErrorMsg"></div>
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
        <input type="email" name="personalEmail" id="personal-email" autocomplete="mobile email"
               ng-pattern="emailPattern"
               ng-model="state.alertInfo.personalEmail">
        <p ng-class="{'visibility-hidden': !alertInfoForm.personalEmail.$error.pattern}"
           class="alert-info-error-text" ng-bind="emailErrorMsg"></p>
        <label></label>

        <label for="alternate-email">Alternate</label>
        <input type="email" name="alternateEmail" id="alternate-email" autocomplete="off"
               ng-pattern="emailPattern"
               ng-model="state.alertInfo.alternateEmail">
        <p ng-class="{'visibility-hidden': !alertInfoForm.alternateEmail.$error.pattern}"
           class="alert-info-error-text" ng-bind="emailErrorMsg"></p>
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

  <div modal-container>
    <modal modal-id="invalid-email-dialog">
      <div confirm-modal
           title="Invalid Email Address">
        <p>
          A submitted email address was invalid:<br>
          {{errorData}}
        </p>
      </div>
    </modal>
  </div>
</section>

<div>
  <div ng-show="purpose.form.$submitted && !purpose.form.$valid" class="margin: 10px 0px;">
    <ess-notification level="error">
      <ul>
        <li ng-if="purpose.form.$error.departmentHeadRequired">
          A department head is required.
        </li>
        <li ng-if="purpose.form.$error.eventTypeRequired">
          A purpose of travel is required.
        </li>
        <li ng-if="purpose.form.$error.eventNameRequired">
          Name of the {{dirtyDraft.amendment.purposeOfTravel.eventType.displayName}} is required.
        </li>
        <li ng-if="purpose.form.$error.additionalPurposeRequired">
          A description of your purpose of travel is required.
        </li>
      </ul>
    </ess-notification>
  </div>

  <div loader-indicator class="loader" ng-show="isLoading"></div>
  <div class="travel-card" ng-show="!isLoading">
    <form ng-cloak name="purpose.form" id="purposeForm" novalidate>
      <div class="travel-card-item">
        <h1>Traveler Info</h1>
        <div class="padding-10">
          <div class="purpose-row">
            <label>Travel application on behalf of:</label>
            <span style="min-width: 200px;" class="padding-left-10">{{dirtyDraft.traveler.fullName}}</span>
          </div>
          <div class="purpose-row" ng-if="showDepartmentHead()">
            <label>Traveler Department Head:</label>
            <span class="padding-left-10">{{dirtyDraft.traveler.department.head.fullName}}</span>
          </div>
        </div>
      </div>
      <div class="travel-card-item">
        <h1 class="">Purpose of Travel</h1>
        <div class="padding-10">
          <div class="purpose-row">
            <label for="eventTypeSelect">Select your purpose of travel:</label>
            <select id="eventTypeSelect"
                    class="travel-input"
                    event-type-validator
                    ng-options="eventType as eventType.displayName for eventType in eventTypes track by eventType.name"
                    ng-model="dirtyDraft.amendment.purposeOfTravel.eventType"/>
          </div>
          <div ng-if="dirtyDraft.amendment.purposeOfTravel.eventType.requiresName" class="purpose-row">
            <label for="eventNameInput">Name of the
              {{dirtyDraft.amendment.purposeOfTravel.eventType.displayName}}:</label>
            <input id="eventNameInput" type="text" class="travel-input"
                   event-name-validator
                   ng-model="dirtyDraft.amendment.purposeOfTravel.eventName"/>
          </div>
          <div ng-if="dirtyDraft.amendment.purposeOfTravel.eventType !== null" class="purpose-row">
            <div ng-if="dirtyDraft.amendment.purposeOfTravel.eventType.requiresAdditionalPurpose">
              <label for="purposeAdditionalTextRequired">
                Enter your purpose of travel:
              </label>
              <textarea id="purposeAdditionalTextRequired"
                        ng-model="dirtyDraft.amendment.purposeOfTravel.additionalPurpose"
                        class="travel-input"
                        additional-purpose-validator
                        cols="120" rows="3"></textarea>
            </div>
            <div ng-if="!dirtyDraft.amendment.purposeOfTravel.eventType.requiresAdditionalPurpose">
              <label for="purposeAdditionalTextOptional">
                Provide additional information (<em>Optional</em>):
              </label>
              <textarea id="purposeAdditionalTextOptional"
                        ng-model="dirtyDraft.amendment.purposeOfTravel.additionalPurpose"
                        class="travel-input"
                        style="resize:vertical;"
                        cols="120" rows="3"></textarea>
            </div>
          </div>
        </div>
      </div>

      <div class="travel-card-item">
        <h1 class="">Supporting Documentation <em class="optional">(Optional)</em></h1>
        <div class="text-align-center padding-10">
          <span class="">You may attach any relevant supporting documentation.</span>
          <div ng-repeat="attachment in dirtyDraft.amendment.attachments" class="travel-attachment-container">
            <div class="travel-attachment-filename">
              {{attachment.originalName}}
              <span ng-click="deleteAttachment(attachment)" class="icon-cross" style="cursor: pointer;"></span>
            </div>
          </div>
          <div class="margin-20">
            <label for="addAttachment" class="travel-primary-btn">
              <i class="icon-upload margin-right-10"></i>Attach Document
            </label>
            <input type="file" value='fileInput' id="addAttachment" name="file" multiple style="display:none;">
          </div>
        </div>
      </div>

      <div class="travel-button-container">
        <button class="travel-neutral-btn"
                ng-if="mode === 'EDIT' || mode === 'RESUBMIT'"
                type="button"
                ng-value="::negativeLabel || 'Cancel'"
                ng-click="cancel()">
          {{::negativeLabel || 'Cancel'}}
        </button>
        <button class="travel-teal-btn"
                ng-if="mode === 'NEW'"
                type="button"
                ng-click="save()">
          Save
        </button>
        <button type="submit" class="travel-submit-btn"
                ng-click="next()">
          Next
        </button>
      </div>
    </form>
  </div>
</div>

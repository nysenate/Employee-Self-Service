<div class="content-container">
  <p class="travel-content-info travel-text-bold" ng-bind="::title"></p>

  <form name="purpose.form" id="purposeForm" novalidate>

    <div ng-show="purpose.form.$submitted && !purpose.form.$valid" class="margin-10">
      <ess-notification level="error" title="Purpose of Travel has errors">
        <ul>
          <li ng-if="purpose.form.$error.eventTypeRequired">A purpose of travel is required</li>
          <li ng-if="purpose.form.$error.eventNameRequired">Name of the {{dirtyApp.purposeOfTravel.eventType.displayName}} is required.</li>
          <li ng-if="purpose.form.$error.additionalPurposeRequired">A description of your purpose of travel is required.</li>
        </ul>
      </ess-notification>
    </div>

    <ess-travel-inner-container title="Purpose of Travel">
      <div class="">
        <div class="purpose-row">
          <label for="eventTypeSelect">Select your purpose of travel:</label>
          <select id="eventTypeSelect"
                  event-type-validator
                  ng-options="eventType as eventType.displayName for eventType in dirtyApp.purposeOfTravel.validEventTypes track by eventType.name"
                  ng-model="dirtyApp.purposeOfTravel.eventType"/>
        </div>
        <div ng-if="dirtyApp.purposeOfTravel.eventType.requiresName" class="purpose-row">
          <label for="eventNameInput">Name of the {{dirtyApp.purposeOfTravel.eventType.displayName}}:</label>
          <input id="eventNameInput" type="text"
                 event-name-validator
                 ng-model="dirtyApp.purposeOfTravel.eventName"/>
        </div>
        <div ng-if="dirtyApp.purposeOfTravel.eventType !== null" class="purpose-row">
          <div ng-if="dirtyApp.purposeOfTravel.eventType.requiresAdditionalPurpose">
            <label for="purposeAdditionalTextRequired">
              Enter your purpose of travel:
            </label>
            <textarea id="purposeAdditionalTextRequired" ng-model="dirtyApp.purposeOfTravel.additionalPurpose"
                      additional-purpose-validator
                      cols="120" rows="3"></textarea>
          </div>
          <div ng-if="!dirtyApp.purposeOfTravel.eventType.requiresAdditionalPurpose">
            <label for="purposeAdditionalTextOptional">
              Provide additional information (Optional):
            </label>
            <textarea id="purposeAdditionalTextOptional" ng-model="dirtyApp.purposeOfTravel.additionalPurpose"
                      cols="120" rows="3"></textarea>
          </div>
        </div>
      </div>
    </ess-travel-inner-container>

    <ess-travel-inner-container ng-if="false" title="Supporting Documentation">
      <div class="text-align-center">
        <div ng-repeat="attachment in app.attachments" class="travel-attachment-container">
          <div class="travel-attachment-filename">{{attachment.originalName}}
            <span ng-click="deleteAttachment(attachment)" class="icon-cross" style="cursor: pointer;"></span>
          </div>
        </div>
        <%--Cant have an inner form, do more testing to see if this form was necessary--%>
        <%--<form method="POST" enctype="multipart/form-data">--%>
        <%--Hack to change the button text of file input--%>
        <input class="neutral-button" type="button" id="addAttachmentDisplay" value="Add Attachment"
               onclick="document.getElementById('addAttachment').click();"/>
        <input type="file" id="addAttachment" name="file" multiple style="display:none;">
        <%--<input type="submit" ng-click="save()">--%>
        <%--</form>--%>
      </div>
    </ess-travel-inner-container>

    <div class="travel-button-container">
      <input type="button" class="reject-button" ng-value="::negativeLabel || 'Cancel'"
             ng-click="cancel()">
      <input type="submit" class="submit-button"
             title="Continue to next step" value="Next"
             ng-click="next()">
    </div>

  </form>
</div>

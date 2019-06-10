<div class="content-container">
  <p class="travel-content-info travel-text-bold" ng-bind="::title"></p>

  <form name="purposeForm" id="purposeForm"
  <%--Only call the callback function if form is valid--%>
        ng-submit="purposeForm.$valid && next()" novalidate>

    <div ng-show="purposeForm.$submitted && !purposeForm.$valid" class="margin-10">
      <ess-notification level="error" message="Purpose of Travel is required."></ess-notification>
    </div>

    <ess-travel-inner-container title="Purpose of Travel">
      <div class="text-align-center">
        <textarea ng-model="dirtyApp.purposeOfTravel" cols="80" rows="6"
                  placeholder="Why will you be traveling?" required></textarea>
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
      <input type="button" class="neutral-button" ng-value="::negativeLabel || 'Cancel'"
             ng-click="cancel()">
      <input type="submit" class="submit-button"
             title="Save purpose and continue" value="Save Purpose">
    </div>

  </form>
</div>

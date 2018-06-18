<div class="content-container">
  <p class="travel-content-info travel-text">
    Enter your purpose of travel and any supporting documentation.
  </p>

  <travel-inner-container title="Purpose of Travel">
    <div class="text-align-center">
      <textarea ng-model="data.purposeOfTravel" cols="80" rows="6"
                placeholder="Why will you be traveling?"></textarea>
    </div>
  </travel-inner-container>

  <travel-inner-container ng-if="false" title="Supporting Documentation">
    <div class="text-align-center">
      <div ng-repeat="attachment in app.attachments" class="travel-attachment-container">
        <div class="travel-attachment-filename">{{attachment.originalName}}
          <span ng-click="deleteAttachment(attachment)" class="icon-cross" style="cursor: pointer;"></span>
        </div>
      </div>
      <form method="POST" enctype="multipart/form-data">
        <%--Hack to change the button text of file input--%>
        <input class="neutral-button" type="button" id="addAttachmentDisplay" value="Add Attachment"
               onclick="document.getElementById('addAttachment').click();"/>
          <input type="file" id="addAttachment" name="file" multiple style="display:none;">
          <%--<input type="submit" ng-click="save()">--%>
      </form>
    </div>
  </travel-inner-container>

  <div class="travel-button-container">
    <input type="button" class="neutral-button" value="Cancel"
           ng-click="purposeCallback(ACTIONS.CANCEL)">
    <input type="button" class="submit-button"
           value="Next"
           ng-disabled="data.purposeOfTravel.length === 0"
           ng-click="purposeCallback(ACTIONS.NEXT, data.purposeOfTravel)">
  </div>
</div>

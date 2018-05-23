<div class="content-container text-align-center">
  <div>
    <h1 class="content-info">Purpose of Travel</h1>
    <p class="margin-top-20">
      Enter the purpose of your travel.
    </p>
    <textarea ng-model="purposeOfTravel" cols="80" rows="6" placeholder="Why will you be traveling?"></textarea>
  </div>

  <div class="margin-10">
    <div>
      <h1>Supporting Documentation</h1>
      <p>If necessary, attach any supporting documentation here.</p>
      <div ng-repeat="attachment in app.attachments" class="travel-attachment-container">
        <div class="travel-attachment-filename">{{attachment.originalName}}
          <span ng-click="deleteAttachment(attachment)" class="icon-cross"></span>
        </div>
      </div>
    </div>
    <div>
      <form method="POST" enctype="multipart/form-data">
        <%--Hack to change the button text of file input--%>
        <input class="neutral-button" type="button" id="addAttachmentDisplay" value="Add Attachment" onclick="document.getElementById('addAttachment').click();"/>
          <input type="file" id="addAttachment" name="file" multiple style="display:none;">
          <%--<input type="submit" ng-click="save()">--%>
      </form>
    </div>
  </div>
  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Cancel"
           ng-click="purposeCallback(purposeOfTravel, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-disabled="purposeOfTravel.length == 0"
           ng-click="purposeCallback(purposeOfTravel, ACTIONS.NEXT)">
  </div>
</div>
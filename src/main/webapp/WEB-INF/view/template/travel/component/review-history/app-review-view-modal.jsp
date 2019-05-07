<div class="content-container no-top-margin padding-top-5">

  <div>
    <ess-app-review-form-body app-review="appReview"></ess-app-review-form-body>
  </div>

  <div class="travel-button-container" style="margin-top: 5px !important;">
    <input type="button" class="travel-neutral-button" value="Close"
           ng-click="exit()">
    <a target="_blank" ng-href="${ctxPath}/travel/application/print?id={{app.id}}&print=true">Print</a>
  </div>
</div>
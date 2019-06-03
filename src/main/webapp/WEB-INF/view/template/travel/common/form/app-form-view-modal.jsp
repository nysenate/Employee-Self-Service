<div class="content-container no-top-margin padding-top-5">

  <div>
    <ess-app-form-body app="app"></ess-app-form-body>
  </div>

  <div class="travel-button-container">
    <a class="margin-10" target="_blank" ng-href="${ctxPath}/travel/application/print?id={{app.id}}&print=true">Print</a>
    <input type="button" class="travel-neutral-button" value="Close"
           ng-click="exit()">
  </div>
</div>
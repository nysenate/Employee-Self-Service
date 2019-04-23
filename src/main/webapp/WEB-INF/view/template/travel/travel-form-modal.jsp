<div class="content-container no-top-margin padding-top-5">

  <div>
    <travel-app-print-body app="app"></travel-app-print-body>
  </div>

  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="exit()">
    <a target="_blank" ng-href="${ctxPath}/travel/application/travel-application-print?id={{app.id}}&print=true">Print</a>
  </div>
</div>
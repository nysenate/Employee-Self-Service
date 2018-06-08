<div class="content-container no-top-margin padding-left-10 padding-right-10 padding-top-5">

  <div>
    <travel-app-print-body app="app"></travel-app-print-body>
  </div>

  <div class="travel-button-container">
    <input type="button" class="travel-neutral-button" value="Exit"
           ng-click="exit()">
    <a target="_blank" ng-href="${ctxPath}/travel/application/travel-application-print?id={{app.id}}">Print</a>
  </div>
</div>
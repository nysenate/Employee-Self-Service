<div ng-controller="NewTravelApplicationCtrl">
  <div class="travel-hero">
    <h2>Travel Application</h2>
  </div>
  <div class="content-container content-controls">
    <div class="padding-10 text-align-center">
      Travel application for: {{application.applicant.firstName}} {{application.applicant.lastName}}
    </div>
  </div>

  <div class="content-container">
    <div class="content-info">
      <travel-app-locations></travel-app-locations>
    </div>


    <div class="grid text-align-center padding-10">
      <%--Left button--%>
      <div class="col-3-12">
        <input type="button" class="reject-button"
               value="Cancel">
      </div>
      <div class="col-6-12">&nbsp;
      </div>
        <%--Right button--%>
      <div class="col-3-12">
        <input type="button" class="submit-button"
                 value="Next"
                 ng-disabled="application.itinerary.origin == null || application.itinerary.destinations.length === 0">
      </div>
    </div>
  </div>
</div>

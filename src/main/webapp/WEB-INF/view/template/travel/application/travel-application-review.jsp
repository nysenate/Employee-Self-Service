
<div class="content-container text-align-center padding-bottom-10">
  <div>
    <h4 class="content-info">Review</h4>
    <p>
      Purpose of Travel.
      <br/>
      {{app.purposeOfTravel}}
    </p>
    <%--Purpose of travel--%>
    <textarea disabled ng-model="purposeOfTravel" cols="80" rows="6" placeholder="Why will you be traveling?"></textarea>


    <%--Allowances--%>
    <div class="grid padding-10">
      <div class="col-6-12 padding-bottom-10">
        <label class="travel-allowance-label">Tolls:</label>
        <input ng-model="app.allowances.tolls" type="number" step="0.01" min="0">
      </div>
      <div class="col-6-12 padding-bottom-10">
        <label class="travel-allowance-label">Parking:</label>
        <input ng-model="app.allowances.parking" type="number" step="0.01" min="0">
      </div>
      <div class="col-6-12">
        <label class="travel-allowance-label">Taxi/Bus/Subway:</label>
        <input ng-model="app.allowances.alternate" type="number" step="0.01" min="0">
      </div>
      <div class="col-6-12">
        <label class="travel-allowance-label">Registration Fee:</label>
        <input ng-model="app.allowances.registrationFee" type="number" step="0.01" min="0">
      </div>
    </div>

  </div>

  <div class="margin-top-20">
    <input type="button" class="neutral-button" value="Back"
           ng-click="allowancesCallback(purposeOfTravel, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="allowancesCallback(purposeOfTravel, ACTIONS.NEXT)">
  </div>
</div>
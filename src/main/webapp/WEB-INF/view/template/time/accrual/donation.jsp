<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="DonationCtrl" id="donation-page">
  <div class="time-attendance-hero">
    <h2>Sick Leave Donation</h2>
  </div>

  <p ng-show="state.maxDonation == null">
    Loading...
  </p>
  <p ng-show="state.maxDonation != null && state.maxDonation < 10">
    You are ineligible to donate sick leave at this time.
  </p>

  <div class="content-container" ng-show="state.maxDonation >= 10">
    <p>
      Accrued sick hours: {{state.accruedSickTime}}<br>
      You may donate 10 - {{state.maxDonation}} hours in half-hour increments.
    </p>
    <form>
      <label for="donation">Donation amount:</label>
      <input type="number" id="donation" name="donation"
             min="10" max={{state.maxDonation}} step="0.5"
             ng-model="state.hoursToDonate" ng-disabled="state.showCertificationMessage"><br><br>

      <input ng-click="state.showCertificationMessage = true" class="submit-button" type="button" value="Donate time"
             ng-disabled="!state.hoursToDonate || state.showCertificationMessage"/>
      <div ng-show="state.showCertificationMessage">
        Once you donate these hours, you can't get them back. Do you still wish to donate?<br>
        <input ng-click="submitDonation()" class="submit-button" type="button" value="Yes">
        <input ng-click="state.showCertificationMessage = false" class="submit-button" type="button" value="No">
      </div>
      <p>
        {{state.message}}
      </p>
    </form>
  </div>

  <div>
    <h3 class="content-info" style="text-align: left;">
      Select a year to view its donation history<br>
    </h3>
    <div class="dropdown">
      <label for="donationHistory"></label>
      <select id="donationHistory" ng-model="state.selectedYear"
              ng-options="year for year in getYears()">
      </select>
    </div>
    <br>

    <table ng-show="state.donationData.length !== 0" class="donation-history-table">
      <thead>
      <tr>
        <th>Date</th>
        <th>Donation Amount</th>
      </tr>
      </thead>
      <tr ng-repeat="donation in state.donationData">
        <td>{{donation.split(":")[0]}}</td>
        <td>{{donation.split(":")[1]}}</td>
      </tr>
    </table>
    <div ng-show="state.donationData.length === 0">
      You have no donations for this year yet.
    </div>
  </div>
</section>

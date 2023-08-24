<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="DonationCtrl" id="donation-page">
  <div class="time-attendance-hero">
    <h2>Sick Time Donation</h2>
  </div>

  <div class="content-container">
    <form>
      <label for="donation">Donation amount:</label>
      <input type="number" id="donation" name="donation"
             min="10" max={{state.maxDonation}} step="0.5"
             ng-model="state.hoursToDonate" ng-disabled="state.showCertificationMessage"><br><br>
      <label for="effectiveDate">Effective date:</label>
      <input type="date" name="effectiveDate" id="effectiveDate"
             ng-model="state.effectiveDate" ng-change="setMaxDonation()"
             ng-disabled="state.showCertificationMessage"><br><br>

      <input ng-click="state.showCertificationMessage = true" class="submit-button" type="button" value="Donate time"
             ng-disabled="!state.hoursToDonate || state.showCertificationMessage"/>
      <div ng-show="state.showCertificationMessage">
        Once you donate this time, you can't get it back. Do you still wish to donate?<br>
        <input ng-click="submitDonation()" class="submit-button" type="button" value="Yes">
        <input ng-click="state.showCertificationMessage = false" class="submit-button" type="button" value="No">
      </div>
    </form>
  </div>

  <div>
    <h3 class="content-info" style="text-align: left;">
      Select a year to view its donation history<br>
    </h3>
    <div class="dropdown">
      <label for="donationHistory"></label>
      <select id="donationHistory" ng-model="state.currYear" ng-change="setDonationHistory()">
        <option ng-repeat="year in getYears()">
          {{year}}
        </option>
      </select>
    </div>
    <br>

    <table ng-show="state.currYear && state.donationData.length !== 0" class="donation-history-table">
      <thead>
      <tr>
        <th>Effective Date</th>
        <th>Donation Amount</th>
      </tr>
      </thead>
      <tr ng-repeat="donation in state.donationData">
        <td>{{donation.split(":")[0]}}</td>
        <td>{{donation.split(":")[1]}}</td>
      </tr>
    </table>
    <div ng-show="state.currYear && state.donationData.length === 0">
      You have no donations for this year yet.
    </div>
  </div>
</section>

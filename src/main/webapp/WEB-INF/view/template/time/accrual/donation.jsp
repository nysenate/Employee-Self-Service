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
      ng-model="state.hoursToDonate"><br><br>
      <label for="effectiveDate">Effective date :</label>
      <input type="date" name="effectiveDate" id="effectiveDate"
             ng-model="state.effectiveDate" ng-change="setMaxDonation()"><br><br>
    </form>

  </div>
  <div>
    <div class="dropdown">
      <label for="history"></label>
      <select id="history" ng-model="state.currYear">
        <option ng-repeat="year in getYears()">
          {{year}}
        </option>
      </select>
      <p ng-repeat="donation in state.donationData">
        {{donation}}
      </p>

    </div>
    <div ng-hide="state.donationData.length !== 0">
      You have no donations for this year yet.
    </div>
  </div>
</section>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="DonationCtrl" id="donation-page">
  <div class="time-attendance-hero">
    <h2>Sick Leave Donation</h2>
  </div>

  <div style="text-align: center; font-size: 110%">
    <p ng-show="state.maxDonation == null">
      Loading...
    </p>
    <p ng-show="state.maxDonation != null && state.maxDonation == 0">
      You are ineligible to donate sick leave at this time.
    </p>

    <div class="content-container" ng-show="state.maxDonation >= 0.5">
      <p style="padding-top: 10px">
        You may donate up to {{state.maxDonation}} hours in half-hour increments.
      </p>
      <form>
        <label for="donation">Donation amount:</label>
        <input type="number" id="donation" name="donation"
               style="margin: 5px"
               min="0.5" max={{state.maxDonation}} step="0.5"
               ng-model="state.hoursToDonate">

        <input ng-click="openContinuePopup()" class="submit-button" type="button"
               value="Donate time"
               ng-disabled="!state.hoursToDonate"/>
      </form>
      <br>
    </div>
  </div>

  <div class="content-controls" style="padding: 10px">
    <p class="content-info" style="text-align: left; padding: 0">
      Filter By Year &nbsp;
      <select ng-model="state.selectedYear"
              ng-options="year for year in getYears()">
      </select>
    </p>
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

  <div modal-container>
    <modal modal-id="donation-modal">
      <sick-donation-confirmation-modal></sick-donation-confirmation-modal>
    </modal>
    <modal modal-id="donation-continue-modal">
      <sick-donation-continue-modal></sick-donation-continue-modal>
    </modal>
  </div>
</section>

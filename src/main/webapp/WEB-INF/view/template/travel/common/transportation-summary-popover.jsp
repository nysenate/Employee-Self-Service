<span ng-hide="hide"
      class="icon-info pointer"
      ns-popover ns-popover-template="transportation-summary"
      ns-popover-theme="ns-popover-tooltip-theme"
      ns-popover-placement="top"
      ns-popover-trigger="click">
</span>

<script type="text/ng-template" id="transportation-summary">
  <div class="triangle"></div>
  <div class="ns-popover-tooltip" style="max-width: 600px;">
    <table class="travel-table">
      <thead>
      <tr>
        <td>From</td>
        <td>To</td>
        <td>Miles</td>
        <td>Rate</td>
        <td>Allowance</td>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="pd in mileagePerDiems.requestedPerDiems">
        <td>{{pd.from.formattedAddressWithCounty}}</td>
        <td>{{pd.to.formattedAddressWithCounty}}</td>
        <td>{{pd.miles}}</td>
        <td>{{pd.mileageRate}}</td>
        <td>{{pd.requestedPerDiem | currency}}</td>
      </tr>
      <tr>
        <td></td>
        <td class="bold">Total:</td>
        <td></td>
        <td></td>
        <td class="bold">{{mileagePerDiems.totalPerDiem | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
</script>
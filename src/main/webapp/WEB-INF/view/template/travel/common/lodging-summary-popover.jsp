<span ng-hide="hide"
      class="icon-info pointer"
      ns-popover ns-popover-template="lodging-summary"
      ns-popover-theme="ns-popover-tooltip-theme"
      ns-popover-placement="top"
      ns-popover-trigger="click">
</span>

<script type="text/ng-template" id="lodging-summary">
  <div class="triangle"></div>
  <div class="ns-popover-tooltip" style="max-width: 600px;">
    <table class="travel-table">
      <thead>
      <tr>
        <td>Date</td>
        <td>Address</td>
        <td>Lodging Per Diem</td>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="perDiem in amd.lodgingPerDiems.requestedLodgingPerDiems">
        <td>{{perDiem.date | date: 'shortDate'}}</td>
        <td>{{perDiem.address.formattedAddressWithCounty}}</td>
        <td ng-class="{'line-through': amd.lodgingPerDiems.isOverridden}">
          {{perDiem.rate | currency}}
        </td>
      </tr>
      <tr>
        <td></td>
        <td class="bold" ng-if="!amd.lodgingPerDiems.isOverridden">Total:</td>
        <td class="disapproved-text" ng-if="amd.lodgingPerDiems.isOverridden">Overridden to:
        </td>
        <td class="bold">{{amd.lodgingPerDiems.totalPerDiem | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
</script>
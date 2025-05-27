<span ng-hide="hide"
      class="icon-info pointer"
      ns-popover ns-popover-template="meal-summary"
      ns-popover-theme="ns-popover-tooltip-theme"
      ns-popover-placement="top"
      ns-popover-trigger="click">
</span>

<script type="text/ng-template" id="meal-summary">
  <div class="triangle"></div>
  <div class="ns-popover-tooltip" style="max-width: 600px;">
    <table class="travel-table">
      <thead>
      <tr>
        <td>Date</td>
        <td>Address</td>
        <td>Breakfast</td>
        <td>Dinner</td>
        <td>Total</td>
      </tr>
      </thead>
      <tbody>
      <tr ng-repeat="perDiem in amd.mealPerDiems.requestedMealPerDiems">
        <td>{{perDiem.date | date: 'shortDate'}}</td>
        <td>{{perDiem.address.formattedAddressWithCounty}}</td>
        <td>{{::(perDiem.breakfast | currency) || NOT_AVAILABLE }}</td>
        <td>{{::(perDiem.dinner | currency) || NOT_AVAILABLE }}</td>
        <td ng-class="{'line-through': amd.mealPerDiems.isOverridden}">
          {{perDiem.total | currency}}
        </td>
      </tr>
      <tr>
        <td></td>
        <td class="bold" ng-if="!amd.mealPerDiems.isOverridden">Total:</td>
        <td class="disapproved-text" ng-if="amd.mealPerDiems.isOverridden">Overridden to:</td>
        <td></td>
        <td></td>
        <td class="bold">{{amd.mealPerDiems.totalPerDiem | currency}}</td>
      </tr>
      </tbody>
    </table>
  </div>
</script>
<div class="padding-10">
  <div>
    <h3 class="content-info">Over Allowed Quantity</h3>
  </div>

  <div ng-show="type === types.ORDER">
    <p>
      You are trying to order over the per order limit on this item.
    </p>
  </div>

  <div ng-show="type === types.MONTH">
    <p>
      You are trying to order over the monthly limit on this item.
    </p>
  </div>
  <p>
    If needed, you can request more of this item through a special request.
    Would you like to submit a speical request for this item?
  </p>
  <div>
    <input class="submit-button" type="button" value="Yes" ng-click="submitSpecialRequest()">
    <input class="neutral-button" type="button" value="Nevermind" ng-click="nevermind()">
  </div>
</div>
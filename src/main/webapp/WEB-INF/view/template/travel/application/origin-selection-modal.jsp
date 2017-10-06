<div class="padding-10">
  <h3 class="content-info">Where are you traveling from?</h3>
  <h4>Enter an address or drag the marker.</h4>
  <div class="padding-bottom-10">
    <label>Address: <input id="travel-origin-address" ng-model="addressString" type="text" size="30"></label>
  </div >
  <div class="content-info clearfix">
    <locationpicker options="locationpickerOptions"></locationpicker>
  </div>
  <div class="padding-top-10 clearfix width-100">
    <input type="button" class="neutral-button" style="float: left;"
           value="Cancel"
           ng-click="addDestination()">
    <input type="button" class="submit-button" style="float: right;"
           value="Select"
           ng-click="addDestination()">
  </div>
</div>

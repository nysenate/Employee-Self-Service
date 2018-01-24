<div>
  <div class="travel-location-div">
    <div style="float: left; font-size: 0.8em;">
      Arrival Date: {{destination.arrivalDate | date: 'shortDate'}}<br/>
      Departure Date: {{destination.departureDate | date: 'shortDate'}}
    </div>

    <div>
      {{destination.address.addr1}}<br/>
      <span ng-if="destination.address.addr2.length <= 0">{{destination.address.addr2}}<br/></span>
      {{destination.address.city}} {{destination.address.state}} {{destination.address.zip5}}
    </div>

    <div style="float: right; font-size: 0.8em;">
      Mode of Transportation: <br/>
      {{destination.modeOfTransportation}}
    </div>
  </div>
</div>

<div>
  <div class="travel-destination-display-box">
    <div>
      Arrival: {{destination.arrivalDate | date: 'shortDate'}}<br/>
      Departure: {{destination.departureDate | date: 'shortDate'}}
    </div>

    <div>
      {{destination.address.addr1}}<br/>
      <span ng-if="destination.address.addr2.length > 0">{{destination.address.addr2}}<br/></span>
      {{destination.address.city}} {{destination.address.state}} {{destination.address.zip5}}
    </div>

    <div>
      Mode of Transportation: <br/>
      {{destination.modeOfTransportation}}
    </div>
  </div>
</div>

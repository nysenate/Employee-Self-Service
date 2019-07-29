<div class="resp-ctr-head-picker">

  <a href ng-click="clearSelected()">Clear Selected Offices</a><br>

  <ui-select multiple ng-model="respCtrHeads.selection" close-on-select="false">
    <ui-select-match placeholder="Select an office">
      {{$item.name}}
    </ui-select-match>
    <ui-select-choices
        repeat="rch in rchResults | filter:$select.search"
        refresh-delay="300">
      <hr ng-show="!$fist">
      <div ng-bind-html="rch.name | highlight: $select.search"></div>
      <div ng-bind-html="rch.code | highlight: $select.search"></div>
    </ui-select-choices>
  </ui-select>

</div>
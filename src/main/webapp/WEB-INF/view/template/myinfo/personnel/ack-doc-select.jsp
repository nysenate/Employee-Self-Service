<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
  <select ng-model="year" ng-options="year for year in docYears">
  </select>

  <ul class="ack-doc-list">
    <li ng-repeat="doc in ackDocsInSelectedYear">
      <a ng-href="{{ctxPath}}/api/v1/acknowledgment/report/complete/{{doc.id}}" ng-bind="doc.title" target="_blank"
      class="ack-doc-list-item-title"></a>
    </li>
  </ul>
</div>
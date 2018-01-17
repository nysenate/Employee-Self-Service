<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AcknowledgementCtrl">
  <div class="my-info-hero">
    <h2>Acknowledge Documents</h2>
  </div>

  <div loader-indicator class="loader" ng-show="isLoading()"></div>

  <div class="content-container" ng-if="!isLoading()">

    <ess-notification level="info" title="No Active Documents" ng-hide="anyAckDocs()">
      <p>
        There are no active documents that need acknowledgement or have been previously acknowledged.<br>
        Please contact the STS Helpline at (518) 455-2011 with any questions or concerns.
      </p>
    </ess-notification>

    <div ng-show="anyAckDocs()">
      <p class="content-info">
        Below is a list of active documents that must be acknowledged by each Senate employee.<br>
        Click a document to view its content and acknowledge it if not done so already.
      </p>

      <div class="ack-doc-display">

        <div ng-show="state.documents.unacknowledged.length > 0">
          <h2>Unacknowledged Documents</h2>
          <ul class="unacknowledged-doc-list">
            <li ng-repeat="doc in state.documents.unacknowledged">
              <a ng-href="{{ctxPath}}/myinfo/personnel/acknowledgement/{{doc.id}}">
                <p class="ack-doc-list-item">{{doc.title}}</p>
              </a>
            </li>
          </ul>
        </div>

        <div ng-show="state.documents.acknowledged.length > 0">
          <h2>Acknowledged Documents</h2>
          <ul>
            <li ng-repeat="doc in state.documents.acknowledged">
              <a ng-href="{{ctxPath}}/myinfo/personnel/acknowledgement/{{doc.id}}">
                <p class="ack-doc-list-item">
                  {{doc.title}}
                  <span class="ack-doc-list-item-ack-date">
                    - acknowledged {{getAcknowledgedDate(doc) | moment:'MMM D, YYYY'}}
                  </span>
                </p>
              </a>
            </li>
          </ul>
        </div>

      </div>
    </div>
  </div>

  <div modal-container></div>
</section>

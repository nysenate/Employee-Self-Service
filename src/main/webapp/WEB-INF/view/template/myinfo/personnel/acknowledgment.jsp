<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AcknowledgmentCtrl">
  <div class="my-info-hero">
    <h2>Acknowledgments</h2>
  </div>

  <div loader-indicator class="loader" ng-show="isLoading()"></div>

  <div class="content-container" ng-if="!isLoading()">

    <ess-notification level="info" title="No Active Documents" ng-hide="anyAckDocs()">
      <p>
        There are no active policies/documents for acknowledgment.<br>
        Please contact the STS Helpline at {{helplinePhoneNumber}} with any questions or concerns.
      </p>
    </ess-notification>

    <div ng-show="anyAckDocs()">
      <p class="content-info">
        Listed below are policies/documents which require your review and acknowledgment annually.<br>
        Click on a document below to proceed.<br>
        <span class="bold-text">FAILURE TO RESPOND MAY RESULT IN THE HOLDING OF YOUR PAYCHECK.</span><br>
        Contact the Personnel Office (518-455-3376) if you have any questions.
      </p>

      <div class="ack-doc-display">

        <h2>Pending Acknowledgments</h2>
        <ul class="unacknowledged-doc-list">
          <li ng-show="state.documents.unacknowledged.length == 0">
            You do not have any pending acknowledgments.
          </li>
          <li ng-repeat="doc in state.documents.unacknowledged">
            <a ng-href="{{ctxPath}}/myinfo/personnel/acknowledgments/{{doc.id}}">
              <p class="ack-doc-list-item">
                <span class="icon-text-document"></span>
                <span class="ack-doc-list-item-title" ng-bind="doc.title"></span>
              </p>
            </a>
          </li>
        </ul>

        <h2>Completed Acknowledgments</h2>
        <ul>
          <li ng-show="state.documents.acknowledged.length == 0">
            You do not have any completed acknowledgments.
          </li>
          <li ng-repeat="doc in state.documents.acknowledged">
            <a ng-href="{{ctxPath}}/myinfo/personnel/acknowledgments/{{doc.id}}">
              <p class="ack-doc-list-item">
                <span class="icon-check"></span>
                <span class="ack-doc-list-item-title" ng-bind="doc.title"></span>
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

  <div modal-container></div>
</section>

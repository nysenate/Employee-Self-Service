<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AcknowledgementCtrl">
  <div class="my-info-hero">
    <h2>Acknowledgements</h2>
  </div>

  <div loader-indicator class="loader" ng-show="isLoading()"></div>

  <div class="content-container" ng-if="!isLoading()">

    <ess-notification level="info" title="No Active Documents" ng-hide="anyAckDocs()">
      <p>
        There are no active policies/documents for acknowledgement.<br>
        Please contact the STS Helpline at (518) 455-2011 with any questions or concerns.
      </p>
    </ess-notification>

    <div ng-show="anyAckDocs()">
      <p class="content-info">
        Listed below are Policies/Documents which require your review and acknowledgment annually.<br>
        Failure to respond may result in the holding of your paycheck.<br>
        Contact the Personnel Office (518-455-3376) if you have any questions.
      </p>

      <div class="ack-doc-display">

        <div ng-show="state.documents.unacknowledged.length > 0">
          <h2>Pending Acknowledgements</h2>
          <ul class="unacknowledged-doc-list">
            <li ng-repeat="doc in state.documents.unacknowledged">
              <a ng-href="{{ctxPath}}/myinfo/personnel/acknowledgements/{{doc.id}}">
                <p class="ack-doc-list-item">{{doc.title}}</p>
              </a>
            </li>
          </ul>
        </div>

        <div ng-show="state.documents.acknowledged.length > 0">
          <h2>Completed Acknowledgements</h2>
          <ul>
            <li ng-repeat="doc in state.documents.acknowledged">
              <a ng-href="{{ctxPath}}/myinfo/personnel/acknowledgements/{{doc.id}}">
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

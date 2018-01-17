<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AckDocViewCtrl">
  <div class="my-info-hero">
    <h2 ng-bind="state.document.title"></h2>
  </div>

  <div loader-indicator class="loader" ng-show="isLoading()"></div>

  <div class="content-container" ng-if="!isLoading()">

    <p class="content-info" ng-hide="state.acknowledged">
      Please read the document below.  Scroll to the end and then click the acceptance button.
    </p>
    <p class="content-info" ng-show="state.acknowledged">
      You acknowledged this document on {{state.ackTimestamp | moment:'LL'}}
    </p>

    <div class="ack-doc-view-nav">
      <a ng-href="{{ackDocPageUrl}}">
        Return to Acknowledge Documents page
      </a>
      <a ng-href="{{ctxPath + state.document.path}}" target="_blank">
        Open Printable View
      </a>
    </div>

    <iframe id="ack-doc-iframe"
            src="{{ctxPath + state.document.path + '#view=fit&toolbar=0&statusbar=0&messages=0&navpanes=0'}}">
    </iframe>

    <div class="ack-doc-button-container" ng-hide="state.acknowledged">
      <input type="button" class="submit-button"
             title="I have read the policy and click here to confirm my acceptance of the same"
             value="I have read the policy and click here to confirm my acceptance of the same"
             ng-click="acknowledgeDocument()">
    </div>

  </div>
  <div modal-container>
    <modal modal-id="acknowledge-success">
      <div confirm-modal rejectable="true" title="Document Acknowledged"
           confirm-message="You have successfully acknowledged the document."
           resolve-button="Return to Acknowledge Documents" resolve-class="time-neutral-button"
           reject-button="Remain Here" reject-class="time-neutral-button">
      </div>
    </modal>
  </div>
</section>

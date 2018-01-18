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

    <div id="ack-doc-container" on-scroll-to-bottom="markDocRead()">
      <div id="ack-doc-scroll-cover" ng-style="{'height': state.docHeight + 'px' }"></div>
      <embed id="ack-doc-embed" type="application/pdf" ng-style="{'height': state.docHeight + 'px' }"
              src="{{ctxPath + state.document.path + '#view=fit&toolbar=0&statusbar=0&messages=0&navpanes=0'}}">
      </embed>
    </div>

    <div class="ack-doc-button-container" ng-hide="state.acknowledged">
      <p class="content-info acknowledgement-text">
        I hereby acknowledge receipt of the New York State Senate document
        {{state.document.title}} and state that I have read the same.  I
        understand that compliance is a condition of employment and that violation
        of any policy could subject me to penalties including, but not limited to,
        loss of privileges to use Senate technologies, demotion, suspension or
        termination.<br>
        <br>
        In addition for purposes of submitting this acknowledgment, the username
        and password is the electronic signature of the employee. As liability
        attaches, the employee should ensure that his or her username and password
        is securely kept and used.
      </p>
      <input type="button" class="submit-button"
             title="{{state.docRead ? 'I Agree' : 'You must read the entire document to agree'}}"
             value="I Agree"
             ng-disabled="!state.docRead"
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

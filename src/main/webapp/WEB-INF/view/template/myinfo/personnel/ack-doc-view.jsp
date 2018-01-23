<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AckDocViewCtrl">
  <div class="my-info-hero">
    <h2 ng-bind="state.document.title"></h2>
  </div>

  <div loader-indicator class="loader" ng-show="isLoading()"></div>

  <div class="content-container ack-doc-container" ng-if="!isLoading()">

    <div class="content-info" ng-hide="state.acknowledged">
      <h3>Instructions</h3>
      <ul class="acknowledgment-instructions">
        <li>
          Please review the following policy/document, using the scroll bar to advance.
       </li>
        <li>
          If desired, click "Open Printable View" to open a separate tab to print the document.
        </li>
        <li>
          After reviewing the entire document, read the acknowledgment at the bottom of the screen and indicate your agreement by clicking the "I Agree" button.
        </li>
        <li class="bold-text">
          You must scroll to the end of the document for the "I Agree" button to become available.
        </li>
      </ul>
    </div>
    <p class="content-info" ng-show="state.acknowledged">
      You acknowledged this policy/document on {{state.ackTimestamp | moment:'LL'}}
    </p>

    <div class="ack-doc-view-nav">
      <a ng-href="{{ackDocPageUrl}}">
        Return to Acknowledgments page
      </a>
      <a ng-href="{{ctxPath + state.document.path}}" target="_blank">
        Open Printable View
      </a>
    </div>

    <div id="ack-doc-embed-container" on-scroll-to-bottom="markDocRead()">
      <div id="ack-doc-scroll-cover" ng-if="useOverlay()" ng-style="{'height': state.docHeight + 'px' }"></div>
      <!-- Use an frame tag for edge.  Edge doesn't support iframes, but embed works better on other browsers -->
      <iframe class="ack-doc-embed" ng-style="{'height': state.docHeight + 'px' }"
              ng-if="useIframe()"
              src="{{ctxPath + state.document.path + '#view=fit&toolbar=0&statusbar=0&messages=0&navpanes=0'}}">
      </iframe>
      <embed class="ack-doc-embed" type="application/pdf" ng-style="{'height': state.docHeight + 'px' }"
             ng-if="!useIframe()"
             src="{{ctxPath + state.document.path + '#view=fit&toolbar=0&statusbar=0&messages=0&navpanes=0'}}">
      </embed>
    </div>

    <div class="ack-doc-button-container" ng-hide="state.acknowledged">
      <p class="content-info acknowledgment-text">
        I hereby acknowledge receipt of the New York State Senate
        <span ng-bind="state.document.title" class="ack-doc-title"></span>
        and state that I have read the same.  I
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
      <div confirm-modal rejectable="true" title="Acknowledgment Complete"
           confirm-message="You have successfully acknowledged this policy/document."
           resolve-button="Return to Acknowledgments" resolve-class="time-neutral-button"
           reject-button="Remain Here" reject-class="time-neutral-button">
      </div>
    </modal>
  </div>
</section>

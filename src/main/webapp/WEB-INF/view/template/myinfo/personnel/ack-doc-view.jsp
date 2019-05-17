<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%-- The PDFJS library is imported only on this page because it is of non-trivial size --%>
<script type="text/javascript" src="${ctxPath}/assets/js/dest/pdf.min.js?v=${releaseVersion}"></script>

<section ng-controller="AckDocViewCtrl">
  <div class="my-info-hero">
    <h2 ng-bind="state.document.title"></h2>
  </div>

  <div loader-indicator class="loader" ng-show="isLoading()"></div>

  <ess-notification level="info" title="Document not found" ng-show="!isLoading() && !state.docFound">
    The requested document was not found.
  </ess-notification>

  <ess-notification level="warn"
                    title="Acknowledgment task not found"
                    ng-show="!isLoading() && state.docFound && !state.taskFound">
    No acknowledgment task record exists for the given document.  Please contact the helpline.
  </ess-notification>

  <div class="content-container ack-doc-container"
       ng-cloak
       ng-if="!isLoading() && state.docFound && state.taskFound">

    <p class="content-info" ng-hide="state.acknowledged">
      Please review this policy/document and click the button to acknowledge it.
      <br/><span class="bold-text">You must scroll to the end of the page for the button to become available.</span>
      <br/>If desired, click "Open Printable View" to open a separate tab to print the document.
    </p>
    <p class="content-info" ng-show="state.acknowledged">
      You acknowledged this policy/document on {{state.ackTimestamp | moment:'LL'}}
    </p>

    <div class="ack-doc-view-nav">
      <a ng-href="{{todoPageUrl}}">
        Return to Personnel To-Do List
      </a>
      <a ng-href="{{ctxPath + state.document.path}}" target="_blank">
        Open Printable View
      </a>
    </div>

    <canvas ng-repeat="pageNum in state.pages" id="ack-pdf-page-{{pageNum}}" width="880"></canvas>

    <div class="ack-doc-button-frame" ng-cloak ng-hide="state.acknowledged">
      <div class="ack-doc-button-container content-container">
        <div loader-indicator class="sm-loader ack-button-loader" ng-hide="state.docReady"></div>
        <span class="acknowledge-button" ng-class="{'disabled': !state.docRead}" ng-show="state.docReady">
          <input type="button" class="submit-button"
                 title="{{state.docRead ? 'Acknowledge' : 'You must read the entire document to acknowledge'}}"
                 value="Acknowledge"
                 ng-disabled="!state.docRead"
                 ng-click="acknowledgeDocument()">
        </span>
      </div>
    </div>

  </div>
  <div modal-container>
    <modal modal-id="acknowledge-prompt">
      <div confirm-modal rejectable="true" title="Acknowledge Policy/Document"
           resolve-button="I Agree"
           reject-button="Cancel" reject-class="time-neutral-button">
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
      </div>
    </modal>
    <modal modal-id="acknowledge-success">
      <div confirm-modal rejectable="true" title="Acknowledgment Complete"
           confirm-message="You have successfully acknowledged this policy/document."
           resolve-button="Return to To-Do List" resolve-class="time-neutral-button"
           reject-button="Remain Here" reject-class="time-neutral-button">
      </div>
    </modal>
  </div>
</section>

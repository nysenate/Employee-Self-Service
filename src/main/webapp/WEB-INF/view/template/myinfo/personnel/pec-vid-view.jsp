<section ng-controller="PecVidCtrl">
  <div class="my-info-hero">
    <h2 ng-bind="state.assignment.task.title"></h2>
  </div>

  <div loader-indicator class="loader" ng-show="state.request.task"></div>

  <div class="content-container" ng-hide="state.request.task">

    <%-- Error message if the video task was not found --%>
    <ess-notification ng-hide="state.assignmentFound"
                      level="error"
                      title="Video Task Not Found">
      The personnel task assignment associated with this video could not be retrieved.<br>
      Please contact the helpline to report this issue.
    </ess-notification>

    <div ng-if="state.assignmentFound">
      <%-- Instructions --%>
      <p class="content-info personnel-todo-instructions">
        <span ng-show="state.assignment.completed">
          Records indicate you have already watched this video on or before
          {{state.assignment.timestamp | moment:'LL'}}.
          <br>
          If you would like to review the video, you may still view it below.
        </span>
        <span ng-hide="state.assignment.completed">
          Please take some time to watch the video below.<br>
          Codes will appear on-screen at various points during the video.<br>
          Record these codes as they appear.<br>
          When the video is finished, use the form below the video to enter the codes to confirm your viewing.
        </span>
      </p>
      <%-- Nav bar--%>
      <div class="todo-task-view-nav">
        <a ng-href="{{todoPageUrl}}">
          Return to Personnel To-Do List
        </a>
      </div>
      <%-- Video Container--%>
      <div class="pec-video-container">
        <video controls>
          <source ng-src="{{state.videoUrl}}" type="video/mp4">
        </video>
      </div>
      <%-- Form container - Show if the task isn't done.--%>
      <div ng-hide="state.assignment.completed">
        <hr>
        <p class="content-info">
          In the form below, please enter the codes from the video and then click "Submit".
        </p>

        <%-- Show loader when submitting codes --%>
        <div class="content-info" ng-show="state.request.code">
          <h3>Submitting codes...</h3>
          <div loader-indicator class="sm-loader"></div>
        </div>
        <%-- Show error message if incorrect codes were submitted --%>
        <ess-notification level="error" title="Incorrect Codes"
                          ng-show="state.incorrectCode"
                          message="One or more of the submitted codes were incorrect.  Please double check them and resubmit."
        ></ess-notification>
        <form name="videoCodeForm"
              ng-hide="state.request.code"
              class="pec-video-code-form"
              ng-submit="submitCodes()">
          <%-- Have an input for each code--%>
          <label ng-repeat="code in state.codes">
            {{code.label}}<br>
            <input type="text" ng-model="code.value" required ng-disabled="state.request.code">
          </label>
          <%-- Disable the submit button if codes are not entered--%>
          <input type="submit"
                 ng-disabled="videoCodeForm.$invalid || state.request.code"
                 class="submit-button"
                 title="{{videoCodeForm.$invalid ? 'You must enter all codes to submit.' : 'Submit Codes'}}">
          <p class="pec-video-code-submit-message" ng-class="{'visibility-hidden': !videoCodeForm.$invalid}">
            You must enter all codes to submit.
          </p>
        </form>
      </div>
    </div>

  </div>

  <div modal-container>
    <modal modal-id="code-submit-success">
      <div confirm-modal rejectable="true" title="Code Submission Complete"
           confirm-message="Video codes were successfully submitted."
           resolve-button="Return to To-Do List" resolve-class="time-neutral-button"
           reject-button="Remain Here" reject-class="time-neutral-button">
      </div>
    </modal>
  </div>
</section>

<section ng-controller="EthicsCourseLiveCtrl">
  <div class="my-info-hero">
    <h2>Mandatory LIVE in-person and Online Ethics Training Instructions</h2>
  </div>

  <div class="content-container">
    <div loader-indicator class="loader" ng-show="state.loading"></div>

    <div ng-hide="state.loading">
      <%-- If the task was not loaded successfully --%>
      <ess-notification ng-hide="state.assignment"
                        level="error"
                        title="Ethics Course Personnel Task Assignment Not Found">
        The personnel task assignment associated with this ethics course could not be retrieved.<br>
        Please contact the helpline to report this issue.
      </ess-notification>

      <div ng-if="state.assignment">
        <p class="content-info personnel-todo-instructions">
          Please follow the instructions below to complete your training.
          <br/>
          Once complete, enter and submit the codes you received to confirm your completion.
        </p>
      </div>

      <div class="legethics-instruction-container">
        <a ng-href="{{todoPageUrl}}">Return to Personnel To-Do List</a>

        <div ng-hide="state.assignment.completed">
          <h2>Training Instructions</h2>
          <p>
            Existing employees must attend a LIVE in-person or online ethics training within the calendar year.
            New employees must attend a LIVE in-person or online ethics training within 90 days of their employment (pursuant to Chapter 56 of the Laws of 2022).
            Trainings will be held in Albany for employees who can attend in-person, and also streamed live online at the same time.
            (Albany-based employees are highly encouraged to participate in the in-person training)
          </p>
          <p>
            More details on dates and times for in-person training and live broadcasts can be found at:
          </p>
          <a ng-href="{{state.assignment.task.getCourseUrl()}}" target="_blank">LINK TO COURSE URL</a>

          <hr style="margin-top: 30px; margin-bottom: 30px; margin-left: 80px; margin-right: 80px"/>

          <h2>Code Submission</h2>
          <p>
            Once you have completed the course, enter the codes from the presenters below to confirm your completion.
          </p>

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
                class="pec-video-code-form"
                ng-submit="submitEthicsCodes()">

            <%--Front End for Date Picker--%>
            <label for="ethicsDate">Date of Training
              <input type="date" ng-model="state.trainingDate" id="ethicsDate" required/>
            </label>

            <label ng-repeat="code in state.codes">
              {{code.label}}
              <br/>
              <input type="text" ng-model="code.value" required/>
            </label>
            <input type="submit"
                   class="submit-button"
                   ng-disabled="videoCodeForm.$invalid"/>
            <p class="pec-video-code-submit-message" ng-class="{'visibility-hidden': !videoCodeForm.$invalid}">
              You must enter all codes to submit.
            </p>
          </form>
        </div>
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
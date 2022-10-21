<section ng-controller="EthicsCourseLiveCtrl">
  <div class="my-info-hero">
    <h2>Ethics Course Live Training</h2>
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
          <h2>Ethics Live Course Training Instructions</h2>
          <p>
            This course can be accessed using the link below
          </p>
          <a ng-href="{{state.assignment.task.getCourseUrl()}}">Live Ethics Training Course</a>

          <hr style="margin-top: 30px; margin-bottom: 30px; margin-left: 80px; margin-right: 80px"/>

          <h2>Ethics Live Course Code Submittion</h2>
          <p>
            Once you have completed the course, enter the codes below to confirm your completion.
          </p>

          <form name="videoCodeForm"
                class="pec-video-code-form"
                ng-submit="submitCodes()">
            <label ng-repeat="code in state.assignment.task.codes">
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

</section>
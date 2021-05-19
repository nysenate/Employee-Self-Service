<section ng-controller="EthicsCourseCtrl">
  <div class="my-info-hero">
    <h2>Ethics Course Training</h2>
  </div>

  <div class="content-container">

    <%--  Show during loading  --%>
    <div loader-indicator class="loader" ng-show="state.loading"></div>

    <%--  Hide during loading  --%>
    <div ng-hide="state.loading">
      <%-- If the task was not loaded successfully --%>
      <ess-notification ng-hide="state.assignment"
                        level="error"
                        title="Ethics Course Personnel Task Assignment Not Found">
        The personnel task assignment associated with this ethics course could not be retrieved.<br>
        Please contact the helpline to report this issue.
      </ess-notification>

      <%-- If the task is loaded --%>
      <div ng-if="state.assignment">

        <%-- Instruction header --%>
        <p class="content-info personnel-todo-instructions">
          <span ng-show="state.assignment.completed">
            Records indicate you completed this Ethics training on or before
            {{state.assignment.timestamp | moment:'LL'}}
          </span>
          <span ng-hide="state.assignment.completed">
            As mandated by law, all current employees are required to complete this ethics course.
            <br>
            Please follow all instructions below to complete the course.
          </span>
        </p>

        <div class="legethics-instruction-container">
          <a ng-href="{{todoPageUrl}}">
            Return to Personnel To-Do List
          </a>

          <%-- If the task is not yet completed --%>
          <div ng-hide="state.assignment.completed">
            <h2>Ethics Course Training Instructions</h2>
            <ul>
              <li>The interactive course can be accessed using the link below.</li>
              <li>
                <span class="bold-text">
                You must use your Senate email address.
              </span>
              </li>
            </ul>
            <p><a ng-href="{{state.assignment.task.getCourseUrl()}}">Ethics Training Course</a></p>
          </div>
        </div>

      </div>
    </div>

  </div>
</section>

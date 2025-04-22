<section ng-controller="Knowbe4CourseCtrl">
  <div class="my-info-hero">
    <h2>Know Be 4 Training Instructions</h2>
  </div>

  <div class="content-container">
    <div loader-indicator class="loader" ng-show="state.loading"></div>

    <div ng-hide="state.loading">
      <%-- If the task was not loaded successfully --%>
      <ess-notification ng-hide="state.assignment"
                        level="error"
                        title="Know Be 4 Course Personnel Task Assignment Not Found">
        The personnel task assignment associated with this cybersecurity course could not be retrieved.<br>
        Please contact the helpline to report this issue.
      </ess-notification>

      <div ng-if="state.assignment">
        <p class="content-info personnel-todo-instructions">
          Please follow the instructions below to complete your training.
        </p>
      </div>

      <div class="legethics-instruction-container">
        <a ng-href="{{todoPageUrl}}">Return to Personnel To-Do List</a>

        <div ng-hide="state.assignment.completed">
          <h2>Training Instructions</h2>
          <p>
            <b>All employees are required to complete online cybersecurity awareness training within the calendar year.
              New employees have 30 days to complete, existing employees will be notified of their completion deadline.</b>
            <br>
            Assignment emails will be sent to employees' Senate mailboxes.
            <br>
            Follow the email instructions or visit the link below to complete the course.
            If you need assistance, please contact the STS Helpline at 518-455-2313.
          </p>
          <a ng-href="{{state.assignment.task.getCourseUrl()}}" target="_blank">LINK TO COURSE URL</a>

        </div>
      </div>

    </div>
  </div>

</section>
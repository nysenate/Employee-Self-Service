<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="TodoCtrl">
  <div class="my-info-hero">
    <h2>Personnel To-Do List</h2>
  </div>

  <div loader-indicator class="loader" ng-show="isLoading()"></div>

  <div class="content-container" ng-if="!isLoading()">

    <ess-notification level="info" title="No Active Documents" ng-hide="anyTasks()">
      <p>
        No personnel tasks were found, completed or otherwise.<br>
        Please contact the STS Helpline at (518) 455-2011 with any questions or concerns.
      </p>
    </ess-notification>

    <div ng-show="anyTasks()">
      <p class="content-info personnel-todo-instructions">
        Listed below are personnel tasks that require your attention.<br>
        Click on a task link to take action on that task.<br>
        <span class="bold-text">FAILURE TO RESPOND MAY RESULT IN THE HOLDING OF YOUR PAYCHECK.</span><br>
        Contact the Personnel Office (518-455-3376) if you have any questions.
      </p>

      <div class="personnel-task-display">

        <h2>Incomplete Tasks</h2>
        <ul class="personnel-task-incomplete-list">
          <li ng-show="state.tasks.incomplete.length == 0">
            You do not have any tasks needing attention.
          </li>
          <li ng-repeat="task in state.tasks.incomplete">
            <a ng-href="{{task.getActionUrl()}}">
              <p class="personnel-task-list-item">
                <span class="{{task.getIconClass()}}"></span>
                <span class="personnel-task-list-item-title">
                  {{task.getActionVerb()}}: {{task.taskDetails.title}}
                </span>
              </p>
            </a>
          </li>
        </ul>

        <h2>Completed Tasks</h2>
        <ul>
          <li ng-show="state.tasks.complete.length == 0">
            You do not have any completed tasks.
          </li>
          <li ng-repeat="task in state.tasks.complete">
            <a ng-href="{{task.getActionUrl()}}">
              <p class="personnel-task-list-item">
                <span class="icon-check"></span>
                <span class="personnel-task-list-item-title" ng-bind="task.taskDetails.title"></span>
                <span class="personnel-task-list-item-action-date">
                  - {{task.getActionVerbPastTense() | lowercase}} {{task.timestamp | moment:'MMM D, YYYY'}}
                </span>
              </p>
            </a>
          </li>
        </ul>

      </div>
    </div>
  </div>

  <div modal-container></div>
</section>

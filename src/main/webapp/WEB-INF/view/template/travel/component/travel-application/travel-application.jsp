<div ng-controller="TravelApplicationCtrl as vm">

  <ess-notification ng-if="vm.data.isAuthorized === false"
                    level="error"
                    title="You are unauthorized to view this page"
                    message="If necessary please contact the STS Helpline.">
  </ess-notification>

  <div ng-if="vm.data.isAuthorized">
    <div class="travel-hero">
      <h2>Travel Application</h2>
    </div>


    <div class="content-container no-top-margin padding-top-5" ng-if="!vm.data.isLoading">
      <div>
        <ess-app-form-body app="vm.data.app"></ess-app-form-body>
      </div>

      <div class="margin-20">
        <div style="display: flex; flex-direction: column;">

          <div class="app-form-grid">
            <div class="app-form-label">
              Status:
            </div>
            <div class="app-form-s-col">
              <span ess-app-status="vm.data.app"></span>
            </div>
          </div>

          <div class="app-form-grid" ng-if="vm.data.app.status.note">
            <div class="app-form-label">
              Reason:
            </div>
            <div class="app-form-l-col">
              {{::vm.data.app.status.note}}
            </div>
          </div>

        </div>
      </div>

      <div class="travel-button-container">
        <a class="margin-10 margin-right-20" target="_blank"
           ng-href="${ctxPath}/api/v1/travel/application/{{vm.data.app.id}}.pdf">Print</a>
      </div>
    </div>

    <div modal-container>
    </div>

  </div>
</div>

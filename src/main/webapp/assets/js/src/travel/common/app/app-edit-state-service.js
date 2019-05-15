var essTravel = angular.module('essTravel');
essTravel.service('AppEditStateService', [StateService]);

function StateService() {

    this.currState = undefined;

    StateService.prototype.STATES = {
        PURPOSE: 1,
        OUTBOUND: 2,
        RETURN: 3,
        ALLOWANCES: 4,
        REVIEW: 5
    };

    StateService.prototype.nextState = function () {
        if (this.currState < this.STATES.REVIEW) {
            this.currState++;
        }
    };

    StateService.prototype.previousState = function () {
        if (this.currState > this.STATES.PURPOSE) {
            this.currState--;
        }
    };

    StateService.prototype.isPurposeState = function () {
        return this.currState === this.STATES.PURPOSE;
    };

    StateService.prototype.isOutboundState = function () {
        return this.currState === this.STATES.OUTBOUND;
    };

    StateService.prototype.isReturnState = function () {
        return this.currState === this.STATES.RETURN;
    };

    StateService.prototype.isAllowancesState = function () {
        return this.currState === this.STATES.ALLOWANCES;
    };

    StateService.prototype.isReviewState = function () {
        return this.currState === this.STATES.REVIEW;
    };

    StateService.prototype.setPurposeState = function () {
        this.currState = this.STATES.PURPOSE;
    };

    StateService.prototype.setOutboundState = function () {
        this.currState = this.STATES.OUTBOUND;
    };

    StateService.prototype.setReturnState = function () {
        this.currState = this.STATES.RETURN;
    };

    StateService.prototype.setAllowancesState = function () {
        this.currState = this.STATES.ALLOWANCES;
    };

    StateService.prototype.setReviewState = function () {
        this.currState = this.STATES.REVIEW;
    };
};

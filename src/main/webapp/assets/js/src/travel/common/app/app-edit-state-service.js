var essTravel = angular.module('essTravel');
essTravel.service('AppEditStateService', [StateService]);

/**
 * Stores and mutates the page state when editing an application.
 */
function StateService() {

    this.currState = undefined;

    StateService.prototype.STATES = {
        PURPOSE: 1,
        OUTBOUND: 2,
        RETURN: 3,
        ALLOWANCES: 4,
        OVERRIDES: 5,
        REVIEW: 6
    };

    StateService.prototype.isPurposeState = function () {
        return this.currState === this.STATES.PURPOSE;
    };

    StateService.prototype.setPurposeState = function () {
        this.currState = this.STATES.PURPOSE;
    };

    StateService.prototype.isPurposeNavigable = function () {
        return this.STATES.PURPOSE < this.currState;
    };

    StateService.prototype.isOutboundState = function () {
        return this.currState === this.STATES.OUTBOUND;
    };

    StateService.prototype.setOutboundState = function () {
        this.currState = this.STATES.OUTBOUND;
    };

    StateService.prototype.isOutboundNavigable = function () {
        return this.STATES.OUTBOUND < this.currState;
    };

    StateService.prototype.isReturnState = function () {
        return this.currState === this.STATES.RETURN;
    };

    StateService.prototype.setReturnState = function () {
        this.currState = this.STATES.RETURN;
    };

    StateService.prototype.isReturnNavigable = function () {
        return this.STATES.RETURN < this.currState;
    };

    StateService.prototype.isAllowancesState = function () {
        return this.currState === this.STATES.ALLOWANCES;
    };

    StateService.prototype.setAllowancesState = function () {
        this.currState = this.STATES.ALLOWANCES;
    };

    StateService.prototype.isAllowancesNavigable = function () {
        return this.STATES.ALLOWANCES < this.currState;
    };

    StateService.prototype.isOverridesState = function () {
        return this.currState == this.STATES.OVERRIDES;
    };

    StateService.prototype.setOverridesState = function () {
        this.currState = StateService.prototype.STATES.OVERRIDES;
    };

    StateService.prototype.isOverridesNavigable = function () {
        return this.STATES.OVERRIDES < this.currState;
    };

    StateService.prototype.isReviewState = function () {
        return this.currState === this.STATES.REVIEW;
    };

    StateService.prototype.setReviewState = function () {
        this.currState = this.STATES.REVIEW;
    };

    StateService.prototype.isReviewNavigable = function () {
        return this.STATES.REVIEW < this.currState;
    };
};

import React from "react";
import BusyRegion from "app/components/BusyRegion";
import ErrorAlert from "app/components/ErrorAlert";
import { useForm } from "react-hook-form";
import Button from "app/components/Button";
import { useSubmitVideoCodes } from "app/views/myinfo/personnel/pec/useTaskAssignment";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";

export default function VideoCodeEntryForm({
  taskId,
  onSuccess,
  onPendingChange,
}) {
  const { data: user } = useRequireAuthedUser();
  const submitVideoCodesApi = useSubmitVideoCodes();
  React.useEffect(() => {
    onPendingChange?.(submitVideoCodesApi.isPending);
    return () => onPendingChange?.(false);
  }, [submitVideoCodesApi.isPending, onPendingChange]);
  const {
    register,
    handleSubmit,
    setError,
    clearErrors,
    formState: { errors },
  } = useForm();

  const onSubmit = (data) => {
    clearErrors("root");
    return submitVideoCodesApi
      .mutateAsync({
        codes: [data.firstCode, data.secondCode],
        empId: user.employeeId,
        taskId: taskId,
      })
      .then(onSuccess)
      .catch((err) => {
        if (err.data?.errorCode === "INVALID_PEC_CODE") {
          // One or more of the codes submitted were invalid.
          setError("firstCode", {
            type: "invalid",
            message: "",
          });
          setError("secondCode", {
            type: "invalid",
            message: "",
          });
        } else {
          setError("root.server", {
            message: "Unable to submit codes. Please try again.",
          });
        }
      });
  };

  return (
    <BusyRegion
      message={
        submitVideoCodesApi.isPending ? "Submitting training codes…" : null
      }
    >
      <form onSubmit={handleSubmit(onSubmit)} className="pb-3">
        {errors.root?.server && (
          <ErrorAlert title={errors.root.server.message} />
        )}
        <div className="my-3 flex items-center justify-center">
          {(errors.firstCode?.type === "invalid" ||
            errors.secondCode?.type === "invalid") && (
            <ErrorAlert title="Check your training codes">
              One or more of the submitted codes were incorrect. Please double
              check them and resubmit.
            </ErrorAlert>
          )}
        </div>

        <div className="grid grid-cols-3 items-center gap-2">
          <div></div>
          <div className="flex items-center justify-start">
            <label
              className="w-24 text-left font-semibold text-teal-700"
              htmlFor="firstCode"
            >
              First Code
            </label>
            <input
              id="firstCode"
              aria-invalid={Boolean(errors.firstCode)}
              aria-describedby={
                errors.firstCode ? "firstCode-error" : undefined
              }
              {...register("firstCode", {
                required: "First code is required",
              })}
              autoComplete="off"
              className={`input ${errors.firstCode ? "input--invalid" : ""}`}
            />
          </div>
          <div>
            {errors.firstCode && (
              <p id="firstCode-error" role="alert" className="text-red-500">
                {errors.firstCode.message}
              </p>
            )}
          </div>

          <div></div>
          <div className="flex items-center justify-start">
            <label
              className="w-24 font-semibold text-teal-700"
              htmlFor="secondCode"
            >
              Second Code
            </label>
            <input
              id="secondCode"
              aria-invalid={Boolean(errors.secondCode)}
              aria-describedby={
                errors.secondCode ? "secondCode-error" : undefined
              }
              {...register("secondCode", {
                required: "Second code is required",
              })}
              autoComplete="off"
              className={`input ${errors.secondCode ? "input--invalid" : ""}`}
            />
          </div>
          <div>
            {errors.secondCode && (
              <p id="secondCode-error" role="alert" className="text-red-500">
                {errors.secondCode.message}
              </p>
            )}
          </div>

          <div></div>
          <div className="justify-self-center">
            <Button
              type="submit"
              variant="primary"
              isPending={submitVideoCodesApi.isPending}
            >
              Submit
            </Button>
          </div>
          <div></div>
        </div>
      </form>
    </BusyRegion>
  );
}

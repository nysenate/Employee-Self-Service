import React from "react";
import { useForm } from "react-hook-form";
import Button from "app/components/Button";
import { useSubmitVideoCodes } from "app/views/myinfo/personnel/pec/useTaskAssignment";
import useAuthedUser from "app/hooks/useAuthedUser";

export default function VideoCodeEntryForm({ taskId, onSuccess }) {
  const { data: user } = useAuthedUser();
  const submitVideoCodesApi = useSubmitVideoCodes();
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm();

  const onSubmit = (data) => {
    submitVideoCodesApi
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
          // TODO will this trigger error boundary once merged?
          throw err;
        }
      });
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="pb-3">
      <div className="my-3 flex items-center justify-center">
        {(errors.firstCode?.type === "invalid" ||
          errors.secondCode?.type === "invalid") && (
          <p className="text-red-500">
            One or more of the submitted codes were incorrect. Please double
            check them and resubmit.
          </p>
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
            {...register("firstCode", {
              required: "First code is required",
            })}
            autoComplete="off"
            className={`input ${errors.firstCode ? "input--invalid" : ""}`}
          />
        </div>
        <div>
          {errors.firstCode && (
            <p className="text-red-500">{errors.firstCode.message}</p>
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
            {...register("secondCode", {
              required: "Second code is required",
            })}
            autoComplete="off"
            className={`input ${errors.secondCode ? "input--invalid" : ""}`}
          />
        </div>
        <div>
          {errors.secondCode && (
            <p className="text-red-500">{errors.secondCode.message}</p>
          )}
        </div>

        <div></div>
        <div className="justify-self-center">
          <Button type="submit" color="success">
            Submit
          </Button>
        </div>
        <div></div>
      </div>
    </form>
  );
}

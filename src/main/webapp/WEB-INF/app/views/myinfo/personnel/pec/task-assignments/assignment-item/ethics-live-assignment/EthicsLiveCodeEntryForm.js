import React from "react";
import { useForm } from "react-hook-form";
import Button from "app/components/Button";
import { useSubmitEthicsLiveForm } from "app/views/myinfo/personnel/pec/useTaskAssignment";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";

export default function EthicsLiveCodeEntryForm({ taskId, onSuccess }) {
  const { data: user } = useRequireAuthedUser();
  const submitEthicsLiveCodesApi = useSubmitEthicsLiveForm();
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm();

  const onSubmit = (data) => {
    submitEthicsLiveCodesApi
      .mutateAsync({
        trainingDate: data.trainingDate,
        codes: [data.firstCode, data.secondCode],
        empId: user.employeeId,
        taskId: taskId,
      })
      .then(onSuccess)
      .catch((err) => {
        if (err.data?.errorCode === "INVALID_PEC_CODE") {
          // One or more of the codes submitted were invalid.
          setError("trainingDate", {
            type: "invalid",
            message: "",
          });
          setError("firstCode", {
            type: "invalid",
            message: "",
          });
          setError("secondCode", {
            type: "invalid",
            message: "",
          });
        } else {
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
            The submitted codes were incorrect for the selected date. Please
            double check them and resubmit.
          </p>
        )}
      </div>

      <div className="grid grid-cols-3 items-center gap-2">
        <div></div>
        <div className="flex items-center justify-start">
          <label
            className="w-24 text-left font-semibold text-teal-700"
            htmlFor="trainingDate"
          >
            Training Date
          </label>
          <input
            {...register("trainingDate", {
              required: "Training date is required",
            })}
            type="date"
            className={`input ${errors.trainingDate ? "input--invalid" : ""}`}
          />
        </div>
        <div>
          {errors.trainingDate && (
            <p className="text-red-500">{errors.trainingDate.message}</p>
          )}
        </div>

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
          <Button type="submit" variant="primary">
            Submit
          </Button>
        </div>
        <div></div>
      </div>
    </form>
  );
}

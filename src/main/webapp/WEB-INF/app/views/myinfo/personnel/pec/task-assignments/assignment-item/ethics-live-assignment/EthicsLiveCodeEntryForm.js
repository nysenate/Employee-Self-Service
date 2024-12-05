import React from 'react';
import { useForm } from "react-hook-form";
import { Button } from "app/components/Button";
import useAuth from "app/contexts/Auth/useAuth";
import { useSubmitEthicsLiveForm } from "app/views/myinfo/personnel/pec/useTaskAssignment";


export default function EthicsLiveCodeEntryForm({ taskId, onSuccess }) {
  const auth = useAuth();
  const submitEthicsLiveCodesApi = useSubmitEthicsLiveForm()
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm();

  const onSubmit = (data) => {
    submitEthicsLiveCodesApi.mutateAsync({
      trainingDate: data.trainingDate,
      codes: [data.firstCode, data.secondCode],
      empId: auth.empId(),
      taskId: taskId,
    })
      .then(onSuccess)
      .catch((err) => {
        if (err.data?.errorCode === "INVALID_PEC_CODE") {
          // One or more of the codes submitted were invalid.
          setError("trainingDate", {
            type: "invalid",
            message: ""
          })
          setError("firstCode", {
            type: "invalid",
            message: ""
          })
          setError("secondCode", {
            type: "invalid",
            message: ""
          })
        } else {
          throw err;
        }
      });
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="pb-3">
      <div className="flex justify-center items-center my-3">
        {(errors.firstCode?.type === "invalid" || errors.secondCode?.type === "invalid") &&
          <p className="text-red-500">
            The submitted codes were incorrect for the selected date. Please double check them and resubmit.
          </p>
        }
      </div>

      <div className="grid grid-cols-3 gap-2 items-center">
        <div></div>
        <div className="flex justify-start items-center">
          <label className="text-left text-teal-700 font-semibold w-24" htmlFor="trainingDate">Training Date</label>
          <input
            {...register("trainingDate", {
              required: "Training date is required"
            })}
            type="date"
            className={`input ${errors.trainingDate ? "input--invalid" : ""}`}
          />
        </div>
        <div>
          {errors.trainingDate && (
            <p className="text-red-500">
              {errors.trainingDate.message}
            </p>
          )}
        </div>

        <div></div>
        <div className="flex justify-start items-center">
          <label className="text-left text-teal-700 font-semibold w-24" htmlFor="firstCode">First Code</label>
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
            <p className="text-red-500">
              {errors.firstCode.message}
            </p>
          )}
        </div>

        <div></div>
        <div className="flex justify-start items-center">
          <label className="text-teal-700 font-semibold w-24" htmlFor="secondCode">Second Code</label>
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
            <p className="text-red-500">
              {errors.secondCode.message}
            </p>
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

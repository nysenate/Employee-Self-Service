import React from "react"
import ContactLabel from "app/views/myinfo/personnel/emergency-alert-info/ContactLabel";
import ErrorText from "app/views/myinfo/personnel/emergency-alert-info/ErrorText";

export default function EmailInputs({ register, errors }) {
  return (
    <>
      <div className="row-span-4 text-center">
        <h3 className="text-xl font-semibold">Email</h3>
      </div>

      <div className="col-span-3">
        <ContactLabel id="workEmail">Work</ContactLabel>
        <EmailInput id="workEmail" register={register} errors={errors}/>
        <ErrorText id="workEmail" errors={errors}/>
      </div>

      <div className="col-span-3">
        <ContactLabel id="personalEmail">Personal</ContactLabel>
        <EmailInput id="personalEmail" register={register} errors={errors}/>
        <ErrorText id="personalEmail" errors={errors}/>
      </div>

      <div className="col-span-3">
        <ContactLabel id="alternateEmail">Alternate</ContactLabel>
        <EmailInput id="alternateEmail" register={register} errors={errors}/>
        <ErrorText id="alternateEmail" errors={errors}/>
      </div>
    </>
  )
}

function EmailInput({ id, register, errors }) {
  return (
    <input id={id}
           name={id}
           className={`input mx-3 ${errors[id] ? "input--invalid" : ""}`}
           type="email"
           {...register(id, {
             pattern: {
               value: /^\S+@\S+\.[A-z]{2,}$/,
               message: "Please enter a valid email address"
             },
           })}
    />
  )
}

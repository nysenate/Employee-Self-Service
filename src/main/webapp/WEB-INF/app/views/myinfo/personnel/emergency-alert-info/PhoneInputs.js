import React from "react"
import ContactLabel from "app/views/myinfo/personnel/emergency-alert-info/ContactLabel";
import ErrorText from "app/views/myinfo/personnel/emergency-alert-info/ErrorText";

export default function PhoneInputs({ register, errors }) {
  return (
    <>
      <div className="row-span-4 text-center">
        <h3 className="text-xl font-semibold">Phone</h3>
      </div>

      <div className="col-span-3">
        <ContactLabel id="workPhone">Work</ContactLabel>
        <PhoneInput id="workPhone" register={register} errors={errors} readOnly/>
      </div>

      <div className="col-span-3">
        <ContactLabel id="homePhone">Home</ContactLabel>
        <PhoneInput id="homePhone" register={register} errors={errors}/>
        <ErrorText id="homePhone" errors={errors}/>
      </div>

      <div className="col-span-3">
        <ContactLabel id="alternatePhone">Alternate</ContactLabel>
        <PhoneInput id="alternatePhone" register={register} errors={errors}/>
        <PhoneContactOptionsSelect name="alternateOptions" register={register}/>
        <ErrorText id="alternatePhone" errors={errors}/>
      </div>

      <div className="col-span-3">
        <ContactLabel id="mobilePhone">Mobile</ContactLabel>
        <PhoneInput id="mobilePhone" register={register} errors={errors}/>
        <PhoneContactOptionsSelect name="mobileOptions" register={register}/>
        <ErrorText id="mobilePhone" errors={errors}/>
      </div>
    </>
  )
}

function PhoneInput({ id, register, errors, readOnly = false }) {
  return (
    <input id={id}
           name={id}
           className={`${!readOnly && 'input'} mx-3 ${errors[id] ? "input--invalid" : ""}`}
           {...register(id, {
             pattern: {
               value: /^ *(\([0-9]{3}\)|[0-9]{3} *-?) *[0-9]{3} *-? *[0-9]{4} *$/,
               message: "Please enter a valid phone number",
             }
           })}
           type="tel"
           readOnly={readOnly}
    />
  )
}

function PhoneContactOptionsSelect({ name, register }) {
  return (
    <select name={name} className="select" {...register(name)}>
      <option value="Only calls">Only calls</option>
      <option value="Only texts">Only texts</option>
      <option value="Both calls and texts">Both calls and texts</option>
    </select>
  )
}

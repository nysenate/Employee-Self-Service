import React from "react"
import { Form, Router } from "react-router-dom";
import { useForm } from "react-hook-form";
import { fetchApiJson } from "app/utils/fetchJson";


export default function AlertInfoForm({ alertInfo }) {
  const useFormDefaultProps = {
    mode: "onBlur",
    defaultValues: {
      workPhone: formatPhoneNumber(alertInfo.workPhone) || "",
      homePhone: formatPhoneNumber(alertInfo.homePhone) || "",
      alternatePhone: formatPhoneNumber(alertInfo.alternatePhone) || "",
      alternateOptions: alertInfo.alternateOptions || "Both calls and texts",
      mobilePhone: formatPhoneNumber(alertInfo.mobilePhone) || "",
      mobileOptions: alertInfo.mobileOptions || "Both calls and texts",
      workEmail: alertInfo.workEmail || "",
      personalEmail: alertInfo.personalEmail || "",
      alternateEmail: alertInfo.alternateEmail || "",
    }
  }
  const [ errorMsg, setErrorMsg ] = React.useState("")
  const {
    register,
    handleSubmit,
    formState: { errors, dirtyFields, isValid },
    reset,
    setError
  } = useForm(useFormDefaultProps)

  React.useEffect(() => {
    reset(useFormDefaultProps)
  }, [ alertInfo ])

  const onSubmit = data => {
    setErrorMsg("")
    data.empId = alertInfo.empId

    // Check for and notify user of any duplicate phone numbers.
    const duplicatePhoneFields = duplicatePhoneNumbers(data)
    if (duplicatePhoneFields.length > 0) {
      duplicatePhoneFields.forEach(
        field => setError(field, { type: 'custom', message: "Please remove duplicate phone numbers" }))
    }

    // Check for and notify user of any duplicate email addresses.
    const duplicateEmailFields = duplicateEmails(data)
    if (duplicateEmailFields.length > 0) {
      duplicateEmailFields.forEach(
        field => setError(field, { type: 'custom', message: "Please remove duplicate email addresses" }))
    }

    if (duplicatePhoneFields.length > 0 || duplicateEmailFields.length > 0) {
      // Don't submit the form if there were any duplicates.
      return
    }

    fetchApiJson(`/alert-info`, { method: "POST", payload: data })
      .then((res) => Router.replace(Router.asPath))
      .catch((err) => setErrorMsg(err.data.message))
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div className="p-3 grid grid-cols-4 gap-3">

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

        <button type="submit">SAVE</button>
      </div>
    </form>
  )
}

function ContactLabel({ id, children }) {
  return (
    <label htmlFor={id}
           className="inline-block w-16 text-right text-teal-700 font-semibold">
      {children}
    </label>
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

function ErrorText({ id, errors }) {
  if (errors[id]?.message) {
    return (
      <div className="inline-block">
        <p className="pl-1 mt-0.5 text-red-600 inline-block">{errors[id]?.message}</p>
      </div>
    )
  }

  return null
}

/**
 * Formats a phone number in a format like "(518) 123-4567".
 * @param phoneNumberString {string} A string representation of a phone number.
 * @returns {string|null} The formatted phone number or null if the phoneNumberString was an invalid phone number.
 */
function formatPhoneNumber(phoneNumberString) {
  const cleanNumber = (phoneNumberString ?? "").replaceAll(/\D/ig, "")
  const match = cleanNumber.match(/^(\d{3})(\d{3})(\d{4})$/);
  if (match) {
    return `(${match[1]}) ${match[2]}-${match[3]}`
  }
  return null;
}

/**
 * Performs a comparison of all phone numbers in the given formData.
 * @param formData
 * @returns {string[]} An array of the field names that are duplicates. If no duplicates were found this array is empty.
 */
const duplicatePhoneNumbers = (formData) => {
  const phoneNumberFields = [ "workPhone", "homePhone", "alternatePhone", "mobilePhone" ]
  const phoneNumbers = phoneNumberFields.map(f => formatPhoneNumber(formData[f]))
  const duplicateNumbers = findDuplicates(phoneNumbers)
  return phoneNumberFields.filter(field => duplicateNumbers.includes(formatPhoneNumber(formData[field])))
}

/**
 * Performs a case-insensitive comparison of all emails in the given formData.
 * @param formData
 * @returns {string[]} An array of the field names that are duplicates. If no duplicates were found this array is empty.
 */
const duplicateEmails = (formData) => {
  const cleanEmail = email => email?.trim().toLowerCase()
  const emailFields = [ "workEmail", "personalEmail", "alternateEmail" ]
  const emailAddresses = emailFields.map(e => cleanEmail(formData[e] ?? ""))
  const duplicateEmailAddresses = findDuplicates(emailAddresses)
  return emailFields.filter(field => duplicateEmailAddresses.includes(cleanEmail(formData[field])))
}

const findDuplicates = arr => arr.filter((item, index) => arr.indexOf(item) != index)

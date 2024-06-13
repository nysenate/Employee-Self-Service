import React from "react"
import { useForm } from "react-hook-form";
import PhoneInputs from "app/views/myinfo/personnel/emergency-alert-info/PhoneInputs";
import EmailInputs from "app/views/myinfo/personnel/emergency-alert-info/EmailInputs";
import { Button } from "app/components/Button";
import { useMutateAlertInfo } from "app/api/alertInfoApi";


export default function AlertInfoForm({ alertInfo }) {
  const mutateAlertInfo = useMutateAlertInfo()
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
        field => setError(field, { type: 'custom', message: "Remove duplicate phone numbers" }))
    }

    // Check for and notify user of any duplicate email addresses.
    const duplicateEmailFields = duplicateEmails(data)
    if (duplicateEmailFields.length > 0) {
      duplicateEmailFields.forEach(
        field => setError(field, { type: 'custom', message: "Remove duplicate email addresses" }))
    }

    if (duplicatePhoneFields.length > 0 || duplicateEmailFields.length > 0) {
      // Don't submit the form if there were any duplicates.
      return
    }

    mutateAlertInfo.mutate(data, { empId: data.empId })
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div className="p-3 grid grid-cols-4 gap-3">

        <PhoneInputs register={register} errors={errors}/>
        {/*Spacer*/}
        <div className="col-span-4 my-3"></div>

        <EmailInputs register={register} errors={errors}/>

        {/*Submit Button*/}
        <div className="text-center py-3">
          <Button type="submit"
                  color="success"
                  disabled={Object.keys(dirtyFields).length <= 0 || !isValid}>
            Save
          </Button>
        </div>
      </div>
    </form>
  )
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
  return "";
}

/**
 * Performs a comparison of all phone numbers in the given formData.
 * @param formData
 * @returns {string[]} An array of the field names that are duplicates. If no duplicates were found this array is empty.
 */
const duplicatePhoneNumbers = (formData) => {
  const phoneNumberFields = [ "workPhone", "homePhone", "alternatePhone", "mobilePhone" ]
  const phoneNumbers = phoneNumberFields.map(f => formatPhoneNumber(formData[f]))
    .filter(f => f !== "") // don't check empty phone numbers for duplicates.
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
    .filter(e => e !== "") // don't check empty emails for duplicates.
  const duplicateEmailAddresses = findDuplicates(emailAddresses)
  return emailFields.filter(field => duplicateEmailAddresses.includes(cleanEmail(formData[field])))
}

const findDuplicates = arr => arr.filter((item, index) => arr.indexOf(item) != index)

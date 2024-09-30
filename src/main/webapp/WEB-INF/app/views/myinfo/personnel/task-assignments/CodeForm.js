import React, { useState } from 'react';
import { loadAuth } from "app/contexts/Auth/authStorage";
import { useNavigate } from "react-router-dom";

const CodeForm = ({ taskId, onError, onLoading }) => {

  const navigate = useNavigate();
  const [ firstCode, setFirstCode ] = useState('');
  const [ secondCode, setSecondCode ] = useState('');
  const [ date, setDate ] = useState('');
  const { empId } = loadAuth();


  const [ formData, setFormData ] = useState({
    codes: [],
    empId: empId,
    trainingDate: '',
    taskId: taskId,
  });

  const [ error, setError ] = useState(null); // State to manage error state
  const isFormValid = firstCode && secondCode && date;

  const handleSubmit = async (event) => {
    event.preventDefault();

    // Validate form fields
    if (!firstCode || !secondCode || !date) {
      setError('Please fill in all fields.');
      return;
    }

    // Prepare form data
    const updatedFormData = {
      ...formData,
      codes: [ firstCode, secondCode ],
      trainingDate: new Date(date).toISOString(),
    };

    setFormData(updatedFormData);
    onError(null); // Clear any previous errors

    try {
      const url = `/api/v1/personnel/task/ethics/live/code`;
      const init = {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json",
        },
        body: JSON.stringify(updatedFormData),
      };

      const response = await fetch(url, init);

      if (!response.ok) {
        onError(true);
        throw new Error('Network response was not ok');
      }

      setError(false);
      console.log('Form submitted successfully');
      setTimeout(() => {
        onLoading(true);
        navigate('/myinfo/personnel/todo');
      }, 2000);
    } catch (error) {
      console.error('Error submitting the form:', error);
      setError('Failed to submit the form. Please try again later.');
    } finally {
      console.log("OKOK")
      onLoading(false)
    }
  };

  return (
    <>
      <div className="flex items-center justify-center">
        <form onSubmit={handleSubmit} className="bg-white p-1 rounded w-80">

            <div className="mb-4">
              <label htmlFor="date" className="block text-left text-md mb-2">Date of Training:
                <input
                  type="date"
                  id="date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                  className="border border-gray-300 p-1 rounded ml-3"
                  required
                />
              </label>
            </div>
          <div className="mb-4">
            <label htmlFor="firstCode" className="block text-left text-md mb-2">First Code:
              <input
                type="text"
                id="firstCode"
                value={firstCode}
                onChange={(e) => setFirstCode(e.target.value)}
                className="border border-gray-300 p-1 rounded ml-11"
                required
              />
            </label>
          </div>
          <div className="mb-4">
            <label htmlFor="secondCode" className="block text-left text-md mb-2">Second Code:
              <input
                type="text"
                id="secondCode"
                value={secondCode}
                onChange={(e) => setSecondCode(e.target.value)}
                className="border border-gray-300 p-1 rounded ml-7"
                required
              />
            </label>
          </div>
          <button
            type="submit"
            disabled={!isFormValid}
            className={`w-[6em] py-2 rounded text-white font-semibold ${
              isFormValid ? 'bg-green-500 hover:bg-green-600' : 'bg-green-200 cursor-not-allowed'}`}>
            Submit
          </button>
          <span className="ml-2 text-black-75">You must enter all codes to submit.</span>
        </form>
      </div>
    </>
  );
};

export default CodeForm;

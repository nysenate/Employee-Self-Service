import React, { useEffect, useRef } from 'react';
import styles from "app/views/time/universalStyles.module.css";
const EmployeeList = ({ searchResults, getMore, handleSelectEmp }) => {
  const ulRef = useRef(null);

  useEffect(() => {
    const handleScroll = () => {
      if (ulRef.current) {
        const { scrollTop, scrollHeight, clientHeight } = ulRef.current;
        if (scrollTop + clientHeight >= scrollHeight - 5) {
          getMore();
        }
      }
    };

    const ulElement = ulRef.current;
    if (ulElement) {
      ulElement.addEventListener('scroll', handleScroll);
    }

    // Cleanup the event listener on component unmount
    return () => {
      if (ulElement) {
        ulElement.removeEventListener('scroll', handleScroll);
      }
    };
  }, [getMore]);

  return (
    <div className={styles.employeeSearchResults}>
      <ul ref={ulRef} style={{ height: '400px', overflowY: 'auto' }}>
        {searchResults.map((emp, index) => (
          <li key={index} onClick={() => handleSelectEmp(emp)}>
            {emp.fullName}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default EmployeeList;

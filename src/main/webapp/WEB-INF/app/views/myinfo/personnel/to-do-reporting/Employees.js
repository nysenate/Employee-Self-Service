import React, { useEffect, useState } from 'react';

export default function Employees() {
  const [isSelected, setIsSelected] = useState(false);

  useEffect(() => {

  }, []);

  return (
    <div className="mt-2 mr-10">
      <table className={"table table-fixed table-striped"}>
        <thead>
        <tr className={"text-left text-sm font-semibold"}>
          <th className="w-1/6 text-left">Completed/ Assigned</th>
          <th className="w-2/6 text-left">Name</th>
          <th className="w-3/6 text-left">Office</th>
        </tr>
        </thead>
        <tbody>

        <tr className="cursor-pointer hover:bg-gray-80" onClick={(e) => {setIsSelected(true)
        console.log(e)}}>
          <td>0/1</td>
          <td>Malcolm Lockyer</td>
          <td>jasojhdjksdjknjkdwcnkj</td>
        </tr>

        <tr>
          <td>0/1</td>
          <td>The Eagles</td>
          <td>1972</td>
        </tr>
        <tr>
          <td>0/1</td>
          <td>Earth, Wind, and Fire</td>
          <td>1975</td>
        </tr>
        </tbody>
      </table>
    </div>
  );
}
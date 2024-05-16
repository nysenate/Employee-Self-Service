import React from "react"
import useAuth from "app/core/Auth/useAuth";
import SummaryTitle from "app/views/myinfo/personnel/summary/SummayTitle";
import Card from "app/components/Card";
import PersonnelInfo from "app/views/myinfo/personnel/summary/PersonnelInfo";
import OrganizationInfo from "app/views/myinfo/personnel/summary/OrganizationInfo";
import PayrollInfo from "app/views/myinfo/personnel/summary/PayrollInfo";
import { FederalTax, NewYorkCityTax, StateTax, YonkersTax } from "app/views/myinfo/personnel/summary/TaxInfo";


export default function SummaryIndex() {
  const auth = useAuth();
  const [ emp, setEmp ] = React.useState()
  const [ transactions, setTransactions ] = React.useState()

  React.useEffect(() => {
    getEmpDetails(auth.empId())
      .then((res) => setEmp(res))
    getEmpTransactionSnapshot(auth.empId())
      .then((res) => setTransactions(res))
  }, [])


  if (!emp || !transactions) {
    // TODO
    return null
  }

  return (
    <div>
      <SummaryTitle emp={emp}/>
      <Card className="p-4 mt-0.5">
        <div className="grid grid-cols-2 gap-4">
          <LeftColumn emp={emp} transactions={transactions}/>
          <RightColumn emp={emp} transactions={transactions}/>
        </div>
      </Card>
    </div>
  )
}

function LeftColumn({ emp, transactions }) {
  return (
    <div>
      <PersonnelInfo emp={emp} transactions={transactions}/>
      <OrganizationInfo emp={emp} transactions={transactions}/>
    </div>
  )
}

function RightColumn({ emp, transactions }) {
  return (
    <div>
      <PayrollInfo emp={emp} transactions={transactions}/>
      <FederalTax transactions={transactions}/>
      <StateTax transactions={transactions}/>
      <NewYorkCityTax transactions={transactions}/>
      <YonkersTax transactions={transactions}/>
    </div>
  )
}


async function getEmpDetails(empId) {
  const res = await fetch(
    `/api/v1/employees?detail=true&empId=${empId}`,
    {
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
      },
      cache: 'no-store',
    })
  const data = await res.json()
  return data.employee
}

async function getEmpTransactionSnapshot(empId) {
  const res = await fetch(
    `/api/v1/empTransactions/snapshot/current?empId=${empId}`,
    {
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
      },
      cache: 'no-store',
    })
  const data = await res.json()
  return data.snapshot.items
}
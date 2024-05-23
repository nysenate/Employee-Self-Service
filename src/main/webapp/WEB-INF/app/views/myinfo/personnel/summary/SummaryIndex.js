import React from "react"
import SummaryTitle from "app/views/myinfo/personnel/summary/SummayTitle";
import Card from "app/components/Card";
import PersonnelInfo from "app/views/myinfo/personnel/summary/PersonnelInfo";
import OrganizationInfo from "app/views/myinfo/personnel/summary/OrganizationInfo";
import PayrollInfo from "app/views/myinfo/personnel/summary/PayrollInfo";
import { FederalTax, NewYorkCityTax, StateTax, YonkersTax } from "app/views/myinfo/personnel/summary/TaxInfo";
import { fetchApiJson } from "app/utils/fetchJson";
import useAuth from "app/core/Auth/useAuth";


const getEmpDetails = async empId => {
  return await fetchApiJson(`/employees?detail=true&empId=${empId}`)
    .then((body) => body.employee)
}

const getEmpTransactionSnapshot = async empId => {
  return await fetchApiJson(`/empTransactions/snapshot/current?empId=${empId}`)
    .then((body) => body.snapshot.items)
}

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

import React from "react"
import SummaryTitle from "app/views/myinfo/personnel/summary/SummayTitle";
import Card from "app/components/Card";
import PersonnelInfo from "app/views/myinfo/personnel/summary/PersonnelInfo";
import OrganizationInfo from "app/views/myinfo/personnel/summary/OrganizationInfo";
import PayrollInfo from "app/views/myinfo/personnel/summary/PayrollInfo";
import { FederalTax, NewYorkCityTax, StateTax, YonkersTax } from "app/views/myinfo/personnel/summary/TaxInfo";
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useEmployeeDetails } from "app/api/employeeDetailsApi";
import { useEmployeeTransactions } from "app/api/employeeTransactionsApi";


export default function SummaryIndex() {
  const auth = useAuth();
  const empDetails = useEmployeeDetails(auth.empId())
  const transactions = useEmployeeTransactions(auth.empId())

  if (empDetails.isPending || transactions.isPending) {
    return <LoadingIndicator/>
  }

  return (
    <div>
      <SummaryTitle emp={empDetails.data}/>
      <Card className="p-4 mt-0.5">
        <div className="grid grid-cols-2 gap-4">
          <LeftColumn emp={empDetails.data} transactions={transactions.data}/>
          <RightColumn emp={empDetails.data} transactions={transactions.data}/>
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

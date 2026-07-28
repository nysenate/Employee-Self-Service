import React from "react";
import SummaryTitle from "app/views/myinfo/personnel/summary/SummaryTitle";
import Card from "app/components/Card";
import PersonnelInfo from "app/views/myinfo/personnel/summary/PersonnelInfo";
import OrganizationInfo from "app/views/myinfo/personnel/summary/OrganizationInfo";
import PayrollInfo from "app/views/myinfo/personnel/summary/PayrollInfo";
import {
  FederalTax,
  NewYorkCityTax,
  StateTax,
  YonkersTax,
} from "app/views/myinfo/personnel/summary/TaxInfo";
import LoadingIndicator from "app/components/LoadingIndicator";
import { useEmployee } from "app/views/useEmployee";
import { useEmployeeTransactions } from "app/views/myinfo/personnel/summary/useEmployeeTransactions";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";

export default function SummaryIndex() {
  const { data: user, isPending: isUserPending } = useRequireAuthedUser();
  const empDetails = useEmployee(user?.employeeId);
  const transactions = useEmployeeTransactions(user?.employeeId);

  if (isUserPending || empDetails.isPending || transactions.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      <SummaryTitle emp={empDetails.data} />
      <Card className="mt-0.5 p-4">
        <div className="grid grid-cols-2 gap-4">
          <LeftColumn emp={empDetails.data} transactions={transactions.data} />
          <RightColumn emp={empDetails.data} transactions={transactions.data} />
        </div>
      </Card>
    </div>
  );
}

function LeftColumn({ emp, transactions }) {
  return (
    <div>
      <PersonnelInfo emp={emp} transactions={transactions} />
      <OrganizationInfo emp={emp} transactions={transactions} />
    </div>
  );
}

function RightColumn({ emp, transactions }) {
  return (
    <div>
      <PayrollInfo emp={emp} transactions={transactions} />
      <FederalTax transactions={transactions} />
      <StateTax transactions={transactions} />
      <NewYorkCityTax transactions={transactions} />
      <YonkersTax transactions={transactions} />
    </div>
  );
}

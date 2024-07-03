import React, { useState } from 'react';
import Hero from "../../../components/Hero";
import FilterSelect from "../../../components/FilterSelect";
export default function ReconciliationIndex () {
    return (
        <div>
            <Hero>Reconciliation/Testing Filter</Hero>
          <div style={{ width: '200px' }}>
            <FilterSelect
              data={itemsData}
              valueField="commodityCode"
              labelField="description"
              initialItem={itemsData[0]}
            />
          </div>
          <div style={{ width: '200px' }}>
            <FilterSelect
              data={destinationsData}
              valueField="locId"
              labelField="location"
              initialItem={destinationsData[0]}
            />
          </div>
          <div style={{ width: '400px' }}>
            <FilterSelect
              data={destinationsData}
              valueField="locId"
              labelField="location"
            />
          </div>
        </div>
    );
};

const itemsData = [
  {
    id: 2205,
    commodityCode: "AA",
    description: "\"AA\" CELL BATTERIES",
    unit: "EACH",
    category: "BATTERIES",
    perOrderAllowance: 24,
    perMonthAllowance: 24,
    unitQuantity: 1,
    reconciliationPage: 2,
    inventoryTracked: true,
    visible: false,
    specialRequest: true,
    expendable: true,
    restricted: false,
    requiresSynchronization: true
  },
  {
    id: 1094,
    commodityCode: "AAABATTERIES",
    description: "\"AAA\" CELL BATTERIES.",
    unit: "EACH",
    category: "BATTERIES",
    perOrderAllowance: 3,
    perMonthAllowance: 3,
    unitQuantity: 1,
    reconciliationPage: 2,
    inventoryTracked: true,
    visible: false,
    specialRequest: true,
    expendable: true,
    restricted: false,
    requiresSynchronization: true
  }
];
const destinationsData = [
  { locId: 'A411F-W', location: '11TH FLOOR, AG4' },
  { locId: 'B512G-X', location: '12TH FLOOR, BG5' }
];
import React, { useState } from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import RequisitionFormIndex from "app/views/supply/requisition/RequisitionFormIndex";
import ShoppingCartIndex from "app/views/supply/cart/ShoppingCartIndex";
import OrderHistoryIndex from "app/views/supply/orderhistory/OrderHistoryIndex";
import OrderDetail from "app/views/supply/orderhistory/OrderDetail";
import FulfillmentIndex from "app/views/supply/fulfillment/FulfillmentIndex";
import ReconciliationIndex from "app/views/supply/reconciliation/ReconciliationIndex";
import RequisitionHistoryIndex from "app/views/supply/requisitionhistory/RequisitionHistoryIndex";
import ItemHistoryIndex from "app/views/supply/itemhistory/ItemHistoryIndex";
import AppLayout from "app/components/AppLayout";
import Navigation from "app/components/Navigation";
import Card from "app/components/Card";
import CategoryCard from "./requisition/CategoryCard";
import styles from "./universalStyles.module.css";

export default function SupplyRouter() {
  const [categories, setCategories] = useState([]);

  return (
      <ThemeContext.Provider value={themes.supply}>
        <Routes>
          <Route path="" element={<SupplyLayout categories={categories}/>}>
            <Route path="requisition-form" element={<RequisitionFormIndex setCategories={setCategories}/>}/>
            <Route path="cart" element={<ShoppingCartIndex/>}/>
            <Route path="order-history/order/:orderId" element={<OrderDetail/>}/>
            <Route path="order-history" element={<OrderHistoryIndex/>}/>
            <Route path="fulfillment" element={<FulfillmentIndex/>}/>
            <Route path="reconciliation" element={<ReconciliationIndex/>}/>
            <Route path="requisition-history" element={<RequisitionHistoryIndex/>}/>
            <Route path="item-history" element={<ItemHistoryIndex/>}/>
            <Route path="" element={<Navigate to="requisition-form" replace/>}/>
            <Route path="*" element={<div>404</div>}/>
          </Route>
        </Routes>
      </ThemeContext.Provider>
  );
}

function SupplyLayout({ categories }) {
  const location = useLocation();

  return (
      <AppLayout>
        <Navigation notWrapInCard={true}>
          <Card className="pb-5">
            <Navigation.Title>
              Supply Menu
            </Navigation.Title>
            <Navigation.Section name="My Supply">
              <Navigation.Link to="/supply/requisition-form">
                Requisition Form
              </Navigation.Link>
              <Navigation.Link to="/supply/cart">
                Shopping Cart
              </Navigation.Link>
              <Navigation.Link to="/supply/order-history">
                Order History
              </Navigation.Link>
            </Navigation.Section>
            <Navigation.Section name="Manage Supply">
              <Navigation.Link to="/supply/fulfillment">
                Fulfillment
              </Navigation.Link>
              <Navigation.Link to="/supply/reconciliation">
                Reconciliation
              </Navigation.Link>
              <Navigation.Link to="/supply/requisition-history">
                Requisition History
              </Navigation.Link>
              <Navigation.Link to="/supply/item-history">
                Item History
              </Navigation.Link>
            </Navigation.Section>
          </Card>
          {location.pathname === "/supply/requisition-form" && categories.length !== 0 && (
              // Big boy
              <div className={`${styles.marginTop20}`} style={{ marginBottom: '100px', minHeight: '0px', minWidth: '0px'}}>
                  <Navigation.Title>
                    Categories
                  </Navigation.Title>
                  <CategoryCard categories={categories}/>
              </div>
          )}
        </Navigation>
      </AppLayout>
  );
}


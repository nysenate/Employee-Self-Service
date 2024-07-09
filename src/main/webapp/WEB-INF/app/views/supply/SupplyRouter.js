import React from "react";
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

export default function SupplyRouter() {
  return (
      <ThemeContext.Provider value={themes.supply}>
        <Routes>
          <Route path="" element={<SupplyLayout/>}>
            <Route path="requisition-form" element={<RequisitionFormIndex/>}/>
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

function SupplyLayout() {
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
          {location.pathname === "/supply/requisition-form" && (
              <div style={{ padding: '20px 0px 100px 0px' }}>
                <Card className="pb-5">
                  <Navigation.Title>
                    Categories
                  </Navigation.Title>
                  <CategoryCard/>
                </Card>
              </div>
          )}
        </Navigation>
      </AppLayout>
  );
}

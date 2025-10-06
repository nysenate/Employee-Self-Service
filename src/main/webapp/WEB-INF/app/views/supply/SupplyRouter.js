import React from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { ThemeContext, themes } from "app/ThemeContext";
import ShopIndex from "app/views/supply/store/shop/ShopIndex";
import CartIndex from "app/views/supply/store/cart/CartIndex";
import OrderHistoryIndex from "app/views/supply/orders/order-list/OrderHistoryIndex";
import OrderDetail from "app/views/supply/orders/order-detail/OrderDetail";
import FulfillmentIndex from "app/views/supply/fulfillment/FulfillmentIndex";
import RequisitionHistoryIndex from "app/views/supply/requisition-history/RequisitionHistoryIndex";
import AppLayout from "app/components/AppLayout";
import Navigation from "app/components/Navigation";
import Card from "app/components/Card";
import { SupplyContextProvider } from "app/views/supply/store/useSupplyContext";
import ReconciliationIndex from "app/views/supply/reconciliation/ReconciliationIndex";
import ItemSummary from "app/views/supply/item-history/ItemSummary";
import AssertPermission from "app/components/AssertPermission";
import NotFound from "app/views/NotFound";

export default function SupplyRouter() {
  return (
    <ThemeContext.Provider value={themes.supply}>
      <SupplyContextProvider>
        <Routes>
          <Route path="" element={<SupplyLayout />}>
            <Route path="shop" element={<ShopIndex />} />
            <Route path="cart" element={<CartIndex />} />
            <Route path="orders" element={<OrderHistoryIndex />} />
            <Route path="orders/:orderId" element={<OrderDetail />} />
            <Route
              path="fulfillment"
              element={
                <AssertPermission permission="supply:ui:manage:fulfillment">
                  <FulfillmentIndex />
                </AssertPermission>
              }
            />
            <Route
              path="reconciliation"
              element={
                <AssertPermission permission="supply:ui:manage:reconciliation">
                  <ReconciliationIndex />
                </AssertPermission>
              }
            />
            <Route
              path="requisition-history"
              element={
                <AssertPermission permission="supply:ui:manage:requisition-history">
                  <RequisitionHistoryIndex />
                </AssertPermission>
              }
            />
            <Route
              path="item-history"
              element={
                <AssertPermission permission="supply:ui:manage:item-history">
                  <ItemSummary />
                </AssertPermission>
              }
            />
            <Route path="" element={<Navigate to="shop" replace />} />
            <Route path="*" element={<NotFound />} />
          </Route>
        </Routes>
      </SupplyContextProvider>
    </ThemeContext.Provider>
  );
}

function SupplyLayout() {
  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>Supply Menu</Navigation.Title>
        <Navigation.Section name="My Supply">
          <Navigation.Link to="/supply/shop">Requisition Form</Navigation.Link>
          <Navigation.Link to="/supply/cart">Shopping Cart</Navigation.Link>
          <Navigation.Link to="/supply/orders">Order History</Navigation.Link>
        </Navigation.Section>
        <Navigation.Section
          name="Manage Supply"
          permission="supply:ui:nav:manage"
        >
          <Navigation.Link
            to="/supply/fulfillment"
            permission="supply:ui:manage:fulfillment"
          >
            Fulfillment
          </Navigation.Link>
          <Navigation.Link
            to="/supply/reconciliation"
            permission="supply:ui:manage:reconciliation"
          >
            Reconciliation
          </Navigation.Link>
          <Navigation.Link
            to="/supply/requisition-history"
            permission="supply:ui:manage:requisition-history"
          >
            Requisition History
          </Navigation.Link>
          <Navigation.Link
            to="/supply/item-history"
            permission="supply:ui:manage:item-history"
          >
            Item History
          </Navigation.Link>
        </Navigation.Section>
      </Navigation>
      <div id="categories-portal" className="py-5"></div>
    </AppLayout>
  );
}

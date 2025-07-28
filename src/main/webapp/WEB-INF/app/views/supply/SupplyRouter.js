import React from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { ThemeContext, themes } from "app/contexts/ThemeContext";
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

export default function SupplyRouter() {
  return (
    <ThemeContext.Provider value={themes.supply}>
      <SupplyContextProvider>
        <Routes>
          <Route path="" element={<SupplyLayout />}>
            <Route path="shop" element={<ShopIndex />} />
            <Route path="cart" element={<CartIndex />} />
            <Route path="orders/:orderId" element={<OrderDetail />} />
            <Route path="orders" element={<OrderHistoryIndex />} />
            <Route path="fulfillment" element={<FulfillmentIndex />} />
            <Route path="reconciliation" element={<ReconciliationIndex />} />
            <Route
              path="requisition-history"
              element={<RequisitionHistoryIndex />}
            />
            <Route path="item-history" element={<ItemSummary />} />
            <Route path="" element={<Navigate to="shop" replace />} />
            <Route path="*" element={<div>404</div>} />
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
      </Navigation>
    </AppLayout>
  );
}

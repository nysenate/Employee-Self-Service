import React from "react"
import { Navigate, Route, Routes } from "react-router-dom";
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import RequisitionFormIndex from "app/views/supply/requisition/RequisitionFormIndex";
import OrderHistoryIndex from "app/views/supply/orderhistory/OrderHistoryIndex";
import OrderDetail from "app/views/supply/orderhistory/OrderDetail";
import ShoppingCartIndex from "app/views/supply/cart/ShoppingCartIndex";
import AppLayout from "app/components/AppLayout";
import Navigation from "app/components/Navigation";


export default function SupplyRouter() {
  return (
    <ThemeContext.Provider value={themes.supply}>
      <Routes>
        <Route path="" element={<SupplyLayout/>}>
          <Route path="shopping/order" element={<RequisitionFormIndex/>}/>
          <Route path="shopping/cart" element={<ShoppingCartIndex/>}/>
          <Route path="order-history/order/:orderId" element={<OrderDetail/>}/>
          <Route path="order-history" element={<OrderHistoryIndex/>}/>
          <Route path="" element={<Navigate to="shopping/order" replace/>}/>
          <Route path="*" element={<div>404</div>}/>
        </Route>
      </Routes>
    </ThemeContext.Provider>
  )
}

function SupplyLayout() {
  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>
          Supply Menu
        </Navigation.Title>
        <Navigation.Section name="My Supply">
          <Navigation.Link to="/supply/shopping/order">
            Requisition Form
          </Navigation.Link>
          <Navigation.Link to="/supply/shopping/cart">
            Shopping Cart
          </Navigation.Link>
          <Navigation.Link to="/supply/order-history">
            Order History
          </Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  )
}

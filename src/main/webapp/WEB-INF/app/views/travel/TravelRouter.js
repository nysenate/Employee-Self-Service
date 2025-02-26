import React from "react";
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";
import { Route, Routes } from "react-router-dom";
import TravelTitle from "app/views/travel/statistics/TravelTitle";

export default function TravelRouter() {
  return (
    <ThemeContext.Provider value={themes.travel}>
      <Routes>
        <Route path="" element={<TravelLayout />}>
          <Route path="/stats" element={<TravelTitle />} />
        </Route>
      </Routes>
    </ThemeContext.Provider>
  );
}

function TravelLayout() {
  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>Travel Menu</Navigation.Title>
        <Navigation.Section name="My Travel">
          <Navigation.Link to="/travel/stats">Statistics</Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  );
}

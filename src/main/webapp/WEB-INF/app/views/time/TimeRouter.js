import React from "react";
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";
import { Route, Routes } from "react-router-dom";

export default function TimeRouter() {
  return (
    <ThemeContext.Provider value={themes.time}>
      <Routes>
        <Route path="" element={<TimeLayout />}>
          <Route path="record/entry" element={<div>Time Record Entry</div>} />
        </Route>
      </Routes>
    </ThemeContext.Provider>
  );
}

function TimeLayout() {
  return (
    <AppLayout>
      <Navigation>
        <Navigation.Title>My Info Menu</Navigation.Title>
        <Navigation.Section name="My Attendance">
          <Navigation.Link to="/time/record/entry">
            Attendance Record Entry
          </Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  );
}

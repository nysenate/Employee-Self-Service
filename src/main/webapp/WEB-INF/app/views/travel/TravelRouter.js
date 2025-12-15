import React from "react";
import { ThemeContext, themes } from "app/ThemeContext";
import Navigation from "app/components/Navigation";
import AppLayout from "app/components/AppLayout";
import Travel from "app/views/travel/Travel";
import { Navigate, Route, Routes } from "react-router-dom";
import NotFound from "app/views/NotFound";
import ApplicationHistory from "app/views/travel/application/history";
import SubmitApplication from "app/views/travel/application/submit";
import Drafts from "app/views/travel/application/drafts";

export default function TravelRouter() {
  return (
    <ThemeContext.Provider value={themes.travel}>
      <Routes>
        <Route path="" element={<TravelLayout />}>
          <Route path="/applications/new" element={<SubmitApplication />} />
          <Route path="/applications/drafts" element={<Drafts />} />
          <Route path="/applications" element={<ApplicationHistory />} />

          <Route path="/stats" element={<Travel />} />

          <Route path="" element={<Navigate to="applications" replace />} />
          <Route path="*" element={<NotFound />} />
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
          <Navigation.Link to="/travel/applications/new">
            Submit Travel Application
          </Navigation.Link>
          <Navigation.Link to="/travel/applications" end>
            Travel History
          </Navigation.Link>
          <Navigation.Link to="/travel/applications/drafts">
            Drafts
          </Navigation.Link>
        </Navigation.Section>
        <Navigation.Section name="Manage Travel">
          <Navigation.Link to="/travel/manage/review">
            Review Travel
          </Navigation.Link>
          <Navigation.Link to="/travel/manage/history">
            Review History
          </Navigation.Link>
          <Navigation.Link to="/travel/manage/stats">
            Statistics
          </Navigation.Link>
        </Navigation.Section>
      </Navigation>
    </AppLayout>
  );
}

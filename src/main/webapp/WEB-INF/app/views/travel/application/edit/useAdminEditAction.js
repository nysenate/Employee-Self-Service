import { useLocation, useNavigate } from "react-router-dom";
import { useUserTravelRoles } from "app/views/travel/shared/hooks/useUserTravelRoles";
import { canAdminEditTravel } from "app/views/travel/shared/travelRoles";
import { editApplicationUrl } from "./editRoutes";

export function useAdminEditAction(reviewSummary, review) {
  const { data: roles } = useUserTravelRoles();
  const location = useLocation();
  const navigate = useNavigate();

  return {
    canEdit: canAdminEditTravel(roles),
    onEdit: () =>
      navigate(editApplicationUrl(review.travelApplication.id), {
        state: {
          returnTo: location.pathname + location.search,
          reviewContext: {
            reviewSummary,
            reviewRole: review.pendingReviewerRole,
          },
        },
      }),
  };
}

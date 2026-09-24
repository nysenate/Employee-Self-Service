import useCheckPermission from "app/hooks/useCheckPermission";

/** Mount optional controls only after permission is granted. No fallback UI. */
export default function PermissionGate({ permission, children }) {
  const { isAllowed } = useCheckPermission(permission);
  return isAllowed ? children : null;
}

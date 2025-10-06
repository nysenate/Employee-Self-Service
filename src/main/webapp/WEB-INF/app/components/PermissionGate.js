import useCheckPermission from "app/hooks/useCheckPermission";

/**
 * Only shows children components if the user has the given permission.
 *
 * @param permission
 * @param children
 * @returns {*|null}
 * @constructor
 */
export default function PermissionGate({ permission, children }) {
  const { data, isPending } = useCheckPermission(permission);

  if (!isPending && data.isPermitted) {
    return children;
  }

  return null;
}

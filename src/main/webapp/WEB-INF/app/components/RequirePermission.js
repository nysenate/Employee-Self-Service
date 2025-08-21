import useCheckPermission from "app/core/useCheckPermission";

/**
 * Use to wrap a page which requires permissions to view.
 * This prevents unauthorized users from seeing the page even if they know the url.
 * @param permission - The permission string required to view the page.
 * @param children - The page to render if the user has permission.
 * @returns {*|null}
 * @constructor
 */
export default function RequirePermission({ permission, children }) {
  const { data, isPending } = useCheckPermission(permission);

  if (!isPending && !data.isPermitted) {
    throw new Error(
      "UNAUTHORIZED - You do not have the required permissions to view this page.",
    );
  }

  if (isPending) {
    return null;
  }

  return children;
}

import React, { useContext } from "react";
import { ThemeContext, themes } from "app/ThemeContext";
import { NavLink } from "react-router-dom";
import Card from "app/components/Card";
import useCheckPermission from "app/hooks/useCheckPermission";

const Navigation = ({ children }) => {
  return (
    <nav className="w-[250px] flex-none print:hidden" aria-label="Secondary">
      <Card className="pb-5">{children}</Card>
    </nav>
  );
};

const Title = ({ children }) => {
  const theme = useContext(ThemeContext);
  let bgColor;
  switch (theme) {
    case themes.myinfo:
      bgColor = "bg-green-800";
      break;
    case themes.time:
      bgColor = "bg-teal-800";
      break;
    case themes.supply:
      bgColor = "bg-purple-800";
      break;
    case themes.travel:
      bgColor = "bg-orange-700";
      break;
    default:
      console.error(
        `The theme "${theme}" is unknown to the Navigation.Title component.`,
      );
      bgColor = "bg-gray-600";
  }
  return (
    <div className={`px-3 py-2 text-lg font-semibold text-white ${bgColor}`}>
      {children}
    </div>
  );
};

/**
 * @param name The name of the section
 * @param permission If given, this Section will only be rendered if the user has this permission.
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
const Section = ({ name, permission, children }) => {
  const { data, isLoading } = useCheckPermission(permission);

  if (isLoading || data?.isPermitted === false) {
    return null;
  }

  return (
    <>
      <h2 className="mx-5 my-2 border-b-1 border-gray-300 py-1 text-lg font-semibold">
        {name}
      </h2>
      <ul>{children}</ul>
    </>
  );
};

/**
 * @param to The url to link to.
 * @param permission If given, this Link will only be rendered if the user has this permission.
 * @param children
 * @returns {JSX.Element|null}
 * @constructor
 */
const Link = ({ to, permission, children, ...rest }) => {
  const theme = useContext(ThemeContext);
  const { data, isLoading } = useCheckPermission(permission);

  let borderColor;
  switch (theme) {
    case themes.myinfo:
      borderColor = "border-green-600";
      break;
    case themes.time:
      borderColor = "border-teal-600";
      break;
    case themes.supply:
      borderColor = "border-purple-600";
      break;
    case themes.travel:
      borderColor = "border-orange-600";
      break;
    default:
      console.error(
        `The theme "${theme}" is unknown to the Navigation.Link component.`,
      );
      borderColor = "border-gray-700";
  }
  const activeClasses = `flex items-center gap-2 py-1 pr-5 pl-4 font-semibold border-l-4 ${borderColor} bg-gray-50`;
  const inactiveClasses = `flex items-center gap-2 px-5 py-1`;

  if (isLoading || data?.isPermitted === false) {
    return null;
  }

  return (
    <li>
      <NavLink
        to={to}
        {...rest}
        className={({ isActive }) =>
          isActive ? activeClasses : inactiveClasses
        }
      >
        {children}
      </NavLink>
    </li>
  );
};

/**
 * A small count pill sitting beside a link, for things needing the user's attention.
 * Ported from the legacy "badge" directive (assets/js/src/common/badge-directive.js), which
 * hid itself when the count was zero.
 *
 * @param count The number to show. Nothing is rendered when it is zero or missing.
 * @param color One of "teal", "green" or "orange".
 * @param title Tooltip describing what is being counted.
 */
const Badge = ({ count, color = "teal", title }) => {
  if (!count) {
    return null;
  }

  return (
    <span
      title={title}
      className={`ml-1 inline-block min-w-5 rounded-full px-1.5 text-center text-xs leading-5 font-semibold text-white ${badgeStyles[color] || badgeStyles.teal}`}
    >
      {count}
    </span>
  );
};

const badgeStyles = {
  teal: "bg-teal-700",
  green: "bg-green-700",
  orange: "bg-orange-600",
};

Navigation.Title = Title;
Navigation.Section = Section;
Navigation.Link = Link;
Navigation.Badge = Badge;

export default Navigation;

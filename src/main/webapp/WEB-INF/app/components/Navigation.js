import React, { useContext } from "react";
import { ThemeContext, themes } from "app/contexts/ThemeContext";
import { NavLink } from "react-router-dom";
import Card from "app/components/Card";

const Navigation = ({ children }) => {
  return (
    <nav className="w-[250px] flex-none" aria-label="Secondary">
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

const Section = ({ name, children }) => {
  return (
    <>
      <h2 className="mx-5 my-2 border-b-1 border-gray-300 py-1 text-lg font-semibold">
        {name}
      </h2>
      <ul>{children}</ul>
    </>
  );
};

const Link = ({ to, children }) => {
  const theme = useContext(ThemeContext);
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
  const activeClasses = `block px-4 py-1 font-semibold border-l-4 ${borderColor} bg-gray-50`;
  const inactiveClasses = `block px-5 py-1`;
  return (
    <li>
      <NavLink
        to={to}
        className={({ isActive }) =>
          isActive ? activeClasses : inactiveClasses
        }
      >
        {children}
      </NavLink>
    </li>
  );
};

Navigation.Title = Title;
Navigation.Section = Section;
Navigation.Link = Link;

export default Navigation;

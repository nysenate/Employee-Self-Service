import { useLocation, useNavigate } from "react-router-dom";
import React from "react";
import "./essNavBar.css";
import { themes } from "app/contexts/ThemeContext";

export default function EssNavBar() {
  const navigate = useNavigate();

  return (
    <nav
      className="fixed h-[45px] w-screen bg-gray-50 shadow"
      aria-label="Main"
    >
      <div className="mx-auto h-full w-[1150px]">
        <div className="flex h-full items-center justify-between">
          <div className="ml-2 flex h-full items-center">
            <img
              src="/assets/img/nysslogo.png"
              alt="logo"
              className="h-[35px] w-[35px]"
            />
            <div className="ml-3 mr-6 inline-block">
              <span className="text-[20.8px] text-lg font-medium">NYS</span>
              &nbsp;
              <span className="text-[20.8px] text-lg font-light">ESS</span>
            </div>
            <ul className="h-full">
              <li className="inline leading-[40px]">
                <AppLink name="My Info" to="/myinfo" theme={themes.myinfo} />
              </li>
              <li className="inline leading-[40px]">
                <AppLink
                  name="Time & Attendance"
                  to="/time"
                  theme={themes.time}
                />
              </li>
              <li className="inline leading-[40px]">
                <AppLink name="Supply" to="/supply" theme={themes.supply} />
              </li>
              <li className="inline leading-[40px]">
                <AppLink name="Travel" to="/travel" theme={themes.travel} />
              </li>
            </ul>
          </div>
          <div>
            <span
              className="cursor-pointer text-[14.3px]"
              onClick={() => navigate("/logout")}
            >
              Sign Out
            </span>
          </div>
        </div>
      </div>
    </nav>
  );
}

function AppLink({ to, name, theme }) {
  const location = useLocation();
  const isActive = location.pathname.includes(theme);
  let themeBorder;
  let themeText;

  switch (theme) {
    case themes.myinfo:
      themeBorder = "border-green-600";
      themeText = "hover:text-green-600";
      break;
    case themes.time:
      themeBorder = "border-teal-600";
      themeText = "hover:text-teal-600";
      break;
    case themes.supply:
      themeBorder = "border-purple-600";
      themeText = "hover:text-purple-600";
      break;
    case themes.travel:
      themeBorder = "border-orange-600";
      themeText = "hover:text-orange-600";
      break;
    default:
      themeBorder = "border-gray-700";
      themeText = "hover:text-gray-700";
  }

  const baseClasses = `text-[14.3px] inline-block h-full px-5 mx-0.5 border-0 hover:border-b-[3px] ${themeBorder} ${themeText}`;
  const activeClasses = `font-semibold border-b-[3px]`;
  const classes = baseClasses + " " + (isActive ? activeClasses : "");
  return (
    <a href={to} className={classes}>
      {name}
    </a>
  );
  // TODO Can't use a NavLink until all ESS sub apps are implemented in React.
  // return (
  //   <NavLink
  //     to={to}
  //     className={({ isActive }) =>
  //       isActive ? `${classes} ${activeClasses}` : `${classes}`
  //     }
  //   >
  //     <span className="app-link inline-block" title={name}>
  //     {name}
  //     </span>
  //   </NavLink>
  // )
}

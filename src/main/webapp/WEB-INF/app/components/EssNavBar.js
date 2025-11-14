import { Link, useLocation } from "react-router-dom";
import React from "react";
import "app/components/essNavBar.css";
import { themes } from "app/ThemeContext";
import { QuestionMarkCircleIcon } from "@heroicons/react/24/outline";
import useAuthedUser from "app/hooks/useAuthedUser";
import { useConfig } from "app/hooks/useConfig";
import clsx from "clsx";
import { twMerge } from "tailwind-merge";

export default function EssNavBar() {
  const { data: config } = useConfig();
  const { data: user } = useAuthedUser();

  return (
    <nav
      className="fixed z-20 h-[45px] w-screen bg-gray-50 shadow print:hidden print:h-0"
      aria-label="Main"
    >
      <div className="mx-auto h-full w-[1150px]">
        <div className="flex h-full items-stretch justify-between">
          <div className="ml-2 flex h-full items-center">
            <img
              src="/assets/img/nysslogo.png"
              alt="logo"
              className="h-[35px] w-[35px]"
            />
            <div className="mr-6 ml-3 inline-block">
              <span className="text-[20.8px] font-medium">NYS</span>
              &nbsp;
              <span className="text-[20.8px] font-light">ESS</span>
            </div>
            <div className="flex h-full flex-row items-stretch">
              <AppLink name="My Info" to="/myinfo" theme={themes.myinfo} />
              <AppLink
                name="Time & Attendance"
                to="/time"
                theme={themes.time}
              />
              <AppLink name="Supply" to="/supply" theme={themes.supply} />
              <AppLink name="Travel" to="/travel" theme={themes.travel} />
            </div>
          </div>
          <div className="flex h-full items-center p-0.5">
            {config?.runtimeLevel === "dev" && (
              <div className="mx-3 text-[14.3px] font-semibold text-red-700">
                <span className="mx-3">dev</span>
                <span>emp #{user.employeeId}</span>
              </div>
            )}
            <div className="mx-3 text-[14.3px] font-semibold">
              Hi, {user?.firstName} {user?.lastName}
            </div>
            <Link
              target="_blank"
              to="/assets/help/html/index.htm"
              className="flex h-full items-center px-2 text-[14.3px] text-gray-800 hover:bg-gray-200 hover:text-gray-800"
            >
              <QuestionMarkCircleIcon className="mr-1 size-5" /> Help
            </Link>
            <Link
              to="/logout"
              className="flex h-full items-center px-2 text-[14.3px] text-gray-800 hover:bg-gray-200 hover:text-gray-800"
            >
              Sign Out
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
}

function AppLink({ to, name, theme }) {
  const location = useLocation();
  const isActive = location.pathname.includes(theme);

  // Define potential classes statically so Tailwind can detect them:
  const possibleBorderHovClasses = {
    [themes.myinfo]: "hover:border-green-600",
    [themes.time]: "hover:border-teal-600",
    [themes.supply]: "hover:border-purple-600",
    [themes.travel]: "hover:border-orange-600",
  };

  const possibleBorderActiveClasses = {
    [themes.myinfo]: "border-green-600",
    [themes.time]: "border-teal-600",
    [themes.supply]: "border-purple-600",
    [themes.travel]: "border-orange-600",
  };

  const possibleTextHovClasses = {
    [themes.myinfo]: "hover:text-green-800",
    [themes.time]: "hover:text-teal-800",
    [themes.supply]: "hover:text-purple-800",
    [themes.travel]: "hover:text-orange-800",
  };

  const containerClasses = twMerge(
    "px-5 mx-0.5 border-b-3 border-transparent",
    possibleBorderHovClasses[theme],
    isActive && "border-b-3",
    isActive && possibleBorderActiveClasses[theme],
  );

  const textClasses = clsx(
    "text-[14.3px] text-gray-800",
    isActive && "font-semibold",
    possibleTextHovClasses[theme],
  );

  return (
    <div className={`flex items-center ${containerClasses}`}>
      <a href={to} className={textClasses}>
        {name}
      </a>
    </div>
  );
}

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

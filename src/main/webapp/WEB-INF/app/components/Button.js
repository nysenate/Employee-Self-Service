import React, { forwardRef, useContext } from "react";
import { ThemeContext } from "app/ThemeContext";
import { twMerge } from "tailwind-merge";
import { Button as AriaButton } from "react-aria-components";

/**
 * A common button component for ESS.
 * @param variant The style of button. Options are "contained", "outlined", "text".
 * @param color The color of the button. Options are "success", "secondary", "error", "theme", "link".
 *              The "theme" color will use an appropriate color for the current theme.
 *              The "link" color only works on the "text" variant, it looks similar to a link.
 * @param children The content to display in the button.
 * @param passThroughProps Any valid attributes for a button element, besides those in controlledProps, will
 *                         be passed onto the button element. For example: "onClick", "disabled", etc.
 */
const Button = forwardRef(function (
  {
    variant = "contained",
    color: colorProp,
    children,
    className = "",
    disabled,
    isDisabled,
    type,
    ...passThroughProps
  },
  ref,
) {
  const theme = useContext(ThemeContext);
  const resolvedDisabled = isDisabled ?? disabled ?? false;
  let color = colorProp;

  if (!color) {
    color = variantDefaultColors[variant];
  } else if (color === "theme") {
    color = theme;
  }

  const classes = twMerge(
    "transition outline-none",
    "data-[disabled]:cursor-not-allowed data-[disabled]:opacity-50",
    "data-[focus-visible]:ring-2 data-[focus-visible]:ring-teal-600 data-[focus-visible]:ring-offset-1",
    `${variantStyles[variant].core} ${variantStyles[variant].color[color]}`,
    className,
  );

  return (
    <AriaButton
      {...passThroughProps}
      ref={ref}
      type={type || "button"}
      isDisabled={resolvedDisabled}
      className={classes}
    >
      {children}
    </AriaButton>
  );
});

export default Button;

// All styles have to be hard coded, they cannot be dynamic due to tailwind's JIT compiler.
const variantStyles = {
  contained: {
    core: "border-b-2 px-2.5 py-1 font-semibold text-white",
    color: {
      success:
        "border-green-800 bg-green-600 data-[hovered]:bg-green-500 data-[pressed]:bg-green-700",
      secondary:
        "border-gray-600 bg-gray-500 data-[hovered]:bg-gray-450 data-[pressed]:bg-gray-600",
      error:
        "border-red-700 bg-red-600 data-[hovered]:bg-red-500 data-[pressed]:bg-red-700",
      myinfo:
        "border-green-800 bg-green-600 data-[hovered]:bg-green-500 data-[pressed]:bg-green-700",
      time: "border-teal-800 bg-teal-600 data-[hovered]:bg-teal-500 data-[pressed]:bg-teal-700",
      supply:
        "border-purple-800 bg-purple-600 data-[hovered]:bg-purple-500 data-[pressed]:bg-purple-700",
      travel:
        "border-orange-800 bg-orange-600 data-[hovered]:bg-orange-500 data-[pressed]:bg-orange-700",
    },
  },
  text: {
    core: "",
    color: {
      success:
        "px-2.5 py-1 font-semibold text-green-600 data-[hovered]:bg-green-100 data-[hovered]:text-green-800 data-[pressed]:bg-green-200",
      secondary:
        "px-2.5 py-1 font-semibold text-gray-600 data-[hovered]:bg-gray-100 data-[hovered]:text-gray-700 data-[pressed]:bg-gray-200",
      error:
        "px-2.5 py-1 font-semibold text-red-600 data-[hovered]:bg-red-100 data-[hovered]:text-red-700 data-[pressed]:bg-red-200",
      link: "font-base leading-none text-teal-600 data-[hovered]:text-teal-800",
      myinfo:
        "px-2.5 py-1 font-semibold text-green-600 data-[hovered]:bg-green-100 data-[hovered]:text-green-800 data-[pressed]:bg-green-200",
      time: "px-2.5 py-1 font-semibold text-teal-600 data-[hovered]:bg-teal-100 data-[hovered]:text-teal-700 data-[pressed]:bg-teal-200",
      supply:
        "px-2.5 py-1 font-semibold text-purple-700 data-[hovered]:bg-purple-100 data-[hovered]:text-purple-800 data-[pressed]:bg-purple-200",
      travel:
        "px-2.5 py-1 font-semibold text-orange-700 data-[hovered]:bg-orange-100 data-[hovered]:text-orange-800 data-[pressed]:bg-orange-200",
    },
  },
  outlined: {
    core: "border px-2.5 py-1 font-semibold",
    color: {
      success:
        "border-green-700 text-green-700 data-[hovered]:bg-green-100 data-[hovered]:text-green-800 data-[pressed]:bg-green-200",
      secondary:
        "border-gray-600 text-gray-600 data-[hovered]:bg-gray-100 data-[hovered]:text-gray-700 data-[pressed]:bg-gray-200",
      error:
        "border-red-600 text-red-600 data-[hovered]:bg-red-100 data-[hovered]:text-red-700 data-[pressed]:bg-red-200",
      myinfo:
        "border-green-700 text-green-700 data-[hovered]:bg-green-100 data-[hovered]:text-green-800 data-[pressed]:bg-green-200",
      time: "border-teal-600 text-teal-600 data-[hovered]:bg-teal-100 data-[hovered]:text-teal-700 data-[pressed]:bg-teal-200",
      supply:
        "border-purple-700 text-purple-700 data-[hovered]:bg-purple-100 data-[hovered]:text-purple-800 data-[pressed]:bg-purple-200",
      travel:
        "border-orange-700 text-orange-700 data-[hovered]:bg-orange-100 data-[hovered]:text-orange-800 data-[pressed]:bg-orange-200",
    },
  },
};

/**
 * Default colors for variants.
 * The `text` variant has a special default style that mimics the appearance of a link.
 */
const variantDefaultColors = {
  contained: "success",
  outlined: "secondary",
  text: "link",
};

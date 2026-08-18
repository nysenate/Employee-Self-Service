import React, { forwardRef, useContext } from "react";
import { ThemeContext } from "app/ThemeContext";
import { cn } from "app/utils/cn";
import {
  Button as AriaButton,
  composeRenderProps,
} from "react-aria-components";

/**
 * A common button component for ESS.
 * React Aria-first component.
 *
 * @param variant The button style. Supported options:
 *                "primary", "secondary", "destructive", "quiet", "theme", "link".
 *                Invalid values throw in development and fall back to "primary" in production.
 * @param onPress Preferred action handler (React Aria API).
 * @param isDisabled Disables interaction.
 * @param isPending Pending state. Pending buttons are rendered as non-interactive and show a spinner.
 * @param children The content to display in the button.
 * @param className Optional string class names to merge with button styles.
 * @param contentClassName Optional string class names for the button's content wrapper.
 * @param passThroughProps Any valid attributes for a button element, besides those in controlledProps, will
 *                         be passed onto the button element.
 */
const Button = forwardRef(function (
  {
    variant = "primary",
    onPress,
    isDisabled = false,
    isPending,
    type = "button",
    className = "",
    contentClassName = "",
    children,
    ...passThroughProps
  },
  ref,
) {
  const theme = useContext(ThemeContext);
  const resolvedVariant = resolveVariant(variant);
  const visualVariant = resolveVisualVariant(resolvedVariant, theme);
  const pending = Boolean(isPending);
  const blocksInteraction = Boolean(isDisabled) || pending;

  if (
    process.env.NODE_ENV !== "production" &&
    typeof className === "function"
  ) {
    throw new Error(
      '[Button] Function "className" is not supported. Pass a class string instead.',
    );
  }

  return (
    <AriaButton
      {...passThroughProps}
      ref={ref}
      type={type}
      isDisabled={blocksInteraction}
      isPending={pending}
      onPress={onPress}
      className={(renderProps) =>
        getButtonClassName({
          variant: visualVariant,
          renderProps,
          className,
        })
      }
    >
      {composeRenderProps(children, (inputChildren, renderProps) => (
        <>
          <span
            className={cn(
              "inline-flex items-center justify-center",
              contentClassName,
              renderProps.isPending && "opacity-0",
            )}
          >
            {inputChildren}
          </span>
          {renderProps.isPending && (
            <span
              aria-hidden="true"
              className="pointer-events-none absolute inset-0 flex items-center justify-center"
            >
              <span className="inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-r-transparent align-middle" />
            </span>
          )}
        </>
      ))}
    </AriaButton>
  );
});

export default Button;

const supportedVariants = Object.freeze([
  "primary",
  "secondary",
  "destructive",
  "quiet",
  "theme",
  "link",
]);

function resolveVariant(variant) {
  if (variant == null) {
    return "primary";
  }

  if (supportedVariants.includes(variant)) {
    return variant;
  }

  if (process.env.NODE_ENV !== "production") {
    throw new Error(
      `[Button] Unsupported variant "${String(variant)}". Supported variants: ${supportedVariants.join(", ")}.`,
    );
  }

  return "primary";
}

function resolveVisualVariant(variant, theme) {
  if (variant !== "theme") {
    return variant;
  }

  return themeVariantByContext[theme] ?? themeVariantByContext.myinfo;
}

function getButtonClassName({ variant, renderProps, className }) {
  const styles = variantStyles[variant];

  return cn(
    "relative inline-flex items-center justify-center transition outline-none",
    styles.base,
    renderProps.isHovered && styles.hover,
    renderProps.isPressed && styles.pressed,
    renderProps.isFocusVisible && styles.focus,
    renderProps.isDisabled && "cursor-not-allowed opacity-50",
    renderProps.isPending && "cursor-progress",
    className,
  );
}

const variantStyles = {
  primary: {
    base: "border-b-2 border-green-800 bg-green-600 px-2.5 py-1 font-semibold text-white",
    hover: "bg-green-700",
    pressed: "bg-green-800",
    focus: "ring-2 ring-green-600 ring-offset-1",
  },
  secondary: {
    base: "border border-gray-600 px-2.5 py-1 font-semibold text-gray-600",
    hover: "bg-gray-100 text-gray-700",
    pressed: "bg-gray-200 text-gray-700",
    focus: "ring-2 ring-gray-500 ring-offset-1",
  },
  destructive: {
    base: "border-b-2 border-red-800 bg-red-600 px-2.5 py-1 font-semibold text-white",
    hover: "bg-red-700",
    pressed: "bg-red-800",
    focus: "ring-2 ring-red-600 ring-offset-1",
  },
  quiet: {
    base: "px-2.5 py-1 font-semibold text-gray-600",
    hover: "bg-gray-100 text-gray-700",
    pressed: "bg-gray-200 text-gray-700",
    focus: "ring-2 ring-gray-500 ring-offset-1",
  },
  link: {
    base: "font-medium leading-none text-teal-600",
    hover: "text-teal-800",
    pressed: "text-teal-900",
    focus: "ring-2 ring-teal-600 ring-offset-1",
  },
  themeMyinfo: {
    base: "border-b-2 border-green-800 bg-green-600 px-2.5 py-1 font-semibold text-white",
    hover: "bg-green-700",
    pressed: "bg-green-800",
    focus: "ring-2 ring-green-600 ring-offset-1",
  },
  themeTime: {
    base: "border-b-2 border-teal-800 bg-teal-600 px-2.5 py-1 font-semibold text-white",
    hover: "bg-teal-700",
    pressed: "bg-teal-800",
    focus: "ring-2 ring-teal-600 ring-offset-1",
  },
  themeSupply: {
    base: "border-b-2 border-purple-800 bg-purple-600 px-2.5 py-1 font-semibold text-white",
    hover: "bg-purple-700",
    pressed: "bg-purple-800",
    focus: "ring-2 ring-purple-600 ring-offset-1",
  },
  themeTravel: {
    base: "border-b-2 border-orange-800 bg-orange-600 px-2.5 py-1 font-semibold text-white",
    hover: "bg-orange-700",
    pressed: "bg-orange-800",
    focus: "ring-2 ring-orange-600 ring-offset-1",
  },
};

const themeVariantByContext = {
  myinfo: "themeMyinfo",
  time: "themeTime",
  supply: "themeSupply",
  travel: "themeTravel",
};

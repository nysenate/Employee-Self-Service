import React, { useContext } from 'react'
import { ThemeContext } from "app/contexts/ThemeContext";


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
export function Button({ variant = "contained", color, children, ...passThroughProps }) {
  const theme = useContext(ThemeContext)
  if (!color) {
    color = variantDefaultColors[variant]
  } else if (color === "theme") {
    color = theme
  }

  const controlledProps = {
    "type": "button",
    "className": `transition disabled:pointer-events-none disabled:opacity-50
                  ${variantStyles[variant].core} ${variantStyles[variant].color[color]}`,
  }
  const props = { ...passThroughProps, ...controlledProps }

  return (
    <span className={props.disabled ? 'cursor-not-allowed' : ''}>
      <button {...props}>
        {children}
      </button>
    </span>
  )
}

// All styles have to be hard coded, they cannot be dynamic due to tailwind's JIT compiler.
const variantStyles = {
  contained: {
    core: "px-2.5 py-1 font-semibold text-white border-b-2",
    color: {
      success: "bg-green-600 border-green-800 hover:bg-green-500",
      secondary: "bg-gray-500 border-gray-600 hover:bg-gray-450",
      error: "bg-red-600 border-red-700 hover:bg-red-500",
      myinfo: "bg-green-600 border-green-800 hover:bg-green-500",
      time: "bg-teal-600 border-teal-800 hover:bg-teal-500",
      supply: "bg-purple-600 border-purple-800 hover:bg-purple-500",
      travel: "bg-orange-600 border-orange-800 hover:bg-orange-500",
    }
  },
  text: {
    core: "",
    color: {
      success: "px-2.5 py-1 font-semibold text-green-600 hover:text-green-800 hover:bg-green-100",
      secondary: "px-2.5 py-1 font-semibold text-gray-600 hover:text-gray-700 hover:bg-gray-100",
      error: "px-2.5 py-1 font-semibold text-red-600 hover:text-red-700 hover:bg-red-100",
      link: "mx-2.5 my-1 font-base leading-none text-teal-600 border-b-1 border-teal-500",
      myinfo: "px-2.5 py-1 font-semibold text-green-600 hover:text-green-800 hover:bg-green-100",
      time: "px-2.5 py-1 font-semibold text-teal-600 hover:text-teal-700 hover:bg-teal-100",
      supply: "px-2.5 py-1 font-semibold text-purple-700 hover:text-purple-800 hover:bg-purple-100",
      travel: "px-2.5 py-1 font-semibold text-orange-700 hover:text-orange-800 hover:bg-orange-100",
    }
  },
  outlined: {
    core: "px-2.5 py-1 font-semibold border-1",
    color: {
      success: "text-green-700 border-green-700 hover:text-green-800 hover:bg-green-100",
      secondary: "text-gray-600 border-gray-600 hover:text-gray-700 hover:bg-gray-100",
      error: "text-red-600 border-red-600 hover:text-red-700 hover:bg-red-100",
      myinfo: "text-green-700 border-green-700 hover:text-green-800 hover:bg-green-100",
      time: "text-teal-600 border-teal-600 hover:text-teal-700 hover:bg-teal-100",
      supply: "text-purple-700 border-purple-700 hover:text-purple-800 hover:bg-purple-100",
      travel: "text-orange-700 border-orange-700 hover:text-orange-800 hover:bg-orange-100",
    }
  }
}

/**
 * Default colors for variants.
 * The `text` variant has a special default style that mimics the appearance of a link.
 */
const variantDefaultColors = {
  contained: "success",
  outlined: "secondary",
  text: "link",
}

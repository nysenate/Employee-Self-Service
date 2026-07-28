import React from "react";
import { ChevronDown } from "lucide-react";
import { cn } from "app/utils/cn";
import {
  Button,
  Disclosure,
  DisclosureGroup,
  DisclosurePanel,
  Heading,
} from "react-aria-components";

/**
 * Common ESS accordion built on React Aria DisclosureGroup.
 *
 * Pass `allowsMultipleExpanded` to let more than one item stay open.
 * Use `defaultExpandedKeys` with matching `Accordion.Item id` values for
 * initially open items.
 */
export default function Accordion({ className, children, ...props }) {
  return (
    <DisclosureGroup
      {...props}
      className={(renderProps) =>
        cn(
          "divide-y divide-gray-200 border border-gray-300 bg-white",
          typeof className === "function" ? className(renderProps) : className,
        )
      }
    >
      {children}
    </DisclosureGroup>
  );
}

function Item({ title, children, className, titleClassName, ...props }) {
  return (
    <Disclosure
      {...props}
      className={(renderProps) =>
        cn(
          "group",
          renderProps.isFocusVisibleWithin && "ring-2 ring-teal-600 ring-inset",
          typeof className === "function" ? className(renderProps) : className,
        )
      }
    >
      <Heading className="m-0 block p-0 leading-none">
        <Button
          slot="trigger"
          className={({ isHovered, isPressed, isFocusVisible }) =>
            cn(
              "box-border flex w-full items-center justify-between gap-3 border-0 bg-transparent px-3 py-2 text-left leading-normal font-semibold text-gray-800 outline-none",
              isHovered && "bg-gray-50 text-gray-900",
              isPressed && "bg-gray-100",
              isFocusVisible && "ring-2 ring-teal-600 ring-inset",
              titleClassName,
            )
          }
        >
          <span>{title}</span>
          <ChevronDown
            aria-hidden="true"
            className="h-4 w-4 shrink-0 transition-transform group-data-[expanded]:rotate-180"
          />
        </Button>
      </Heading>
      {children}
    </Disclosure>
  );
}

function Panel({ className, children, ...props }) {
  return (
    <DisclosurePanel
      {...props}
      className={(renderProps) =>
        cn(
          "px-3 pt-2 pb-3 [&[hidden]]:p-0",
          renderProps.isFocusVisibleWithin && "ring-2 ring-teal-600 ring-inset",
          typeof className === "function" ? className(renderProps) : className,
        )
      }
    >
      {children}
    </DisclosurePanel>
  );
}

Accordion.Item = Item;
Accordion.Panel = Panel;

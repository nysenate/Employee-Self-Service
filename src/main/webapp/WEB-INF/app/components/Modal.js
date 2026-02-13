import React from "react";
import { cn } from "app/utils/cn";
import {
  Dialog as AriaDialog,
  Heading,
  Modal as AriaModal,
  ModalOverlay,
} from "react-aria-components";

/**
 * Displays a modal dialog.
 *
 * @param {boolean} isOpen - The modal is displayed when this is true. To close the modal, set to false.
 * @param {(isOpen: boolean) => void} [onOpenChange] - Preferred open-state callback. Called with false when user requests close.
 * @param {boolean} [isDismissable] - Whether outside click/tap and Escape can dismiss. Defaults to true when a close callback is provided.
 * @param {string} [ariaLabel] - Accessible dialog label fallback when `Modal.Title` is not rendered.
 * @param {string} [overlayClassName] - Extra classes for the overlay container.
 * @param {string} [className] - Extra classes for the modal panel.
 * @param {JSX.Element} children - The content of the modal.
 * @returns {JSX.Element}
 */
function Modal({
  isOpen,
  onOpenChange,
  isDismissable,
  ariaLabel,
  overlayClassName,
  className,
  children,
}) {
  const hasCloseHandler = typeof onOpenChange === "function";
  const canDismiss = isDismissable ?? hasCloseHandler;
  const hasTitle = hasModalTitle(children);

  if (process.env.NODE_ENV !== "production" && !ariaLabel && !hasTitle) {
    throw new Error(
      "[Modal] Accessible name required. Add <Modal.Title> or pass ariaLabel.",
    );
  }

  return (
    <ModalOverlay
      isOpen={isOpen}
      onOpenChange={onOpenChange}
      isDismissable={canDismiss}
      isKeyboardDismissDisabled={!canDismiss}
      className={({ isEntering, isExiting }) =>
        cn(
          "fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 transition-opacity duration-200 ease-out motion-reduce:transition-none",
          isEntering && "opacity-0",
          isExiting && "opacity-0",
          overlayClassName,
        )
      }
    >
      <AriaModal
        className={({ isEntering, isExiting }) =>
          cn(
            "w-auto max-w-screen-xl rounded-none bg-white shadow-xl outline-none transition duration-200 ease-out motion-reduce:transition-none",
            isEntering && "translate-y-2 scale-95 opacity-0",
            isExiting && "translate-y-2 scale-95 opacity-0",
            className,
          )
        }
      >
        <AriaDialog
          {...(ariaLabel ? { "aria-label": ariaLabel } : {})}
          className="outline-none focus:outline-none focus-visible:outline-none"
        >
          {children}
        </AriaDialog>
      </AriaModal>
    </ModalOverlay>
  );
}

function hasModalTitle(children) {
  return React.Children.toArray(children).some((child) => {
    if (!React.isValidElement(child)) {
      return false;
    }

    if (child.type === Title) {
      return true;
    }

    if (!child.props?.children) {
      return false;
    }

    return hasModalTitle(child.props.children);
  });
}

const Title = ({ className, children }) => (
  <Heading
    slot="title"
    className={cn(
      "border-b-1 border-teal-200 px-4 py-3 text-center font-semibold",
      className,
    )}
  >
    {children}
  </Heading>
);

const Body = ({ className, children }) => (
  <div className={cn("m-4", className)}>{children}</div>
);

const Controls = ({ className, children }) => (
  <div className={cn("border-t-1 border-teal-200 p-3", className)}>{children}</div>
);

/**
 * Similar to Controls, but adds additional styling to center buttons.
 * Use this for a simple button layout, use Controls for a more customizable layout.
 */
const Buttons = ({ className, children }) => (
  <Controls>
    <div className={cn("flex justify-center gap-3", className)}>{children}</div>
  </Controls>
);

Modal.Title = Title;
Modal.Body = Body;
Modal.Controls = Controls;
Modal.Buttons = Buttons;

export default Modal;

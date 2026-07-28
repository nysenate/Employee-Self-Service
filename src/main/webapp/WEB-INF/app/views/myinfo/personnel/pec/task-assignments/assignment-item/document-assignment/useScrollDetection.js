import { useEffect, useState } from "react";

/**
 * Returns true when scrolled to bottom of the page.
 * @returns {boolean}
 */
export default function useScrollDetection() {
  const [isScrolledToBottom, setIsScrolledToBottom] = useState(false);

  const onScrollHandler = (e) => {
    const windowHeight = window.innerHeight;
    const body = document.body;
    const html = document.documentElement;
    const docHeight = Math.max(
      body.scrollHeight,
      body.offsetHeight,
      html.clientHeight,
      html.scrollHeight,
      html.offsetHeight,
    );
    const scrollHeight = windowHeight + window.scrollY;
    const atBottom = docHeight - scrollHeight < 5;
    setIsScrolledToBottom(atBottom);
  };

  useEffect(() => {
    document.addEventListener("scroll", onScrollHandler);
    return () => {
      document.removeEventListener("scroll", onScrollHandler);
    };
  }, []);

  return isScrolledToBottom;
}

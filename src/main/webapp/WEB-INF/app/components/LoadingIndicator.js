import React from "react"
import styles from "./LoadingIndicator.module.css"

/**
 * Displays three animated dots to indicate loading.
 * @param {String} [variant] By default, a large loading indicator is used. Set the variant to 'sm' or 'small' to use
 *                           a smaller indicator.
 */
export default function LoadingIndicator({ variant }) {
  let size
  switch (variant) {
    case "sm":
    case "small":
      size = `${styles.small}`
      break
    default:
      size = `${styles.large}`

  }
  return (
    <div className={`${styles.loader} ${size}`}>
      <div className={`${styles.dot} ${styles.dot1}`}></div>
      <div className={`${styles.dot} ${styles.dot2}`}></div>
      <div className={`${styles.dot} ${styles.dot3}`}></div>
      <div className={`${styles.dot} ${styles.dot4}`}></div>
    </div>
  )
}

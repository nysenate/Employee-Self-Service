import React from 'react';
import styles from './EssNotification.module.css';

export default function EssNotification({ level, title, children }) {
  let levelClass = '';
  if (level === 'info') {
    levelClass = styles.info;
  } else if (level === 'warn') {
    levelClass = styles.warn;
  } else if (level === 'error') {
    levelClass = styles.error;
  }

  return (
    <div className={`${styles.essNotification} ${levelClass}`}>
      {title && ( <h2>{title}</h2> )}
      {children}
    </div>
  );
};

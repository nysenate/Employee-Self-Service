import React, { useState, useEffect } from 'react';
import styles from './TogglePanel.module.css';

const TogglePanel = ({
                       label,
                       open = false,
                       renderClosed = false,
                       extraClasses = '',
                       showTip = false,
                       callback = () => {},
                       children
                     }) => {
  const [isOpen, setIsOpen] = useState(open);

  useEffect(() => {
    callback(isOpen);
  }, [isOpen, callback]);

  const toggle = () => {
    setIsOpen(!isOpen);
  };

  return (
    <div className={`${styles.contentContainer} ${styles.extraClasses} ${isOpen ? styles.open : ''}`}>
      <div className={styles.togglePanel} onClick={toggle}>
        <h1 className={styles.togglePanelLabel}>{label}</h1>
        <span className={styles.togglePanelStatus}>
          {!isOpen && showTip && <span className="togglePanelTip">(Click to expand section)</span>}
          <i className={`${isOpen ? styles.iconChevronUp : styles.iconChevronDown}`}></i>
        </span>
      </div>
      {(isOpen || renderClosed) && (
        <div className={`panel-content ${isOpen ? 'open' : ''}`}>
          {children}
        </div>
      )}
    </div>
  );
};

export default TogglePanel;

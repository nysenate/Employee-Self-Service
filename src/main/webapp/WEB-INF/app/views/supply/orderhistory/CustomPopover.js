import React from 'react';
import { Tooltip } from 'react-tippy';
import 'react-tippy/dist/tippy.css';
import styles from './OrderDetail.module.css';

const CustomPopover = ({ order }) => {
    const popoverContent = (
        <div className={styles.popoverContent} style={{alignContent: 'center'}}>
            <div className={styles.triangle}></div>
            <div className={styles.popoverTooltip}>
                <ul>
                    <li><b>Phone Number:</b> {order.customer.workPhone}</li>
                    <li><b>Email:</b> {order.customer.email}</li>
                </ul>
            </div>
        </div>
    );

    return (
        <Tooltip
            html={popoverContent}
            position="bottom"
            trigger="mouseenter"
            arrow={true}
            theme="light"
            duration={200}
        >
            <div className={`${styles.col412}`}>
                <b>Requested By:</b> {order.customer.fullName}
            </div>
        </Tooltip>
    );
};

export default CustomPopover;

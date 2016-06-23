package gov.nysenate.ess.core.service.notification;

import gov.nysenate.ess.core.service.notification.base.message.base.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for notification package
 * Created by Chenguang He on 6/15/2016.
 */
public class NotificationUtils {
    private NotificationUtils() {
    }

    /**
     * get a value from class path
     *
     * @param path class path
     * @return value in string
     */
    private static String getValue(String path) {
        String[] subs = path.split("\\.");
        return subs[subs.length - 1];
    }

    /**
     * get a name from class path
     * @param path class path
     * @return name in string
     */
    private static String getName(String path) {
        String[] subs = path.split("\\.");
        return subs[1];
    }

    /**
     *  get header names
     * @param castType cast type string
     * @param eventType event type string
     * @param notificationType notification type string
     * @return header name
     */
    public static String getHeaderName(StringBuilder castType, StringBuilder eventType, StringBuilder notificationType) {
        StringBuilder sb = new StringBuilder();
        sb.append(NotificationUtils.getName(castType.toString()));
        sb.append("#");
        sb.append(NotificationUtils.getName(eventType.toString()));
        sb.append("#");
        sb.append(NotificationUtils.getName(notificationType.toString()));
        return sb.toString();
    }

    /**
     *  get header value
     * @param castType cast type string
     * @param eventType event type string
     * @param notificationType notification type string
     * @return header value
     */
    public static String getHeaderValue(StringBuilder castType, StringBuilder eventType, StringBuilder notificationType) {
        StringBuilder sb = new StringBuilder();
        sb.append(NotificationUtils.getValue(castType.toString()));
        sb.append("#");
        sb.append(NotificationUtils.getValue(eventType.toString()));
        sb.append("#");
        sb.append(NotificationUtils.getValue(notificationType.toString()));
        return sb.toString();
    }

    /**
     *  get header maps
     * @param castType cast type string
     * @param eventType event type string
     * @param notificationType notification type string
     * @return header maps
     */
    public static Map<String, String> toMap(StringBuilder castType, StringBuilder eventType, StringBuilder notificationType) {
        Map<String, String> map = new HashMap<>();
        String name1 = getName(castType.toString());
        String value1 = getValue(castType.toString());
        map.put(name1, value1);
        String name2 = getName(eventType.toString());
        String value2 = getValue(eventType.toString());
        map.put(name2, value2);
        String name3 = getName(notificationType.toString());
        String value3 = getValue(notificationType.toString());
        map.put(name3, value3);
        return map;
    }

    /**
     *  cast component to its specific class
     * @param cla class
     * @param c component
     * @return the object of class
     * @throws ClassNotFoundException
     */
    public static Object deCompoent(Class<?> cla, Component c) throws ClassNotFoundException {
        return cla.cast(c);
    }


}

package gov.nysenate.ess.core.service.notification;

import gov.nysenate.ess.core.service.notification.base.message.base.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by senateuser on 6/15/2016.
 */
public class NotificationUtils {
    private NotificationUtils() {
    }

    private static String getValue(String path) {
        String[] subs = path.split("\\.");
        return subs[subs.length - 1];
    }

    private static String getName(String path) {
        String[] subs = path.split("\\.");
        return subs[1];
    }

    public static String getHeaderName(StringBuilder castType, StringBuilder eventType, StringBuilder notificationType) {
        StringBuilder sb = new StringBuilder();
        sb.append(NotificationUtils.getName(castType.toString()));
        sb.append("#");
        sb.append(NotificationUtils.getName(eventType.toString()));
        sb.append("#");
        sb.append(NotificationUtils.getName(notificationType.toString()));
        return sb.toString();
    }

    public static String getHeaderValue(StringBuilder castType, StringBuilder eventType, StringBuilder notificationType) {
        StringBuilder sb = new StringBuilder();
        sb.append(NotificationUtils.getValue(castType.toString()));
        sb.append("#");
        sb.append(NotificationUtils.getValue(eventType.toString()));
        sb.append("#");
        sb.append(NotificationUtils.getValue(notificationType.toString()));
        return sb.toString();
    }

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

    public static Object deCompoent(Class<?> cla, Component c) throws ClassNotFoundException {
        return cla.cast(c);
    }


}

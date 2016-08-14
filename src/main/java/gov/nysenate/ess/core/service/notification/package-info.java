/**
 * Created by Chenguang He on 6/23/2016.
 * notification package abstract the requirement of notification center.
 * It can be implemented in multiple ways of notification.
 * It use google guava's event bus to deliver message across the machine.
 * It can supply muti-thread in future but it is not thread-safety right now
 * // TODO: 6/23/2016
 */
package gov.nysenate.ess.core.service.notification;

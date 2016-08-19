package gov.nysenate.ess.time.service.attendance.validation;

import com.google.common.collect.*;
import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.time.model.attendance.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Checks time records to make sure that no time record contains partially entered miscellaneous values
 */

@Service
public class ChangeTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(ChangeTRV.class);
    @Autowired private EmpTransactionService empTransService;

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState,TimeRecordAction action) {
        // If the saved record contains entries in the employee scope
        return record.getScope() == TimeRecordScope.EMPLOYEE;
    }

    /**
     *  checkTimeRecord check hourly increments for all of the daily records
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException
     **/

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) throws TimeRecordErrorException {
        LocalDate entryDate;
        TimeRecord prevState = previousState.orElse(null);

        if (prevState!=null) {
            if (record.getTimeRecordId().compareTo(prevState.getTimeRecordId())!= 0 ) {
                throw new TimeRecordErrorException(TimeRecordErrorCode.TIME_REC_ID_CHANGED,
                        new InvalidParameterView("TimeRecID", "string",
                                " Time Record Id =  "+record.getTimeRecordId()+" -> "+prevState.getTimeRecordId(), record.getTimeRecordId().toString()));
            }
            if (record.getEmployeeId().intValue() != prevState.getEmployeeId().intValue()) {
                throw new TimeRecordErrorException(TimeRecordErrorCode.EMP_REC_ID_CHANGED,
                        new InvalidParameterView("EmployeeID", "string",
                                " Employee Id =  "+record.getEmployeeId().toString()+ " -> " + prevState.getEmployeeId().toString(), record.getEmployeeId().toString()));
            }

            if (record.getRecordStatus().equals("I'")) {
                throw new TimeRecordErrorException(TimeRecordErrorCode.INACTIVE_TIME_RECORD,
                        new InvalidParameterView("RecordStatus", "string",
                                " Record Status=  "+record.getRecordStatus().getCode(), record.getRecordStatus().getCode()));
            }

            checkEntries(record, prevState);
        }

    }

    private void checkEntries(TimeRecord record, TimeRecord prevState) {

        ImmutableList<TimeEntry> entries =  record.getTimeEntries();
        ImmutableList<TimeEntry> prevEntries =  prevState.getTimeEntries();
        TimeEntry prevEntryHold = null;
        boolean idExisted;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        checkForDuplicates(entries);

        checkForRemovedEntries(record, prevEntries, entries);

        // Generate a range set including all dates where the employee was not a temporary employee

        for (TimeEntry entry : entries) {

            /**
            *   Paytype changed might not really be an error but coded it as an error for now
            **/

            // Check for changed Paytype

            idExisted = false;
            int curRow = -1;

            for (TimeEntry prevEntry : prevEntries) {
                curRow++;
                if (    prevEntry != null &&
                        prevEntry.getEntryId() != null &&
                        entry != null &&
                        entry.getEntryId() != null) {
                    if (prevEntry.getEntryId().equals(entry.getEntryId())) {
                        prevEntryHold = prevEntry;
                        idExisted = true;
                        break;
                    }
                }


            }

            /**  Entry Added:  Date not found in Prev, Entry ID not found in prev
             *   Entry Removed: Entry ID found in Prev but not in current  (checked above in checkForRemovedEntries)
             *   Entry Changed: Entry ID found but not Date
             *                            OR
             *                  Date Found but not Entry ID
            **/


            if (idExisted) {
                // Check for changed Employee Xref
                if (entry.getEmpId() != prevEntryHold.getEmpId()) {
                    throw new TimeRecordErrorException(TimeRecordErrorCode.EMP_REC_ID_CHANGED,
                            new InvalidParameterView("EmployeeID", "string",
                                    " Employee Id =  " + entry.getEmpId() + " -> " + prevState.getTimeRecordId().toString(), record.getEmployeeId().toString()));
                }

                // Check for changed date
                if (entry.getDate().compareTo(prevEntryHold.getDate()) != 0) {
                    throw new TimeRecordErrorException(TimeRecordErrorCode.ENTRY_CHANGED,
                            new InvalidParameterView("EmployeeID", "string",
                                    " Employee Id =  " + entry.getEmpId() + " -> " + prevState.getTimeRecordId().toString(), record.getEmployeeId().toString()));
                }

                // Check for activation of an inactive record
                if (entry.isActive() && !prevEntryHold.isActive()) {
                    throw new TimeRecordErrorException(TimeRecordErrorCode.INACTIVE_TIME_RECORD,
                            new InvalidParameterView("RecordStatus", "string",
                                    " Record Status = I", "I"));
                }

                // Check for changed Original Date
                if (!entry.getOriginalDate().equals(prevEntryHold.getOriginalDate())) {
                    throw new TimeRecordErrorException(TimeRecordErrorCode.ORIGIN_DATE_CHANGED,
                            new InvalidParameterView("PayType", "string",
                                    " Date =  " + entry.getDate().format(dateTimeFormatter) + " Original Entry Date = " + entry.getOriginalDate().format(dateTimeFormatter) + " (Should be  "  + prevEntryHold.getOriginalDate().format(dateTimeFormatter)+")" ,  entry.getPayType().toString()));
                }

                // Check for changed Time Record ID
                if (entry.getTimeRecordId().longValue() != prevEntryHold.getTimeRecordId().longValue()) {
                    throw new TimeRecordErrorException(TimeRecordErrorCode.TIME_REC_ID_CHANGED,
                            new InvalidParameterView("TimeRecID", "string",
                                    " Time Record Id =  " + entry.getTimeRecordId() + " (Should be: "+prevEntryHold.getTimeRecordId()+")" , entry.getTimeRecordId().toString()));

                }
                // Check for changed Entry ID
                if (!entry.getEntryId().equals(prevEntryHold.getEntryId())) {
                    throw new TimeRecordErrorException(TimeRecordErrorCode.ENTRY_ID_CHANGED,
                            new InvalidParameterView("EntryID", "string",
                                    " Entry Id =  " + entry.getEntryId() +" (Should be: "+prevEntryHold.getEntryId()+")" , entry.getEntryId().toString()));

                }

                if (entry.isAccruing() != prevEntryHold.isAccruing()) {
                    throw new TimeRecordErrorException(TimeRecordErrorCode.ACCRUE_CHANGED,
                            new InvalidParameterView("PayType", "string",
                                    " Date =  " + entry.getDate().format(dateTimeFormatter) + " Accrue Flag =  " + String.valueOf(entry.isAccruing()) + " (Should be: " + String.valueOf(prevEntryHold.isAccruing()) + ")", entry.getPayType().toString()));
                }

                if (entry.getPayType().compareTo(prevEntryHold.getPayType()) != 0) {
                    throw new TimeRecordErrorException(TimeRecordErrorCode.PAYTYPE_CHANGED,
                            new InvalidParameterView("PayType", "string",
                                    " Date =  " + entry.getDate().format(dateTimeFormatter)+ " PayType =  " + entry.getPayType().toString() +" (Should be: " + prevEntryHold.getPayType().toString()+")" ,  entry.getPayType().toString()));
                }

            } else {
                // Check for a newly created ID. Newly created rows should not have a newly created ID at this point.
                if (entry.getEntryId() != null && entry.getEntryId().compareTo(BigInteger.ZERO) != 0) {
                    // Entry ID previously did not exist, we cannot create a new one without going through proper Entry ID Creation
                    throw new TimeRecordErrorException(TimeRecordErrorCode.ENTRY_ID_ADDED,
                            new InvalidParameterView("EntryId", "string",
                                    " Entry Id  = " + entry.getEntryId().toString(), entry.getEntryId().toString()));
                }
            }
        }
    }

    /**
     * checkForRemovedEntries checks to see if the user is trying to submit a timesheet with missing entries which were previously entered.
     * @param prevEntries
     * @param entries
     * @throws TimeRecordErrorException
     */

    public void checkForRemovedEntries (TimeRecord record, ImmutableList<TimeEntry> prevEntries,  ImmutableList<TimeEntry> entries) throws TimeRecordErrorException {
        boolean entryRemoved = true;
        // Used old fashioned Java to check for Entry Id in both Previous and Current Lists (as opposed to using Java 1/8 Streams)
        // Check for Entries with IDs that have been removed
        int prevRow = -1;
        int curRow = -1;
        for (TimeEntry prevEntry : prevEntries) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            entryRemoved = true;
            prevRow++;
            curRow = -1;
            if (prevEntry == null) {
                entryRemoved = false;
            }
            else if ( prevEntry.getEntryId() == null){
                entryRemoved = false;
            }
            else {
                for (TimeEntry entry : entries) {
                    curRow++;
                    if (prevEntry != null
                            && prevEntry.getEntryId() != null
                            && entry != null
                            && entry.getEntryId() != null
                            && prevEntry.getEntryId().compareTo(entry.getEntryId()) == 0) {
                        entryRemoved = false;

                        break;
                    }
                }
            }
             if (prevEntry != null && prevEntry.getEntryId() != null &&  entryRemoved) {
                 TimeEntry changedEntry = record.getEntry(prevEntry.getDate());
                 if (changedEntry != null && changedEntry.getEntryId() != null && changedEntry.getEntryId().compareTo(BigInteger.ZERO) != 0) {
                     throw new TimeRecordErrorException(TimeRecordErrorCode.ENTRY_ID_CHANGED,
                             new InvalidParameterView("EntryId", "string",
                                     " Entry Id  = " + prevEntry.getEntryId().toString()+ " illegally changed to = "+changedEntry.getEntryId().toString(), prevEntry.getEntryId().toString()));
                 }
                 else {
                     throw new TimeRecordErrorException(TimeRecordErrorCode.ENTRY_ID_REMOVED,
                             new InvalidParameterView("EntryId", "string",
                                     " Entry Id  = " + prevEntry.getEntryId().toString(), prevEntry.getEntryId().toString()));
                 }
            }
        }
    }

    public void checkForDuplicates (ImmutableList<TimeEntry> entries) throws TimeRecordErrorException {

        int cnt = 0;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        for (TimeEntry entry : entries) {
            cnt = 0;

            if (entry != null && entry.getDate() != null && entry.getEntryId() != null ) {
                for (TimeEntry entryCheck : entries) {
                    // Check for Duplicate Entry IDs with different dates
                    if (entryCheck != null && entryCheck.getDate() != null && entryCheck.getEntryId() != null) {
                        if (entryCheck.getDate().compareTo(entry.getDate()) != 0 && entryCheck.getEntryId().compareTo(entry.getEntryId()) == 0) {
                            throw new TimeRecordErrorException(TimeRecordErrorCode.DUPLICATE_ENTRY_ID,
                                    new InvalidParameterView("EntryId", "string",
                                            " Entry Id  = " + entry.getEntryId().toString() + " Entry Dates = " + entry.getDate().format(dateTimeFormatter) + ", " + entryCheck.getDate().format(dateTimeFormatter) + " = "  + entry.getDate().format(dateTimeFormatter), entry.getEntryId().toString()));
                        }

                        // Check for Different Entry IDs with the same date
                        if (entryCheck.getDate().compareTo(entry.getDate()) == 0 && entryCheck.getEntryId().compareTo(entry.getEntryId()) != 0) {
                            throw new TimeRecordErrorException(TimeRecordErrorCode.DUPLICATE_DATE,
                                    new InvalidParameterView("EntryId", "string",
                                            " Entry Date = " + entry.getDate().format(dateTimeFormatter), entry.getEntryId().toString()));
                        }

                        // Count the number of records with the same Entry ID and date
                        if (entryCheck.getDate().compareTo(entry.getDate()) == 0 && entryCheck.getEntryId().compareTo(entry.getEntryId()) == 0) {
                            cnt++;
                        }
                    }
                }
            }

            // Check for more than one record with the same Entry ID and date
            if (cnt>1) {
                throw new TimeRecordErrorException(TimeRecordErrorCode.DUPLICATE_ENTRY,
                        new InvalidParameterView("EntryId", "string",
                                " Entry Id  = " + entry.getEntryId().toString()+" Entry Date = "+entry.getDate().format(dateTimeFormatter)+" Record Count = "+cnt, entry.getEntryId().toString()));
            }

        }
    }

     /**
     * Constructs a Range from the given parameters.  Throws an exception if the parameter values are invalid
     * @param lower T
     * @param upper T
     * @param lowerName String
     * @param upperName String
     * @param lowerType BoundType
     * @param upperType BoundType
     * @param <T> T
     * @return Range<T>
     */
    protected <T extends Comparable> com.google.common.collect.Range<T> getRange(T lower, T upper, String lowerName, String upperName,
                                                                                 BoundType lowerType, BoundType upperType) {
        try {
            return com.google.common.collect.Range.range(lower, lowerType, upper, upperType);
        } catch (IllegalArgumentException ex) {
            String rangeString = (lowerType == BoundType.OPEN ? "(" : "[") + lower + " - " +
                    upper + (upperType == BoundType.OPEN ? ")" : "]");
            throw new InvalidRequestParamEx( rangeString, lowerName + ", " + upperName, "range",
                    "Range start must not exceed range end");
        }
    }

    protected <T extends Comparable> com.google.common.collect.Range<T> getOpenRange(T lower, T upper, String lowerName, String upperName) {
        return getRange(lower, upper, lowerName, upperName, BoundType.OPEN, BoundType.OPEN);
    }

    protected <T extends Comparable> com.google.common.collect.Range<T> getOpenClosedRange(T lower, T upper, String lowerName, String upperName) {
        return getRange(lower, upper, lowerName, upperName, BoundType.OPEN, BoundType.CLOSED);
    }

    protected <T extends Comparable> com.google.common.collect.Range<T> getClosedOpenRange(T lower, T upper, String lowerName, String upperName) {
        return getRange(lower, upper, lowerName, upperName, BoundType.CLOSED, BoundType.OPEN);
    }

    protected <T extends Comparable> com.google.common.collect.Range<T> getClosedRange(T lower, T upper, String lowerName, String upperName) {
        return getRange(lower, upper, lowerName, upperName, BoundType.CLOSED, BoundType.CLOSED);
    }

}

CREATE OR REPLACE PACKAGE SYNCHRONIZE_SUPPLY AS
  -- Constants
  SUPPLY_LOCATION_CODE VARCHAR2(6) := 'LC100S';
  SUPPLY_LOCATION_TYPE VARCHAR2(1) := 'P';

  /*
  Updates Fd12ExpIssue, Fd12ExpAudit, and Fm12Inventry tables for all item movements in the given requisition.
  requisition_xml_string is a single requisition serialized to xml.
  */
  FUNCTION synchronize_with_supply(requisition_xml_string VARCHAR2)
    RETURN NUMBER;

END SYNCHRONIZE_SUPPLY;



-------------------------------------------------------------------


CREATE OR REPLACE PACKAGE BODY SYNCHRONIZE_SUPPLY AS

  /*-------------------------
  ----- Private methods -----
  ---------------------------*/

  /* Get the responsibility head for a location. */
  FUNCTION get_responsibility_head(location_code VARCHAR2, location_type VARCHAR2)
    RETURN VARCHAR2 IS responsibility_head VARCHAR2(10);
    BEGIN
      SELECT CDRESPCTRHD
      INTO responsibility_head
      FROM SL16LOCATION
      WHERE CDLOCAT = location_code
            AND CDLOCTYPE = location_type
            AND CDSTATUS = 'A';
      RETURN responsibility_head;
    END;

  /* Gets the nuissue that should be used when inserting this item_id. */
  FUNCTION get_next_nuissue(item_id NUMBER, to_location_code VARCHAR2, to_location_type VARCHAR2, issue_date VARCHAR)
    RETURN NUMBER IS nuissue NUMBER;
    BEGIN
      SELECT max(NUISSUE)
      INTO nuissue
      FROM FD12EXPISSUE
      WHERE NUXREFCO = item_id
            AND DTISSUE = TO_DATE(SUBSTR(issue_date, 1, 10), 'YYYY-MM-DD')
            AND CDLOCTYPETO = to_location_type
            AND CDLOCATTO = to_location_code
            AND CDLOCTYPEFRM = SUPPLY_LOCATION_TYPE
            AND CDLOCATFROM = SUPPLY_LOCATION_CODE
            AND CDSTATUS = 'A';
      -- If no nuissue found, default to 0.
      IF nuissue IS NULL
      THEN
        nuissue := 0;
      END IF;
      -- Increment the current nuissue.
      RETURN nuissue + 1;
    END;

  /* Gets the standard unit size for a item's unit. */
  FUNCTION get_standard_unit_size(unit VARCHAR2)
    RETURN NUMBER IS standard_unit NUMBER;
    BEGIN
      SELECT AMSTDUNIT
      INTO standard_unit
      FROM FL12STDUNIT
      WHERE CDSTDUNIT = unit
            AND CDSTATUS = 'A';
      RETURN standard_unit;
    END;

  /* Get the current standard quantity of an item at the supply location. */
  FUNCTION get_supply_inventory(item_id VARCHAR2)
    RETURN NUMBER IS standard_quantity NUMBER;
    BEGIN
      SELECT AMQTYOHSTD
      INTO standard_quantity
      FROM FM12INVENTRY
      WHERE NUXREFCO = item_id
            AND CDLOCAT = SUPPLY_LOCATION_CODE
            AND CDLOCTYPE = SUPPLY_LOCATION_TYPE
            AND CDSTATUS = 'A';
      RETURN standard_quantity;
    END;

  /* Insert an item move into FD12ExpIssue. */
  PROCEDURE insert_item_move(requisition_id      NUMBER,
                             nuissue             NUMBER,
                             item_id             NUMBER,
                             issue_date          VARCHAR2,
                             to_location_code    VARCHAR2,
                             to_location_type    VARCHAR2,
                             issuer_uid          VARCHAR2,
                             quantity            NUMBER,
                             amqtyissstd         NUMBER,
                             item_unit           VARCHAR2,
                             responsibility_head VARCHAR2) IS
    -- Constants.
    cdrectype VARCHAR(1) := 'P'; -- Not sure what this is but its always 'P' for supply moves.
    cdstatus  VARCHAR(1) := 'A';
    cdorgid   VARCHAR(3) := 'ALL'; -- Deprecated, always ALL.
    BEGIN
      INSERT INTO FD12EXPISSUE (NUISSUE, NUXREFCO, DTISSUE, DTTXNUPDATE, DTTXNORIGIN,
                                CDLOCTYPEFRM, CDLOCTYPETO, CDRECTYPE, CDSTATUS,
                                CDLOCATFROM, CDLOCATTO, NAISSUEDBY, NATXNORGUSER,
                                NATXNUPDUSER, AMQTYISSUE, AMQTYISSSTD,
                                CDORGID, CDISSUNIT, CDRESPCTRHD, NUREQUISITIONID)
      VALUES (nuissue, item_id, TO_DATE(SUBSTR(issue_date, 1, 10), 'YYYY-MM-DD'), SYSDATE, SYSDATE, SUPPLY_LOCATION_TYPE,
              to_location_type, cdrectype, cdstatus, SUPPLY_LOCATION_CODE, to_location_code, issuer_uid, USER,
              USER, quantity, amqtyissstd, cdorgid, item_unit, responsibility_head, requisition_id);
    END;

  /* Has all columns from the insert_item_move query plus the new amqtyohstd value for the from location. */
  PROCEDURE insert_item_move_audit(requisition_id      NUMBER,
                                   nuissue             NUMBER,
                                   item_id             NUMBER,
                                   issue_date          VARCHAR2,
                                   to_location_code    VARCHAR2,
                                   to_location_type    VARCHAR2,
                                   issuer_uid          VARCHAR2,
                                   quantity            NUMBER,
                                   amqtyissstd         NUMBER,
                                   item_unit           VARCHAR2,
                                   responsibility_head VARCHAR2) IS
    new_standard_quantity_on_hand NUMBER := get_supply_inventory(item_id);
    -- Constants.
    cdrectype                     VARCHAR(1) := 'P'; -- Not sure what this is but its always 'P' for supply moves.
    cdstatus                      VARCHAR(1) := 'A';
    cdorgid                       VARCHAR(3) := 'ALL'; -- Deprecated, always ALL.
    BEGIN
      INSERT INTO FD12EXPAUDIT (NUISSUE, NUXREFCO, DTISSUE, DTTXNUPDATE, DTTXNORIGIN,
                                CDLOCTYPEFRM, CDLOCTYPETO, CDRECTYPE, CDSTATUS,
                                CDLOCATFROM, CDLOCATTO, NAISSUEDBY, NATXNORGUSER,
                                NATXNUPDUSER, AMQTYISSUE, AMQTYISSSTD, AMQTYOHSTD,
                                CDORGID, CDISSUNIT, CDRESPCTRHD, NUREQUISITIONID)
      VALUES (nuissue, item_id, TO_DATE(SUBSTR(issue_date, 1, 10), 'YYYY-MM-DD'), SYSDATE, SYSDATE, SUPPLY_LOCATION_TYPE,
              to_location_type, cdrectype, cdstatus, SUPPLY_LOCATION_CODE, to_location_code, issuer_uid, USER, USER,
              quantity, amqtyissstd, new_standard_quantity_on_hand, cdorgid, item_unit, responsibility_head, requisition_id);
    END;

  /* Subtracts items from supply location inventory. */
  PROCEDURE subtract_items_from_inventory(item_id NUMBER, issued_standard_quantity NUMBER) IS
    new_standard_quantity NUMBER;
    BEGIN
      -- Calculate new quantity.
      new_standard_quantity := get_supply_inventory(item_id) - issued_standard_quantity;
      IF new_standard_quantity < 0
      THEN
        -- New quantities should never be negative.
        RAISE_APPLICATION_ERROR(-20000, 'Cannot set inventory count to negative for item ' || item_id);
      END IF;

      -- Update with new quantity.
      UPDATE FM12INVENTRY
      SET AMQTYOHSTD = new_standard_quantity,
        DTTXNUPDATE  = SYSDATE,
        NATXNUPDUSER = USER
      WHERE NUXREFCO = item_id
            AND CDLOCAT = SUPPLY_LOCATION_CODE
            AND CDLOCTYPE = SUPPLY_LOCATION_TYPE
            AND CDSTATUS = 'A';
    END;

  /*
  Checks if a requisition has already been inserted into SFMS.
  Returns TRUE if it has already been inserted, FALSE otherwise.
   */
  FUNCTION is_duplicate(requisition_id NUMBER)
    RETURN BOOLEAN IS isDuplicate BOOLEAN;
    results NUMBER;
    BEGIN
      SELECT count(*) INTO results
      FROM fd12expissue
      WHERE NUREQUISITIONID = requisition_id
            AND cdstatus = 'A';

      -- If any rows exist with that NuRequisitionId, its a duplicate.
      IF results > 0
        THEN
        RETURN TRUE;
      END IF;

      RETURN FALSE;
    END;

  /*------------------------
  ----- Public methods -----
  --------------------------*/

  FUNCTION synchronize_with_supply(requisition_xml_string VARCHAR2)
    RETURN NUMBER IS requisition_id NUMBER;
    requisition_xml                 XMLTYPE;
    item_count                      NUMBER := 1;

    -- Requisition information.
    approved_date_time              VARCHAR2(23);
    destination_code                VARCHAR2(6);
    destination_type                VARCHAR2(1);
    issuer_uid                      VARCHAR2(12);
    responsibility_head             VARCHAR2(10);

    -- Requisition Item information.
    nuissue                         NUMBER;
    item_id                         NUMBER;
    quantity                        NUMBER;
    standard_quantity               NUMBER;
    issue_unit                      VARCHAR2(10);

    BEGIN
      requisition_xml := XMLTYPE(requisition_xml_string);

      -- Extract Requisition data
      requisition_id := requisition_xml.extract('/RequisitionView/requisitionId/text()').getNumberVal();
      approved_date_time := requisition_xml.extract('/RequisitionView/approvedDateTime/text()').getStringVal();
      destination_code := requisition_xml.extract('/RequisitionView/destination/code/text()').getStringVal();
      destination_type := requisition_xml.extract('/RequisitionView/destination/locationTypeCode/text()').getStringVal();
      issuer_uid := UPPER(requisition_xml.extract('/RequisitionView/issuer/uid/text()').getStringVal());
      responsibility_head := get_responsibility_head(destination_code, destination_type);

      -- Ensure this requisition has not been inserted before.
      IF is_duplicate(requisition_id)
        THEN
        RAISE_APPLICATION_ERROR(-20001, 'Requisition ' || requisition_id || ' has already been inserted into SFMS.');
      END IF;

      -- Loop through all items in the requisition.
      WHILE requisition_xml.existsNode('//lineItems/lineItems[' || item_count || ']') = 1
      LOOP
        -- Extract item data.
        item_id := requisition_xml.extract('//lineItems/lineItems[' || item_count || ']/item/id/text()').getNumberVal();
        quantity := requisition_xml.extract('//lineItems/lineItems[' || item_count || ']/quantity/text()').getNumberVal();
        issue_unit := requisition_xml.extract('//lineItems/lineItems[' || item_count || ']/item/unit/text()').getStringVal();
        nuissue := get_next_nuissue(item_id, destination_code, destination_type, approved_date_time);
        standard_quantity := quantity * get_standard_unit_size(issue_unit);

        -- Insert item moves.
        insert_item_move(requisition_id, nuissue, item_id, approved_date_time, destination_code, destination_type,
                         issuer_uid, quantity, standard_quantity, issue_unit, responsibility_head);
        subtract_items_from_inventory(item_id, standard_quantity);
        insert_item_move_audit(requisition_id, nuissue, item_id, approved_date_time, destination_code,
                               destination_type,
                               issuer_uid, quantity, standard_quantity, issue_unit, responsibility_head);

        -- increment and continue looping through items.
        item_count := item_count + 1;
      END LOOP;

      COMMIT;
      RETURN requisition_id;

      EXCEPTION
      WHEN OTHERS THEN
      -- If any exceptions, rollback and re raise them so supply backend can handle them.
      ROLLBACK;
      RAISE;
    END;

END SYNCHRONIZE_SUPPLY;

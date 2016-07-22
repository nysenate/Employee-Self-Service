CREATE OR REPLACE PACKAGE SYNCHRONIZE_SUPPLY AS
  -- Config
  ESS_USERNAME VARCHAR2(50) := 'caseiras';
  ESS_PASSWORD VARCHAR2(50) := 'a';
  -- Constants
  SUPPLY_LOCATION_CODE VARCHAR2(6) := 'LC100S';
  SUPPLY_LOCATION_TYPE VARCHAR2(1) := 'P';

  /*
  Gets all supply requisitions from the ESS Supply Application that are
  not in Oracle and inserts them.
  */
  PROCEDURE synchronize_with_supply;

END SYNCHRONIZE_SUPPLY;



-------------------------------------------------------------------


CREATE OR REPLACE PACKAGE BODY SYNCHRONIZE_SUPPLY AS

  /*-------------------------
  ----- Private methods -----
  ---------------------------*/

  /* Login to ESS */
  PROCEDURE login IS
    req    utl_http.req;
    res    utl_http.resp;
    url    VARCHAR2(4000) :=
    'http://10.3.13.63:8080/timesheets/login?username=' || ESS_USERNAME || '&password=' || ESS_PASSWORD ||
    '&rememberMe=false';
    name   VARCHAR2(4000);
    buffer VARCHAR2(32000);
    BEGIN
      req := utl_http.begin_request(url, 'POST', 'HTTP/1.1');
      utl_http.set_header(req, 'Accept', 'application/json');
      utl_http.set_header(req, 'Content-Type', 'application/x-www-form-urlencoded');
      res := utl_http.get_response(req);
      utl_http.read_text(res, buffer);
      utl_http.end_response(res);
      EXCEPTION
      WHEN utl_http.end_of_body THEN
      utl_http.end_response(res);
    END;

  FUNCTION get_requisitions_xml(iso_date VARCHAR2, limit NUMBER, offset NUMBER)
    RETURN XMLTYPE IS xml XMLTYPE;
    req                   utl_http.req;
    res                   utl_http.resp;
    url                   VARCHAR2(4000) :=
    'http://10.3.13.63:8080/timesheets/api/v1/supply/requisitions.xml?status=APPROVED&limit=' || limit -- TODO savedInSfms
    || '&offset=' || offset || '&to=' || iso_date;
    buffer                VARCHAR(32000);
    BEGIN
      req := UTL_HTTP.begin_request(url, 'GET', 'HTTP/1.1');
      utl_http.set_header(req, 'Accept', 'text/xml');
      res := utl_http.get_response(req);
      utl_http.read_text(res, buffer);
      utl_http.end_response(res);
      dbms_output.put_line(buffer);
      RETURN XMLTYPE(buffer);
      EXCEPTION
      WHEN utl_http.end_of_body THEN
      utl_http.end_response(res);
    END;

  /* Return a varchar2 representing the current date in ISO format. */
  FUNCTION current_iso_date
    RETURN VARCHAR2 IS date_time VARCHAR2(20);
    BEGIN
      SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD"T"HH24:MI:SS')
      INTO date_time
      FROM dual;
      RETURN date_time;
    END;

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

  -- TODO insert requisition_id
  -- TODO insure requisition_id does not already exist
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
                                CDORGID, CDISSUNIT, CDRESPCTRHD)
      VALUES (nuissue, item_id, TO_DATE(SUBSTR(issue_date, 1, 10), 'YYYY-MM-DD'), SYSDATE, SYSDATE,
                       SUPPLY_LOCATION_TYPE, to_location_type, cdrectype, cdstatus,
                       SUPPLY_LOCATION_CODE, to_location_code, issuer_uid, USER,
              USER, quantity, amqtyissstd,
              cdorgid, item_unit, responsibility_head);
    END;

  -- TODO insert requisition id.
  -- TODO insure requisition_id does not already exist
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
                                CDORGID, CDISSUNIT, CDRESPCTRHD)
      VALUES (nuissue, item_id, TO_DATE(SUBSTR(issue_date, 1, 10), 'YYYY-MM-DD'), SYSDATE, SYSDATE,
                       SUPPLY_LOCATION_TYPE, to_location_type, cdrectype, cdstatus,
                       SUPPLY_LOCATION_CODE, to_location_code, issuer_uid, USER,
              USER, quantity, amqtyissstd, new_standard_quantity_on_hand,
              cdorgid, item_unit, responsibility_head);
    END;

  /* Subtracts items from supply location inventory. */
  PROCEDURE subtract_items_from_inventory(item_id NUMBER, issued_standard_quantity NUMBER) IS
    new_standard_quantity NUMBER;
    BEGIN
      new_standard_quantity := get_supply_inventory(item_id) - issued_standard_quantity;
      IF new_standard_quantity < 0
      THEN
        RAISE_APPLICATION_ERROR(-20000, 'Cannot set inventory count to negative for item ' || item_id);
      END IF;
      UPDATE FM12INVENTRY
      SET AMQTYOHSTD = new_standard_quantity,
        DTTXNUPDATE  = SYSDATE,
        NATXNUPDUSER = USER
      WHERE NUXREFCO = item_id
            AND CDLOCAT = SUPPLY_LOCATION_CODE
            AND CDLOCTYPE = SUPPLY_LOCATION_TYPE
            AND CDSTATUS = 'A';
    END;

  PROCEDURE send_failure_to_supply(requisition_id NUMBER, error_message VARCHAR2) IS
    req     utl_http.req;
    res     utl_http.resp;
    url     VARCHAR2(4000) := 'http://10.3.13.63:8080/timesheets/api/v1/supply/error';
    message VARCHAR2(4000) := 'Error saving requisition: ' || requisition_id || '. ' || error_message;
    BEGIN
      req := utl_http.begin_request(url, 'POST', 'HTTP/1.1');
      utl_http.set_header(req, 'Accept', 'text/xml');
      utl_http.set_header(req, 'Content-Type', 'text/plain');
      utl_http.set_header(req, 'Content-Length', length(message));
      utl_http.set_body_charset('UTF-8');
      utl_http.write_text(req, message);
      res := utl_http.get_response(req);
      utl_http.end_response(res);
      EXCEPTION
      WHEN utl_http.end_of_body THEN
      utl_http.end_response(res);
    END;

  /**
   * Send a CSV of successfully inserted requisition id's to supply.
   * This is called at the end of the procedure.
   */
  PROCEDURE send_finished_to_supply(inserted_ids VARCHAR2) IS
    req    utl_http.req;
    res    utl_http.resp;
    url    VARCHAR2(4000) := 'http://10.3.13.63:8080/timesheets/api/v1/supply/sfms/synch';
    buffer VARCHAR2(32000);
    BEGIN
      dbms_output.put_line('sending results to supply: ' || inserted_ids);
      req := utl_http.begin_request(url, 'POST', 'HTTP/1.1');
      utl_http.set_header(req, 'Accept', 'text/xml');
      utl_http.set_header(req, 'Content-Type', 'text/plain');
      utl_http.set_header(req, 'Content-Length', NVL(length(inserted_ids), 0)); -- never set length to null, zero instead
      utl_http.set_body_charset('UTF-8');
      utl_http.write_text(req, inserted_ids);
      res := utl_http.get_response(req);
      utl_http.read_text(res, buffer);
      utl_http.end_response(res);
      EXCEPTION
      WHEN utl_http.end_of_body THEN
      utl_http.end_response(res);
    END;

  /*------------------------
  ----- Public methods -----
  --------------------------*/

  PROCEDURE synchronize_with_supply IS
    query_limit         NUMBER := 1;
    query_offset        NUMBER := 1;
    query_to_date       VARCHAR2(20);
    total_results       NUMBER := 1;
    requisition_xml     XMLTYPE;
    item_count          NUMBER := 1;

    -- Requisition information.
    requisition_id      NUMBER;
    approved_date_time  VARCHAR2(23);
    destination_code    VARCHAR2(6);
    destination_type    VARCHAR2(1);
    issuer_uid          VARCHAR2(12);
    responsibility_head VARCHAR2(10);

    -- Requisition Item information.
    nuissue             NUMBER;
    item_id             NUMBER;
    quantity            NUMBER;
    standard_quantity   NUMBER;
    issue_unit          VARCHAR2(10);

    -- Script processing information
    successful_inserts  VARCHAR2(32000); -- CSV of requisition_id's that were successfully inserted into oracle.

    INVALID_XML_RESPONSE EXCEPTION;
    BEGIN
      login();
      query_to_date := current_iso_date();

      -- Loop through all requisitions needing to be synchronized, inserting each one into Oracle.
      LOOP
        BEGIN
          SAVEPOINT requisition_insert_savepoint;
          -- TODO still dont like this. How to handle invalid xml exception.
          BEGIN
            requisition_xml := get_requisitions_xml(query_to_date, query_limit, query_offset);
            total_results := requisition_xml.extract('/ListViewResponse/total/text()').getNumberVal();
            EXCEPTION
            WHEN OTHERS THEN
            dbms_output.put_line(sqlerrm);
            query_offset := query_offset + 1;
          END;
          -- Exit when we have paginated through all the results.
          EXIT WHEN query_offset > total_results;

          -- Extract Requisition data
          requisition_id := requisition_xml.extract('/ListViewResponse/result/requisitionId/text()').getNumberVal();
          approved_date_time := requisition_xml.extract(
              '/ListViewResponse/result/approvedDateTime/text()').getStringVal();
          destination_code := requisition_xml.extract(
              '/ListViewResponse/result/destination/code/text()').getStringVal();
          destination_type := requisition_xml.extract(
              '/ListViewResponse/result/destination/locationTypeCode/text()').getStringVal();
          issuer_uid := UPPER(requisition_xml.extract('/ListViewResponse/result/issuer/uid/text()').getStringVal());
          responsibility_head := get_responsibility_head(destination_code, destination_type);

          -- Looping through new requisition, reset item_count;
          item_count := 1;
          -- Loop through all items in the requisition.
          WHILE requisition_xml.existsNode('//lineItems/lineItems[' || item_count || ']') = 1
          LOOP
            -- Extract item data.
            item_id := requisition_xml.extract(
                '//lineItems/lineItems[' || item_count || ']/item/id/text()').getNumberVal();
            quantity := requisition_xml.extract(
                '//lineItems/lineItems[' || item_count || ']/quantity/text()').getNumberVal();
            issue_unit := requisition_xml.extract(
                '//lineItems/lineItems[' || item_count || ']/item/unit/text()').getStringVal();
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

          -- Commit a single requisition insert.
          COMMIT;

          -- Add to list of successful inserts
          IF successful_inserts IS NULL
          THEN
            successful_inserts := requisition_id;
          ELSE
            successful_inserts := successful_inserts || ',' || requisition_id;
          END IF;

          -- Increment offset.
          query_offset := query_offset + 1;

          -- If there's any exceptions while inserting a requisition, skip to next requisition.
          EXCEPTION
          WHEN OTHERS THEN
          ROLLBACK TO requisition_insert_savepoint;
          query_offset := query_offset + 1;
          dbms_output.put_line(sqlerrm);
          send_failure_to_supply(requisition_id, sqlerrm);

        END;
      END LOOP;

      send_finished_to_supply(successful_inserts);

    END;

END SYNCHRONIZE_SUPPLY;




EXEC SYNCHRONIZE_SUPPLY.SYNCHRONIZE_WITH_SUPPLY;

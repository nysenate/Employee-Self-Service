CREATE OR REPLACE PACKAGE SYNCHRONIZE_SUPPLY AS
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
req utl_http.req;
res utl_http.resp;
url varchar2(4000) := 'http://10.3.13.63:8080/timesheets/login?username=????&password=??&rememberMe=false';
name varchar2(4000);
buffer varchar2(4000);
BEGIN
  req := utl_http.begin_request(url, 'POST',' HTTP/1.1');
  utl_http.set_header(req, 'Accept', 'application/json');
  utl_http.set_header(req, 'Content-Type', 'application/x-www-form-urlencoded');
  utl_http.set_cookie_support(req, true); -- NOT WORKING - due to lack of permissions?
  res := utl_http.get_response(req);
  -- TODO: remove print outs. Throw exception if we dont login successfully.
      -- begin
      loop
      utl_http.read_line(res, buffer);
      dbms_output.put_line(buffer);

      end loop;
      utl_http.end_response(res);
      exception
      when utl_http.end_of_body then
      utl_http.end_response(res);
      -- end;
END;


FUNCTION get_requisitions_xml RETURN XMLTYPE IS xml XMLTYPE;
  req utl_http.req;
  resp utl_http.resp;
  resp_content VARCHAR(3200);
  BEGIN
   req := UTL_HTTP.begin_request('http://localhost:8080/timesheets/api/v1/supply/requisitions.xml?status=APPROVED&limit=1'
                                         , 'GET'
                                         , 'HTTP/1.1');
   resp := utl_http.get_response(req);
   utl_http.read_text(resp, resp_content);
   dbms_output.put_line(resp_content);
   utl_http.end_response(resp);
    RETURN XMLTYPE(resp_content);
    -- TODO is this exception body needed? We do want an exception if there is an error calling the API.
    EXCEPTION
      WHEN utl_http.end_of_body THEN
        utl_http.end_response(resp);
  END;

/* Return a varchar2 representing the current date in ISO format. */
FUNCTION current_iso_date RETURN VARCHAR2 IS date_time VARCHAR2(20);
  BEGIN
    SELECT TO_CHAR(SYSDATE,'YYYY-MM-DD"T"HH24:MI:SS') INTO date_time FROM dual;
    RETURN date_time;
  END;

/*------------------------
----- Public methods ----- 
--------------------------*/

PROCEDURE synchronize_with_supply IS
  query_limit NUMBER := 1; -- Query one requisition at a time
  query_offset NUMBER := 1;
  query_to_date VARCHAR2(20);
  xml XMLTYPE;
  BEGIN
  login();
  query_to_date := current_iso_date();
--  dbms_output.put_line(TO_CHAR(query_to_date));
  xml := get_requisitions_xml();
  -- get req id
  dbms_output.put_line(xml.extract('/ListViewResponse/result/id/text()').getstringVal());
  
  dbms_output.put_line(xml.extract('/ListViewResponse/result/id').GETCLOBVAL());
    
  dbms_output.put_line(xml.extract('/ListViewResponse/result/activeVersion/lineItems/lineItems').getclobval());
--  
--  dbms_output.put_line(xml.extract('/ListViewResponse/result/id/activeVersion/lineItems//lineItems/item/id/text()').getstringVal());

  END;

END SYNCHRONIZE_SUPPLY;




exec SYNCHRONIZE_SUPPLY.SYNCHRONIZE_WITH_SUPPLY;
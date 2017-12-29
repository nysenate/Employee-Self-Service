package gov.nysenate.ess.travel.fixtures;

/**
 * Contains raw text json responses from the GSA Api to be used in tests.
 */
public class GsaApiResponseFixture {

    public static String GsaApiResponseFY2018Zip10036() {
        return gsaResponse;
    }

    private static String gsaResponse = "{\n" +
            "  \"help\": \"https:\\/\\/inventory.data.gov\\/api\\/3\\/action\\/help_show?name=datastore_search\",\n" +
            "  \"success\": true,\n" +
            "  \"result\": {\n" +
            "    \"resource_id\": \"8ea44bc4-22ba-4386-b84c-1494ab28964b\",\n" +
            "    \"fields\": [\n" +
            "      {\n" +
            "        \"type\": \"int4\",\n" +
            "        \"id\": \"_id\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"DestinationID\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"text\",\n" +
            "        \"id\": \"City\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"text\",\n" +
            "        \"id\": \"County\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"FiscalYear\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Oct\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Nov\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Dec\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Jan\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Feb\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Mar\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Apr\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"May\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Jun\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Jul\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Aug\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Sep\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Meals\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"text\",\n" +
            "        \"id\": \"State\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"numeric\",\n" +
            "        \"id\": \"Zip\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"text\",\n" +
            "        \"id\": \"LocationDefined\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"records\": [\n" +
            "      {\n" +
            "        \"City\": \"New York City\",\n" +
            "        \"Dec\": \"291\",\n" +
            "        \"Feb\": \"164\",\n" +
            "        \"Zip\": \"10036\",\n" +
            "        \"Aug\": \"230\",\n" +
            "        \"Sep\": \"291\",\n" +
            "        \"Apr\": \"253\",\n" +
            "        \"Jun\": \"253\",\n" +
            "        \"State\": \"NY\",\n" +
            "        \"Jul\": \"230\",\n" +
            "        \"Meals\": \"74\",\n" +
            "        \"County\": \"New York County, NY\",\n" +
            "        \"May\": \"253\",\n" +
            "        \"DestinationID\": \"266\",\n" +
            "        \"Mar\": \"253\",\n" +
            "        \"Jan\": \"164\",\n" +
            "        \"LocationDefined\": \"Bronx \\/ Kings \\/ New York \\/ Queens \\/ Richmond\",\n" +
            "        \"Nov\": \"291\",\n" +
            "        \"_id\": 3034,\n" +
            "        \"Oct\": \"291\",\n" +
            "        \"FiscalYear\": \"2018\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"_links\": {\n" +
            "      \"start\": \"\\/api\\/action\\/datastore_search?filters=%7B%22FiscalYear%22%3A%222018%22%2C%22Zip%22%3A%2210036%22%7D&resource_id=8ea44bc4-22ba-4386-b84c-1494ab28964b\",\n" +
            "      \"next\": \"\\/api\\/action\\/datastore_search?offset=100&filters=%7B%22FiscalYear%22%3A%222018%22%2C%22Zip%22%3A%2210036%22%7D&resource_id=8ea44bc4-22ba-4386-b84c-1494ab28964b\"\n" +
            "    },\n" +
            "    \"filters\": {\n" +
            "      \"Zip\": \"10036\",\n" +
            "      \"FiscalYear\": \"2018\"\n" +
            "    },\n" +
            "    \"total\": 1\n" +
            "  }\n" +
            "}";
}

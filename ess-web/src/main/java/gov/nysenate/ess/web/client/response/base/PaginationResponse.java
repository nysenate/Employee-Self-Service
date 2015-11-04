package gov.nysenate.ess.web.client.response.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.nysenate.ess.core.util.LimitOffset;

import java.io.IOException;

public abstract class PaginationResponse extends BaseResponse
{
    protected int total;
    protected int offsetStart;
    protected int offsetEnd;
    protected int limit;

    public PaginationResponse(int total, int offsetStart, int offsetEnd, int limit) {
        this.total = total;
        this.offsetStart = offsetStart;
        this.offsetEnd = offsetEnd;
        this.limit = limit;
    }

    public PaginationResponse(int total, LimitOffset limitOffset) {
        this(total, limitOffset.getOffsetStart(), Math.min(limitOffset.getOffsetEnd(), total), limitOffset.getLimit());
    }

    public void serialize(PaginationResponse pr, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        super.serialize(pr, jsonGenerator, serializerProvider);
        jsonGenerator.writeNumberField("total", pr.getTotal());
        jsonGenerator.writeNumberField("offsetStart", pr.getOffsetStart());
        jsonGenerator.writeNumberField("offsetEnd", pr.getOffsetEnd());
        jsonGenerator.writeNumberField("limit", pr.getLimit());
    }

    public int getTotal() {
        return total;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public int getLimit() {
        return limit;
    }
}

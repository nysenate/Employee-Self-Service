package gov.nysenate.ess.core.client.response.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;

import javax.xml.bind.annotation.XmlElement;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonSerialize(using = ListViewResponse.ListViewResponseJsonSerializer.class)
public class ListViewResponse<ViewType> extends PaginationResponse
{
    @XmlElement public ListView<ViewType> result;
    private String resultFieldName;

    protected ListViewResponse(ListView<ViewType> result, String resultFieldName, int total, LimitOffset limitOffset) {
        super(total, limitOffset);
        if (resultFieldName == null) {
            resultFieldName = "result";
        }
        this.resultFieldName = resultFieldName;
        this.result = result;
        if (result != null) {
            success = true;
            this.responseType = result.getViewType();
        }
    }

    public static <ViewType extends ViewObject> ListViewResponse<ViewType> of(Collection<ViewType> items) {
        return of(items, null,  items.size(), new LimitOffset(items.size()));
    }

    public static <ViewType extends ViewObject> ListViewResponse<ViewType> of(Collection<ViewType> items, String resultFieldName) {
        return of(items, resultFieldName, items.size(), new LimitOffset(items.size()));
    }

    public static <ViewType extends ViewObject> ListViewResponse<ViewType> of(Collection<ViewType> items, int total, LimitOffset limOff) {
        return of(items, null, total, limOff);
    }

    public static <ViewType extends ViewObject> ListViewResponse<ViewType> of(
        Collection<ViewType> items, String resultFieldName, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.of(items), resultFieldName, total, limitOffset);
    }

    public static <ModelType, ViewType extends ViewObject> ListViewResponse<ViewType> fromPaginatedList(
            PaginatedList<ModelType> paginatedList, Function<ModelType, ViewType> viewConverter) {
        List<ViewType> resultViews = paginatedList.getResults().stream()
                .map(viewConverter)
                .collect(Collectors.toList());
        return of(resultViews, paginatedList.getTotal(), paginatedList.getLimOff());
    }

    public static ListViewResponse<String> ofStringList(Collection<String> items, String resultFieldName, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.ofStringList(items), resultFieldName, total, limitOffset);
    }

    public static ListViewResponse<Integer> ofIntList(Collection<Integer> items, String resultFieldName) {
        return new ListViewResponse<>(ListView.ofIntList(items), resultFieldName, items.size(), new LimitOffset(items.size()));
    }

    public static ListViewResponse<Integer> ofIntList(Collection<Integer> items, String resultFieldName, int total, LimitOffset limitOffset) {
        return new ListViewResponse<>(ListView.ofIntList(items), resultFieldName, total, limitOffset);
    }

    public static class ListViewResponseJsonSerializer extends JsonSerializer<ListViewResponse>
    {
        @Override
        public void serialize(ListViewResponse listViewResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            listViewResponse.serialize(listViewResponse, jsonGenerator, serializerProvider);
            jsonGenerator.writeObjectField(listViewResponse.resultFieldName, listViewResponse.result);
            jsonGenerator.writeEndObject();
        }
    }
}
package gov.nysenate.ess.core.client.view.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.List;

@XmlRootElement
@JsonSerialize(using = ListView.ListViewJsonSerializer.class)
public class ListView<ViewType> implements ViewObject
{
    @XmlElement public final ImmutableList<ViewType> items;

    public static <ViewType extends ViewObject> ListView<ViewType> of(List<ViewType> items) {
        return new ListView<>(items);
    }
    public static ListView<String> ofStringList(List<String> items) {
        return new ListView<>(items);
    }
    public static ListView<Integer> ofIntList(List<Integer> items) {
        return new ListView<>(items);
    }

    private ListView(List<ViewType> items) {
        this.items = items != null ? ImmutableList.copyOf(items) : ImmutableList.of();
    }

    @Override
    public String getViewType() {
        String listContentType = items.size()>0 ? ViewObject.getViewTypeOf(items.get(0)) : "empty";
        return listContentType + " list";
    }

    public static class ListViewJsonSerializer extends JsonSerializer<ListView>
    {
        @Override
        public void serialize(ListView listView, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeStartArray(listView.items.size());
            for (Object o : listView.items) {
                jsonGenerator.writeObject(o);
            }
            jsonGenerator.writeEndArray();
        }
    }
}

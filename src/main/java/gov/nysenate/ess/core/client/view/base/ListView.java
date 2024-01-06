package gov.nysenate.ess.core.client.view.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.Collection;

@XmlRootElement
@JsonSerialize(using = ListView.ListViewJsonSerializer.class)
public class ListView<ViewType> implements ViewObject
{
    @XmlElement public final ImmutableList<ViewType> items;

    public static <ViewType extends ViewObject> ListView<ViewType> of(Collection<ViewType> items) {
        return new ListView<>(items);
    }
    public static ListView<String> ofStringList(Collection<String> items) {
        return new ListView<>(items);
    }
    public static ListView<Integer> ofIntList(Collection<Integer> items) {
        return new ListView<>(items);
    }

    private ListView(Collection<ViewType> items) {
        this.items = items != null ? ImmutableList.copyOf(items) : ImmutableList.of();
    }

    private ListView() {
        items = ImmutableList.of();
    }

    @Override
    public String getViewType() {
        String listContentType = items.size()>0 ? ViewObject.getViewTypeOf(items.get(0)) : "empty";
        return listContentType + " list";
    }

    public static class ListViewJsonSerializer extends JsonSerializer<ListView>
    {
        @Override
        public void serialize(ListView listView, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartArray(listView.items.size());
            for (Object o : listView.items) {
                jsonGenerator.writeObject(o);
            }
            jsonGenerator.writeEndArray();
        }
    }
}

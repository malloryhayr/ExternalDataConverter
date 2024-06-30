package ca.spottedleaf.dataconverter.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GsonUtil {
    public static final Gson GSON = (new GsonBuilder()).create();

    @Nullable
    public static <T> T fromNullableJson(Gson gson, String content, Class<T> type, boolean isLenient) {
        try {
            JsonReader jsonReader = new JsonReader(new StringReader(content));
            jsonReader.setLenient(isLenient);
            return gson.getAdapter(type).read(jsonReader);
        } catch (IOException ex) {
            throw new JsonParseException(ex);
        }
    }

    public static <T> T fromJson(Gson gson, String content, Class<T> type, boolean isLenient) {
        T result = fromNullableJson(gson, content, type, isLenient);

        if (result == null) {
            throw new JsonParseException("null result");
        }

        return result;
    }

    public static String toStableString(JsonElement json) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        try {
            writeValue(jsonWriter, json, Comparator.naturalOrder());
        } catch (IOException var4) {
            throw new AssertionError(var4);
        }

        return stringWriter.toString();
    }

    public static void writeValue(JsonWriter writer, @Nullable JsonElement json, @Nullable Comparator<String> comparator) throws IOException {
        if (json == null || json.isJsonNull()) {
            writer.nullValue();
        } else if (json.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
            if (jsonPrimitive.isNumber()) {
                writer.value(jsonPrimitive.getAsNumber());
            } else if (jsonPrimitive.isBoolean()) {
                writer.value(jsonPrimitive.getAsBoolean());
            } else {
                writer.value(jsonPrimitive.getAsString());
            }
        } else if (json.isJsonArray()) {
            writer.beginArray();

            for (JsonElement jsonElement : json.getAsJsonArray()) {
                writeValue(writer, jsonElement, comparator);
            }

            writer.endArray();
        } else {
            if (!json.isJsonObject()) {
                throw new IllegalArgumentException("Couldn't write " + json.getClass());
            }

            writer.beginObject();

            for (Entry<String, JsonElement> entry : sortByKeyIfNeeded(json.getAsJsonObject().entrySet(), comparator)) {
                writer.name(entry.getKey());
                writeValue(writer, entry.getValue(), comparator);
            }

            writer.endObject();
        }
    }

    private static Collection<Entry<String, JsonElement>> sortByKeyIfNeeded(
        Collection<Entry<String, JsonElement>> entries, @Nullable Comparator<String> comparator
    ) {
        if (comparator == null) {
            return entries;
        } else {
            List<Entry<String, JsonElement>> list = new ArrayList<>(entries);
            list.sort(Entry.comparingByKey(comparator));
            return list;
        }
    }
}

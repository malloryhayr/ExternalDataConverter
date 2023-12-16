package ca.spottedleaf.dataconverter.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

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

}

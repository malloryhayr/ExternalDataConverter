package ca.spottedleaf.dataconverter.minecraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import javax.annotation.Nullable;
import java.io.StringReader;

public final class ComponentUtils {

    public static final String EMPTY = createPlainTextComponent("");

    public static String createPlainTextComponent(final String text) {
        final JsonObject ret = new JsonObject();

        ret.addProperty("text", text);

        return ret.toString();
    }

    public static String createTranslatableComponent(final String key) {
        final JsonObject ret = new JsonObject();

        ret.addProperty("translate", key);

        return ret.toString();
    }

    @Nullable
    public static Component fromJsonLenient(String json) {
        JsonReader jsonreader = new JsonReader(new StringReader(json));

        jsonreader.setLenient(true);
        JsonElement jsonelement = JsonParser.parseReader(jsonreader);

        return jsonelement == null ? null : GsonComponentSerializer.gson().deserializeFromTree(jsonelement);
    }

    private ComponentUtils() {}
}

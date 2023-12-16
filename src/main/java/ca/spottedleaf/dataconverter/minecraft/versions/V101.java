package ca.spottedleaf.dataconverter.minecraft.versions;

import ca.spottedleaf.dataconverter.converters.DataConverter;
import ca.spottedleaf.dataconverter.minecraft.MCVersions;
import ca.spottedleaf.dataconverter.minecraft.datatypes.MCTypeRegistry;
import ca.spottedleaf.dataconverter.minecraft.util.ComponentUtils;
import ca.spottedleaf.dataconverter.types.MapType;
import ca.spottedleaf.dataconverter.util.GsonUtil;
import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.lang.reflect.Type;

public final class V101 {

    public static final Gson BLOCK_ENTITY_SIGN_TEXT_STRICT_JSON_FIX_GSON = (new GsonBuilder()).registerTypeAdapter(Component.class, new JsonDeserializer<Component>() {
        @Override
        public Component deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                return Component.text(jsonElement.getAsString());
            } else if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                Component mutableComponent = null;

                for(JsonElement jsonElement2 : jsonArray) {
                    Component mutableComponent2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                    if (mutableComponent == null) {
                        mutableComponent = mutableComponent2;
                    } else {
                        mutableComponent = mutableComponent.append(mutableComponent2);
                    }
                }

                return mutableComponent;
            } else {
                throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
            }
        }
    }).create();

    protected static final int VERSION = MCVersions.V15W32A + 1;

    protected static void updateLine(final MapType<String> data, final String path) {
        final String textString = data.getString(path);
        if (textString == null || textString.isEmpty() || "null".equals(textString)) {
            data.setString(path, GsonComponentSerializer.gson().serialize(Component.empty()));
            return;
        }

        Component component = null;

        if (textString.charAt(0) == '"' && textString.charAt(textString.length() - 1) == '"'
                || textString.charAt(0) == '{' && textString.charAt(textString.length() - 1) == '}') {
            try {
                component = GsonUtil.fromNullableJson(BLOCK_ENTITY_SIGN_TEXT_STRICT_JSON_FIX_GSON, textString, Component.class, true);
                if (component == null) {
                    component = Component.empty();
                }
            } catch (final JsonParseException ignored) {}

            if (component == null) {
                try {
                    component = GsonComponentSerializer.gson().deserialize(textString);
                } catch (final JsonParseException ignored) {}
            }

            if (component == null) {
                try {
                    component = ComponentUtils.fromJsonLenient(textString);
                } catch (final JsonParseException ignored) {}
            }

            if (component == null) {
                component = Component.text(textString);
            }
        } else {
            component = Component.text(textString);
        }

        data.setString(path, GsonComponentSerializer.gson().serialize(component));
    }

    public static void register() {
        MCTypeRegistry.TILE_ENTITY.addConverterForId("Sign", new DataConverter<>(VERSION) {

            @Override
            public MapType<String> convert(final MapType<String> data, final long sourceVersion, final long toVersion) {
                updateLine(data, "Text1");
                updateLine(data, "Text2");
                updateLine(data, "Text3");
                updateLine(data, "Text4");
                return null;
            }
        });
    }

    private V101() {}

}

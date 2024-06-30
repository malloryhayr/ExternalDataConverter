package ca.spottedleaf.dataconverter.minecraft.converters.custom;

import ca.spottedleaf.dataconverter.converters.DataConverter;
import ca.spottedleaf.dataconverter.minecraft.MCDataConverter;
import ca.spottedleaf.dataconverter.minecraft.MCVersions;
import ca.spottedleaf.dataconverter.minecraft.datatypes.MCTypeRegistry;
import ca.spottedleaf.dataconverter.types.ListType;
import ca.spottedleaf.dataconverter.types.MapType;
import ca.spottedleaf.dataconverter.types.ObjectType;
import ca.spottedleaf.dataconverter.util.GsonUtil;
import ca.spottedleaf.dataconverter.util.nbt.NBTUtil;
import com.google.common.base.Suppliers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

public final class V3818_Commands {

    private static final int VERSION = MCVersions.V24W07A + 1;

    private static final boolean DISABLE_COMMAND_CONVERTER = Boolean.getBoolean("Paper.DisableCommandConverter");

    private static final Logger LOGGER = LoggerFactory.getLogger(V3818_Commands.class);

    public static String toCommandFormat(final NBTCompound components) {
        final StringBuilder ret = new StringBuilder();
        ret.append('[');
        for (final Iterator<String> iterator = components.getKeys().iterator(); iterator.hasNext();) {
            final String key = iterator.next();
            ret.append(key);
            ret.append('=');
            ret.append(components.get(key).toString());
            if (iterator.hasNext()) {
                ret.append(',');
            }
        }
        ret.append(']');

        return ret.toString();
    }

    public static JsonElement convertToJson(final NBT tag) {
        // We don't have conversion utilities, but DFU does...

        return NBTUtil.convertToJson(tag);
    }

    public static void walkComponent(final JsonElement primitive) {
        if (!(primitive instanceof JsonObject root)) {
            if (primitive instanceof JsonArray array) {
                for (final JsonElement component : array) {
                    walkComponent(component);
                }
            }
            return;
        }

        final JsonElement clickEventElement = root.get("clickEvent");
        if (clickEventElement instanceof JsonObject clickEvent) {
            final JsonElement actionElement = clickEvent.get("action");
            final JsonElement cmdElement = clickEvent.get("value");
            if (actionElement instanceof JsonPrimitive action && cmdElement instanceof JsonPrimitive cmd) {
                final String actionString = action.getAsString();
                final String cmdString = cmd.getAsString();

                if ((actionString.equals("suggest_command") && cmdString.startsWith("/")) || actionString.equals("run_command")) {
                    try {
                        final Object res = MCDataConverter.convert(
                            MCTypeRegistry.DATACONVERTER_CUSTOM_TYPE_COMMAND, cmdString, MCVersions.V1_20_4, MCVersions.current
                        );
                        if (res instanceof String newCmd) {
                            clickEvent.addProperty("value", newCmd);
                        }
                    } catch (final Exception ex) {
                        LOGGER.error("Failed to convert command '" + cmdString + "'", ex);
                    }
                }
            }
        }

        final JsonElement hoverEventElement = root.get("hoverEvent");
        if (hoverEventElement instanceof JsonObject hoverEvent) {
            final JsonElement showText = hoverEvent.get("action");
            if (showText instanceof JsonPrimitive showTextPrimitive && showTextPrimitive.getAsString().equals("show_item")) {
                final JsonElement contentsElement = hoverEvent.get("contents");
                if (contentsElement instanceof JsonObject contents) {
                    final JsonElement idElement = contents.get("id");
                    final JsonElement tagElement = contents.get("tag");

                    if (idElement instanceof JsonPrimitive idPrimitive) {
                        final Map<String, NBT> itemNBT = new HashMap<>();
                        itemNBT.put("id", NBT.String(idPrimitive.getAsString()));
                        itemNBT.put("Count", NBT.Int(1));

                        if (tagElement instanceof JsonPrimitive tagPrimitive) {
                            try {
                                final NBTCompound tag = NBTUtil.parseCompoundSNBTString(tagPrimitive.getAsString());
                                itemNBT.put("tag", tag);
                            } catch (final Exception ignore) {}
                        }

                        final NBTCompound converted = MCDataConverter.convertTag(
                                MCTypeRegistry.ITEM_STACK, new NBTCompound(itemNBT), MCVersions.V1_20_4,
                                MCVersions.current
                        );

                        contents.remove("tag");

                        contents.addProperty("id", converted.getString("id"));

                        if (NBTUtil.contains(converted,"components", NBTType.TAG_Compound)) {
                            contents.add("components", convertToJson(converted.getCompound("components")));
                        }
                    }
                }
                final JsonElement valueElement = hoverEvent.get("value");
                if (valueElement instanceof JsonPrimitive valuePrimitive) {
                    try {
                        final Map<String, NBT> itemNBT = new HashMap<>(NBTUtil.parseCompoundSNBTString(valuePrimitive.getAsString()).asMapView());
                        if (NBTUtil.contains(itemNBT, "id", NBTType.TAG_String)) {
                            final boolean explicitCount = itemNBT.containsKey("Count");
                            if (!explicitCount) {
                                itemNBT.put("Count", NBT.Int(1));
                            }
                            final NBTCompound converted = MCDataConverter.convertTag(
                                MCTypeRegistry.ITEM_STACK, new NBTCompound(itemNBT), MCVersions.V1_20_4,
                                MCVersions.current
                            );

                            hoverEvent.remove("value");

                            final JsonObject contents = new JsonObject();
                            hoverEvent.add("contents", contents);

                            contents.addProperty("id", converted.getString("id"));
                            if (explicitCount) {
                                contents.addProperty("count", converted.getInt("count"));
                            }

                            if (NBTUtil.contains(converted, "components", NBTType.TAG_Compound)) {
                                contents.add("components", convertToJson(converted.getCompound("components")));
                            }
                        }
                    } catch (final Exception ignore) {}
                }
            }
        }

        final JsonElement extra = root.get("extra");
        if (extra instanceof JsonArray array) {
            for (final JsonElement component : array) {
                walkComponent(component);
            }
        }
    }

    private static String walkComponent(final String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }

        try {
            final JsonElement element = JsonParser.parseString(json);
            walkComponent(element);
            return GsonUtil.toStableString(element);
        } catch (final JsonParseException ex) {
            return json;
        } catch (final Exception ex) {
            LOGGER.error("Failed to convert text component '" + json + "'", ex);
            return json;
        }
    }

    // this is AFTER all the converters for subversion 5, so these run AFTER them
    public static void register_5() {
        if (DISABLE_COMMAND_CONVERTER) {
            return;
        }
        //NOTE(CafeStube): We can probably never fix this, as it uses a lot of internal classes and methods from Minecraft.

        // Command is already registered in walker for command blocks
//        MCTypeRegistry.DATACONVERTER_CUSTOM_TYPE_COMMAND.addConverter(new DataConverter<>(VERSION, 5) {
//            private static final Supplier<CommandArgumentUpgrader> COMMAND_UPGRADER = Suppliers.memoize(() ->
//                    CommandArgumentUpgrader.upgrader_1_20_4_to_1_20_5(999));
//
//            @Override
//            public Object convert(final Object data, final long sourceVersion, final long toVersion) {
//                if (!(data instanceof String cmd)) {
//                    return null;
//                }
//                // We use startsWith("/") because we aren't supporting WorldEdit style commands,
//                // and passing the context of whether the use supports leading slash would be high effort low return
//                try {
//                    return COMMAND_UPGRADER.get().upgradeCommandArguments(cmd, cmd.startsWith("/"));
//                } catch (final Exception ex) {
//                    LOGGER.error("Failed to convert command '" + cmd + "'", ex);
//                    return null;
//                }
//            }
//        });

        // command is not registered in any walkers for books/signs, and we don't want to do that as we would parse
        // the json every walk. instead, we create a one time converter to avoid the additional cost of parsing the json
        // for future updates

        // books
        // note: at this stage, item is converted to components, so we can use the data components type
        MCTypeRegistry.DATA_COMPONENTS.addStructureConverter(new DataConverter<>(VERSION, 5) {
            private static void walkPath(final MapType<String> data, final String path) {
                final String str = data.getString(path);
                if (str == null) {
                    return;
                }

                final String newStr = walkComponent(str);
                if (newStr != null) {
                    data.setString(path, newStr);
                }
            }

            private static void walkBookContent(final MapType<String> data, final String path) {
                if (data == null) {
                    return;
                }

                final MapType<String> content = data.getMap(path);
                if (content == null) {
                    return;
                }

                final ListType pages = content.getList("pages", ObjectType.MAP);
                if (pages == null) {
                    return;
                }

                for (int i = 0, len = pages.size(); i < len; ++i) {
                    final MapType<String> text = pages.getMap(i);

                    walkPath(text, "raw");
                    walkPath(text, "filtered");
                }
            }

            @Override
            public MapType<String> convert(final MapType<String> data, final long sourceVersion, final long toVersion) {
                walkBookContent(data, "minecraft:written_book_content");
                return null;
            }
        });

        // signs

        final DataConverter<MapType<String>, MapType<String>> signTileConverter = new DataConverter<>(VERSION, 5) {
            private static void walkText(final MapType<String> data, final String path) {
                if (data == null) {
                    return;
                }

                final MapType<String> text = data.getMap(path);
                if (text == null) {
                    return;
                }

                final ListType messages = text.getList("messages", ObjectType.STRING);
                if (messages != null) {
                    for (int i = 0, len = Math.min(4, messages.size()); i < len; ++i) {
                        messages.setString(i, walkComponent(messages.getString(i)));
                    }
                }

                final ListType filteredMessages = text.getList("filtered_messages", ObjectType.STRING);

                if (filteredMessages != null) {
                    for (int i = 0, len = Math.min(4, filteredMessages.size()); i < len; ++i) {
                        filteredMessages.setString(i, walkComponent(filteredMessages.getString(i)));
                    }
                }
            }

            @Override
            public MapType<String> convert(final MapType<String> data, final long sourceVersion, final long toVersion) {
                walkText(data, "front_text");
                walkText(data, "back_text");
                return null;
            }
        };

        MCTypeRegistry.TILE_ENTITY.addConverterForId("minecraft:sign", signTileConverter);
        MCTypeRegistry.TILE_ENTITY.addConverterForId("minecraft:hanging_sign", signTileConverter);
    }
}

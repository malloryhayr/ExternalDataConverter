package ca.spottedleaf.dataconverter.util.nbt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTByte;
import org.jglrxavpok.hephaistos.nbt.NBTByteArray;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTDouble;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.nbt.NBTFloat;
import org.jglrxavpok.hephaistos.nbt.NBTInt;
import org.jglrxavpok.hephaistos.nbt.NBTIntArray;
import org.jglrxavpok.hephaistos.nbt.NBTList;
import org.jglrxavpok.hephaistos.nbt.NBTLong;
import org.jglrxavpok.hephaistos.nbt.NBTLongArray;
import org.jglrxavpok.hephaistos.nbt.NBTShort;
import org.jglrxavpok.hephaistos.nbt.NBTString;
import org.jglrxavpok.hephaistos.nbt.NBTType;
import org.jglrxavpok.hephaistos.parser.SNBTParser;

import java.io.StringReader;
import java.util.Map;
import java.util.Objects;

public class NBTUtil {

    public static NBTCompound parseCompoundSNBTString(String string) {
        try(SNBTParser parser = new SNBTParser(new StringReader(string))) {
            return (NBTCompound) parser.parse();
        } catch (NBTException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean contains(NBTCompound compound, String tag, NBTType type) {
        return compound.containsKey(tag) && Objects.requireNonNull(compound.get(tag)).getID() == type;
    }

    public static boolean contains(Map<String, NBT> compound, String tag, NBTType type) {
        return compound.containsKey(tag) && Objects.requireNonNull(compound.get(tag)).getID() == type;
    }


    public static JsonElement convertToJson(final NBT tag) {
        switch (tag) {
            case NBTCompound entries -> {
                JsonObject object = new JsonObject();
                for (String key : entries.getKeys()) {
                    object.add(key, convertToJson(((NBTCompound) tag).get(key)));
                }
                return object;
            }
            case NBTList<?> list -> {
                JsonArray array = new JsonArray();
                for (NBT i : list.getValue()) {
                    array.add(convertToJson(i));
                }
                return array;
            }
            case NBTInt nbtInt -> {
                return new JsonPrimitive(nbtInt.getValue());
            }
            case NBTIntArray integers -> {
                JsonArray array = new JsonArray();
                for (int i : integers.getValue()) {
                    array.add(new JsonPrimitive(i));
                }
                return array;
            }
            case NBTByte nbtByte -> {
                return new JsonPrimitive(nbtByte.getValue());
            }
            case NBTByteArray bytes -> {
                JsonArray array = new JsonArray();
                for (byte i : bytes.getValue()) {
                    array.add(new JsonPrimitive(i));
                }
                return array;
            }
            case NBTLong nbtLong -> {
                return new JsonPrimitive(nbtLong.getValue());
            }
            case NBTLongArray longs -> {
                JsonArray array = new JsonArray();
                for (long i : longs.getValue()) {
                    array.add(new JsonPrimitive(i));
                }
                return array;
            }
            case NBTFloat nbtFloat -> {
                return new JsonPrimitive(nbtFloat.getValue());
            }
            case NBTDouble nbtDouble -> {
                return new JsonPrimitive(nbtDouble.getValue());
            }
            case NBTShort nbtShort -> {
                return new JsonPrimitive(nbtShort.getValue());
            }
            case NBTString nbtString -> {
                return new JsonPrimitive(nbtString.getValue());
            }
            case null, default ->
                throw new UnsupportedOperationException("Unsupported NBT type: " + tag.getClass().getName());
        }
    }


}

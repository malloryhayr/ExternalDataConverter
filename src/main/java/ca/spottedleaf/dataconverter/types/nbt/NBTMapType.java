package ca.spottedleaf.dataconverter.types.nbt;

import ca.spottedleaf.dataconverter.types.ListType;
import ca.spottedleaf.dataconverter.types.MapType;
import ca.spottedleaf.dataconverter.types.ObjectType;
import ca.spottedleaf.dataconverter.types.TypeUtil;
import ca.spottedleaf.dataconverter.types.Types;
import org.jglrxavpok.hephaistos.nbt.*;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.Set;

public final class NBTMapType implements MapType<String> {

    private final MutableNBTCompound map;

    public NBTMapType() {
        this.map = new MutableNBTCompound();
    }

    public NBTMapType(final MutableNBTCompound tag) {
        this.map = tag;
    }

    public NBTMapType(final NBTCompound tag) {
        this.map = tag.toMutableCompound();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != NBTMapType.class) {
            return false;
        }

        return this.map.equals(((NBTMapType)obj).map);
    }

    @Override
    public TypeUtil getTypeUtil() {
        return Types.NBT;
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public String toString() {
        return "NBTMapType{" +
                "map=" + this.map +
                '}';
    }

    @Override
    public int size() {
        return this.map.getSize();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<String> keys() {
        return this.map.getKeys();
    }

    public NBTCompound getTag() {
        return this.map.toCompound();
    }

    @Override
    public MapType<String> copy() {
        return new NBTMapType(new MutableNBTCompound().copyFrom(this.map));
    }

    @Override
    public boolean hasKey(final String key) {
        return this.map.get(key) != null;
    }

    @Override
    public boolean hasKey(final String key, final ObjectType type) {
        final NBT tag = this.map.get(key);
        if (tag == null) {
            return false;
        }
        final ObjectType valueType = NBTListType.getType((byte) tag.getID().getOrdinal());

        return valueType == type || (type == ObjectType.NUMBER && valueType.isNumber());
    }

    @Override
    public void remove(final String key) {
        this.map.remove(key);
    }

    @Override
    public Object getGeneric(final String key) {
        final NBT tag = this.map.get(key);
        if (tag == null) {
            return null;
        }

        return tag.getValue();
    }

    @Override
    public Number getNumber(final String key) {
        return this.getNumber(key, null);
    }

    @Override
    public Number getNumber(final String key, final Number dfl) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue();
        }
        return dfl;
    }

    @Override
    public boolean getBoolean(final String key) {
        return this.getByte(key) != 0;
    }

    @Override
    public boolean getBoolean(final String key, final boolean dfl) {
        return this.getByte(key, dfl ? (byte)1 : (byte)0) != 0;
    }

    @Override
    public void setBoolean(final String key, final boolean val) {
        this.setByte(key, val ? (byte)1 : (byte)0);
    }

    @Override
    public byte getByte(final String key) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().byteValue();
        }
        return 0;
    }

    @Override
    public byte getByte(final String key, final byte dfl) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().byteValue();
        }
        return dfl;
    }

    @Override
    public void setByte(final String key, final byte val) {
        this.map.put(key, NBT.Byte(val));
    }

    @Override
    public short getShort(final String key) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().shortValue();
        }
        return 0;
    }

    @Override
    public short getShort(final String key, final short dfl) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().shortValue();
        }
        return dfl;
    }

    @Override
    public void setShort(final String key, final short val) {
        this.map.put(key, NBT.Short(val));
    }

    @Override
    public int getInt(final String key) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().intValue();
        }
        return 0;
    }

    @Override
    public int getInt(final String key, final int dfl) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().intValue();
        }
        return dfl;
    }

    @Override
    public void setInt(final String key, final int val) {
        this.map.put(key, NBT.Int(val));
    }

    @Override
    public long getLong(final String key) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().longValue();
        }
        return 0;
    }

    @Override
    public long getLong(final String key, final long dfl) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().longValue();
        }
        return dfl;
    }

    @Override
    public void setLong(final String key, final long val) {
        this.map.put(key, NBT.Long(val));
    }

    @Override
    public float getFloat(final String key) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().floatValue();
        }
        return 0;
    }

    @Override
    public float getFloat(final String key, final float dfl) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().floatValue();
        }
        return dfl;
    }

    @Override
    public void setFloat(final String key, final float val) {
        this.map.put(key, NBT.Float(val));
    }

    @Override
    public double getDouble(final String key) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().doubleValue();
        }
        return 0;
    }

    @Override
    public double getDouble(final String key, final double dfl) {
        final NBT tag = this.map.get(key);
        if (tag.getValue() instanceof NBTNumber<?> number) {
            return number.getValue().doubleValue();
        }
        return dfl;
    }

    @Override
    public void setDouble(final String key, final double val) {
        this.map.put(key, NBT.Double(val));
    }

    @Override
    public byte[] getBytes(final String key) {
        return this.getBytes(key, null);
    }

    @Override
    public byte[] getBytes(final String key, final byte[] dfl) {
        final NBT tag = this.map.get(key);
        if (tag instanceof NBTByteArray) {
            return ((NBTByteArray)tag).getValue().copyArray();
        }
        return dfl;
    }

    @Override
    public void setBytes(final String key, final byte[] val) {
        this.map.put(key, NBT.ByteArray(val));
    }

    @Override
    public short[] getShorts(final String key) {
        return this.getShorts(key, null);
    }

    @Override
    public short[] getShorts(final String key, final short[] dfl) {
        // NBT does not support short array
        return dfl;
    }

    @Override
    public void setShorts(final String key, final short[] val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getInts(final String key) {
        return this.getInts(key, null);
    }

    @Override
    public int[] getInts(final String key, final int[] dfl) {
        final NBT tag = this.map.get(key);
        if (tag instanceof NBTIntArray) {
            return ((NBTIntArray)tag).getValue().copyArray();
        }
        return dfl;
    }

    @Override
    public void setInts(final String key, final int[] val) {
        this.map.put(key, NBT.IntArray(val));
    }

    @Override
    public long[] getLongs(final String key) {
        return this.getLongs(key, null);
    }

    @Override
    public long[] getLongs(final String key, final long[] dfl) {
        final NBT tag = this.map.get(key);
        if (tag instanceof NBTLongArray) {
            return ((NBTLongArray)tag).getValue().copyArray();
        }
        return dfl;
    }

    @Override
    public void setLongs(final String key, final long[] val) {
        this.map.put(key, NBT.LongArray(val));
    }

    @Override
    public ListType getListUnchecked(final String key) {
        return this.getListUnchecked(key, null);
    }

    @Override
    public ListType getListUnchecked(final String key, final ListType dfl) {
        final NBT tag = this.map.get(key);
        if (tag instanceof NBTList<?> list) {
            return new NBTListType(list);
        }
        return dfl;
    }

    @Override
    public void setList(final String key, final ListType val) {
        this.map.put(key, ((NBTListType)val).getTag());
    }

    @Override
    public MapType<String> getMap(final String key) {
        return this.getMap(key, null);
    }

    @Override
    public MapType<String> getMap(final String key, final MapType dfl) {
        final NBT tag = this.map.get(key);
        if (tag instanceof NBTCompoundLike) {
            return new NBTMapType(((NBTCompoundLike) tag).toMutableCompound());
        }
        return dfl;
    }

    @Override
    public void setMap(final String key, final MapType<?> val) {
        this.map.put(key, ((NBTMapType)val).getTag().toCompound());
    }

    @Override
    public String getString(final String key) {
        return this.getString(key, null);
    }

    @Override
    public String getString(final String key, final String dfl) {
        final NBT tag = this.map.get(key);
        if (tag instanceof NBTString) {
            return ((NBTString)tag).getValue();
        }
        return dfl;
    }

    @Override
    public void setString(final String key, final String val) {
        this.map.put(key, new NBTString(val));
    }
}

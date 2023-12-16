package ca.spottedleaf.dataconverter.types.nbt;

import ca.spottedleaf.dataconverter.types.ObjectType;
import ca.spottedleaf.dataconverter.types.ListType;
import ca.spottedleaf.dataconverter.types.MapType;
import ca.spottedleaf.dataconverter.types.TypeUtil;
import ca.spottedleaf.dataconverter.types.Types;
import ca.spottedleaf.dataconverter.util.nbt.MutableNBTList;
import org.jglrxavpok.hephaistos.nbt.*;

public final class NBTListType implements ListType {

    private final MutableNBTList list;

    public NBTListType() {
        this.list = new MutableNBTList();
    }

    @SuppressWarnings("unchecked")
    public NBTListType(final NBTList tag) {
        this.list = new MutableNBTList(tag.getValue(), tag.getID());
    }

    @Override
    public TypeUtil getTypeUtil() {
        return Types.NBT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != NBTListType.class) {
            return false;
        }

        return this.list.equals(((NBTListType)obj).list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public String toString() {
        return "NBTListType{" +
                "list=" + this.list +
                '}';
    }

    public NBTList<?> getTag() {
        return new NBTList<>(this.list.getType(), this.list.getList());
    }

    @Override
    public ListType copy() {
        return new NBTListType(this.getTag());
    }

    protected static ObjectType getType(final byte id) {
        switch (id) {
            case 0: // END
                return ObjectType.NONE;
            case 1: // BYTE
                return ObjectType.BYTE;
            case 2: // SHORT
                return ObjectType.SHORT;
            case 3: // INT
                return ObjectType.INT;
            case 4: // LONG
                return ObjectType.LONG;
            case 5: // FLOAT
                return ObjectType.FLOAT;
            case 6: // DOUBLE
                return ObjectType.DOUBLE;
            case 7: // BYTE_ARRAY
                return ObjectType.BYTE_ARRAY;
            case 8: // STRING
                return ObjectType.STRING;
            case 9: // LIST
                return ObjectType.LIST;
            case 10: // COMPOUND
                return ObjectType.MAP;
            case 11: // INT_ARRAY
                return ObjectType.INT_ARRAY;
            case 12: // LONG_ARRAY
                return ObjectType.LONG_ARRAY;
            default:
                throw new IllegalStateException("Unknown type: " + id);
        }
    }

    @Override
    public ObjectType getType() {
        return getType((byte) this.list.getType().getOrdinal());
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public void remove(final int index) {
        this.list.remove(index);
    }

    @Override
    public Number getNumber(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTNumber<?> number)) {
            throw new IllegalStateException();
        }
        return number.getValue();
    }

    @Override
    public byte getByte(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTNumber<?> number)) {
            throw new IllegalStateException();
        }
        return number.getValue().byteValue();
    }

    @Override
    public void setByte(final int index, final byte to) {
        this.list.set(index, NBT.Byte(to));
    }

    @Override
    public short getShort(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTNumber<?> number)) {
            throw new IllegalStateException();
        }
        return number.getValue().shortValue();
    }

    @Override
    public void setShort(final int index, final short to) {
        this.list.set(index, NBT.Short(to));
    }

    @Override
    public int getInt(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTNumber<?> number)) {
            throw new IllegalStateException();
        }
        return number.getValue().intValue();
    }

    @Override
    public void setInt(final int index, final int to) {
        this.list.set(index, NBT.Int(to));
    }

    @Override
    public long getLong(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTNumber<?> number)) {
            throw new IllegalStateException();
        }
        return number.getValue().longValue();
    }

    @Override
    public void setLong(final int index, final long to) {
        this.list.set(index, NBT.Long(to));
    }

    @Override
    public float getFloat(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTNumber<?> number)) {
            throw new IllegalStateException();
        }
        return number.getValue().floatValue();
    }

    @Override
    public void setFloat(final int index, final float to) {
        this.list.set(index, NBT.Float(to));
    }

    @Override
    public double getDouble(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTNumber<?> number)) {
            throw new IllegalStateException();
        }
        return number.getValue().doubleValue();
    }

    @Override
    public void setDouble(final int index, final double to) {
        this.list.set(index, NBT.Double(to));
    }

    @Override
    public byte[] getBytes(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTByteArray)) {
            throw new IllegalStateException();
        }
        return ((NBTByteArray)tag).getValue().copyArray();
    }

    @Override
    public void setBytes(final int index, final byte[] to) {
        this.list.set(index, NBT.ByteArray(to));
    }

    @Override
    public short[] getShorts(final int index) {
        // NBT does not support shorts
        throw new UnsupportedOperationException();
    }

    @Override
    public void setShorts(final int index, final short[] to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getInts(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTIntArray)) {
            throw new IllegalStateException();
        }
        return ((NBTIntArray)tag).getValue().copyArray();
    }

    @Override
    public void setInts(final int index, final int[] to) {
        this.list.set(index, NBT.IntArray(to));
    }

    @Override
    public long[] getLongs(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTLongArray)) {
            throw new IllegalStateException();
        }
        return ((NBTLongArray)tag).getValue().copyArray();
    }

    @Override
    public void setLongs(final int index, final long[] to) {
        this.list.set(index, NBT.LongArray(to));
    }

    @Override
    public ListType getList(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTList<?>)) {
            throw new IllegalStateException();
        }
        return new NBTListType((NBTList<?>) tag);
    }

    @Override
    public void setList(final int index, final ListType list) {
        this.list.set(index, ((NBTListType)list).getTag());
    }

    @Override
    public MapType<String> getMap(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTCompound)) {
            throw new IllegalStateException();
        }
        return new NBTMapType(((NBTCompound) tag).toMutableCompound());
    }

    @Override
    public void setMap(final int index, final MapType<?> to) {
        this.list.set(index, ((NBTMapType)to).getTag().toCompound());
    }

    @Override
    public String getString(final int index) {
        final NBT tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NBTString)) {
            throw new IllegalStateException();
        }
        return ((NBTString)tag).getValue();
    }

    @Override
    public void setString(final int index, final String to) {
        this.list.set(index, NBT.String(to));
    }

    @Override
    public void addByte(final byte b) {
        this.list.add(NBT.Byte(b));
    }

    @Override
    public void addByte(final int index, final byte b) {
        this.list.add(index, NBT.Byte(b));
    }

    @Override
    public void addShort(final short s) {
        this.list.add(NBT.Short(s));
    }

    @Override
    public void addShort(final int index, final short s) {
        this.list.add(index, NBT.Short(s));
    }

    @Override
    public void addInt(final int i) {
        this.list.add(NBT.Int(i));
    }

    @Override
    public void addInt(final int index, final int i) {
        this.list.add(index, NBT.Int(i));
    }

    @Override
    public void addLong(final long l) {
        this.list.add(NBT.Long(l));
    }

    @Override
    public void addLong(final int index, final long l) {
        this.list.add(index, NBT.Long(l));
    }

    @Override
    public void addFloat(final float f) {
        this.list.add(NBT.Float(f));
    }

    @Override
    public void addFloat(final int index, final float f) {
        this.list.add(index, NBT.Float(f));
    }

    @Override
    public void addDouble(final double d) {
        this.list.add(NBT.Double(d));
    }

    @Override
    public void addDouble(final int index, final double d) {
        this.list.add(index, NBT.Double(d));
    }

    @Override
    public void addByteArray(final byte[] arr) {
        this.list.add(NBT.ByteArray(arr));
    }

    @Override
    public void addByteArray(final int index, final byte[] arr) {
        this.list.add(index, NBT.ByteArray(arr));
    }

    @Override
    public void addShortArray(final short[] arr) {
        // NBT does not support short[]
        throw new UnsupportedOperationException();
    }

    @Override
    public void addShortArray(final int index, final short[] arr) {
        // NBT does not support short[]
        throw new UnsupportedOperationException();
    }

    @Override
    public void addIntArray(final int[] arr) {
        this.list.add(NBT.IntArray(arr));
    }

    @Override
    public void addIntArray(final int index, final int[] arr) {
        this.list.add(index, NBT.IntArray(arr));
    }

    @Override
    public void addLongArray(final long[] arr) {
        this.list.add(NBT.LongArray(arr));
    }

    @Override
    public void addLongArray(final int index, final long[] arr) {
        this.list.add(index, NBT.LongArray(arr));
    }

    @Override
    public void addList(final ListType list) {
        this.list.add(((NBTListType)list).getTag());
    }

    @Override
    public void addList(final int index, final ListType list) {
        this.list.add(index, ((NBTListType)list).getTag());
    }

    @Override
    public void addMap(final MapType<?> map) {
        this.list.add(((NBTMapType)map).getTag().toCompound());
    }

    @Override
    public void addMap(final int index, final MapType<?> map) {
        this.list.add(index, ((NBTMapType)map).getTag().toCompound());
    }

    @Override
    public void addString(final String string) {
        this.list.add(NBT.String(string));
    }

    @Override
    public void addString(final int index, final String string) {
        this.list.add(index, NBT.String(string));
    }
}

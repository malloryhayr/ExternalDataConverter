package ca.spottedleaf.dataconverter.util.nbt;

import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTInt;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.util.List;

public class MutableNBTList {
    
    private final List<NBT> list;
    private NBTType<?> type;

    public MutableNBTList(List<NBT> list, NBTType<?> type) {
        this.list = list;
        this.type = type;
    }

    public MutableNBTList() {
        this.list = new java.util.ArrayList<>();
        this.type = NBTType.TAG_End;
    }

    public void add(NBT tag) {
        if(this.updateType(tag.getID())) {
            this.list.add(tag);
        }
    }

    public void add(int index, NBT tag) {
        if(this.updateType(tag.getID())) {
            this.list.add(index, tag);
        }
    }

    public void set(int index, NBT tag) {
        if(this.updateType(tag.getID())) {
            this.list.set(index, tag);
        }
    }

    public NBT remove(int index) {
        return this.list.remove(index);
    }

    public NBT get(int index) {
        return this.list.get(index);
    }

    public int size() {
        return this.list.size();
    }

    public NBTType<?> getType() {
        return this.type;
    }

    public List<NBT> getList() {
        return this.list;
    }


    private boolean updateType(NBTType<?> type) {
        if(type == NBTType.TAG_End) {
            return false;
        } else if (this.type == NBTType.TAG_End) {
            this.type = type;
            return true;
        } else {
            return this.type == type;
        }
    }


}

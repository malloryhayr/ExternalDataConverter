package ca.spottedleaf.dataconverter.util.nbt;

import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.parser.SNBTParser;

import java.io.StringReader;

public class NBTUtil {

    public static NBTCompound parseCompoundSNBTString(String string) {
        try(SNBTParser parser = new SNBTParser(new StringReader(string))) {
            return (NBTCompound) parser.parse();
        } catch (NBTException e) {
            throw new RuntimeException(e);
        }
    }

}

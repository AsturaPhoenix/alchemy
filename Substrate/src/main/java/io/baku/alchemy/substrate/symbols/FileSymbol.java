package io.baku.alchemy.substrate.symbols;

import java.io.File;

import lombok.Getter;

@Getter
public class FileSymbol extends Symbol {
    public static String TYPE = "file";
    
    public FileSymbol(final File file) {
        super(TYPE, file);
    }
    
    public File getFile() {
        return (File)getValue();
    }
}

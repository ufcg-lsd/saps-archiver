package main.java.interfaces;

import java.io.IOException;

public class PermanentStorage {

    boolean archive(SapsImage task) throws IOException;

    boolean delete(SapsImage task) throws IOException;
    
}

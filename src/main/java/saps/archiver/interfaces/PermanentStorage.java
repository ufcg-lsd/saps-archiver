package saps.archiver.interfaces;

import saps.common.core.model.SapsImage;
import java.io.IOException;

public interface PermanentStorage {

    boolean archive(SapsImage task) throws IOException;

    boolean delete(SapsImage task) throws IOException;
    
}
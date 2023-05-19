package saps.archiver.core;

import saps.archiver.interfaces.PermanentStorageInterface;
import saps.common.core.model.SapsImage;

public class DefaultPermanentStorage implements PermanentStorage {

    public boolean archive(SapsImage task) {
        return false;
    }

    public boolean delete(SapsImage task) {
        return false;
    }
}
package saps.archiver.interfaces;

import java.util.Properties;

public class DefaultPermanentStorage implements PermanentStorage {

    public DefaultPermanentStorage(Properties properties) {
		// TODO Auto-generated constructor stub
	}

	public boolean archive(SapsImage task) {
        return false;
    }

    public boolean delete(SapsImage task) {
        return false;
    }
}
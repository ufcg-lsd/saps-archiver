package saps.archiver.core;

//TODO: missing imports

public class ArchiverMain {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        FileInputStream input = new FileInputStream([0]);
        properties.load(input);

        Archiver fetcher = createArchiver(properties);
        while (true) {
            fetcher.gc();
            fetcher.archive();
            Thread.sleep(fetcher.getDelayMilis());
        }
    }

    private static Archiver createArchiver(Properties properties) throws Exception {
        PermanentStorage permanentStorage = createPermanentStorage(properties);
        Catalog catalog = new JDBCCatalog(properties);
        Archiver archiver = new DefaultArchiver(properties, catalog, permanentStorage);
        return archiver;
    }

    private static PermanentStorage createPermanentStorage(Properties properties) throws Exception {
        String permanentStorageType = properties.getProperty(SapsPropertiesConstants.SAPS_PERMANENT_STORAGE_TYPE);
        return new NfsPermanentStorage(properties);
        throw new SapsException("Failed to recognize type of permanent storage");
  }
}
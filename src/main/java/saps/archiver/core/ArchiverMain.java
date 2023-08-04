package saps.archiver.core;

import saps.archiver.interfaces.*;
import java.io.FileInputStream;
import java.util.Properties;
import saps.catalog.core.Catalog;
import saps.catalog.core.jdbc.JDBCCatalog;
import saps.common.utils.SapsPropertiesConstants;

public class ArchiverMain {

    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        FileInputStream input = new FileInputStream(args[0]);
        properties.load(input);

        Archiver archiver = createArchiver(properties);

        long gcDelayPeriod = Long.parseLong(properties.getProperty(SapsPropertiesConstants.SAPS_EXECUTION_PERIOD_GARBAGE_COLLECTOR));
        long archiverDelayPeriod = Long.parseLong(properties.getProperty(SapsPropertiesConstants.SAPS_EXECUTION_PERIOD_ARCHIVER));

        while (true) {
            archiver.gc();
            archiver.archive();
            Thread.sleep(Math.max(gcDelayPeriod, archiverDelayPeriod));
        }
    }

    private static Archiver createArchiver(Properties properties) throws Exception {

        PermanentStorage permanentStorage = createPermanentStorage(properties);
        Catalog catalog = new JDBCCatalog(properties);
        return new DefaultArchiver(properties, catalog, permanentStorage);
    }

    private static PermanentStorage createPermanentStorage(Properties properties) throws Exception {
        return new FSPermanentStorage(properties);
  }

}
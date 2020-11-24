/* (C)2020 */
package saps.archiver.core;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import saps.catalog.core.Catalog;
import saps.catalog.core.jdbc.JDBCCatalog;
import saps.common.core.storage.PermanentStorage;
import saps.common.core.storage.PermanentStorageType;
import saps.common.core.storage.nfs.NfsPermanentStorage;
import saps.common.core.storage.swift.SwiftPermanentStorage;
import saps.common.exceptions.SapsException;
import saps.common.utils.SapsPropertiesConstants;

public class ArchiverMain {

  public static void main(String[] args) throws Exception {
    Properties properties = new Properties();
    FileInputStream input = new FileInputStream(args[0]);
    properties.load(input);

    Archiver Fetcher = createArchiver(properties);
    Fetcher.start();
  }

  private static Archiver createArchiver(Properties properties) throws Exception {
    PermanentStorage permanentStorage = createPermanentStorage(properties);
    Catalog catalog = new JDBCCatalog(properties);
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    Archiver archiver = new Archiver(properties, catalog, permanentStorage, executor);
    return archiver;
  }

  private static PermanentStorage createPermanentStorage(Properties properties) throws Exception {
    String permanentStorageType =
        properties.getProperty(SapsPropertiesConstants.SAPS_PERMANENT_STORAGE_TYPE);
    if (PermanentStorageType.SWIFT.toString().equalsIgnoreCase(permanentStorageType)) {
      return new SwiftPermanentStorage(properties);
    } else if (PermanentStorageType.NFS.toString().equalsIgnoreCase(permanentStorageType)) {
      return new NfsPermanentStorage(properties);
    }
    throw new SapsException("Failed to recognize type of permanent storage");
  }
}

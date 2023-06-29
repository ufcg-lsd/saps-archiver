/* (C)2020 */
package saps.archiver.core;

import static saps.common.core.storage.PermanentStorageConstants.INPUTDOWNLOADING_DIR;
import static saps.common.core.storage.PermanentStorageConstants.PREPROCESSING_DIR;
import static saps.common.core.storage.PermanentStorageConstants.PROCESSING_DIR;
import static saps.common.core.storage.PermanentStorageConstants.SAPS_TASK_STAGE_DIR_PATTERN;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import saps.common.core.model.SapsImage;
import saps.archiver.interfaces.PermanentStorage;
import saps.common.core.storage.exceptions.InvalidPropertyException;
import saps.common.utils.SapsPropertiesConstants;

public class FSPermanentStorage implements PermanentStorage {

  public static final Logger LOGGER = Logger.getLogger(FSPermanentStorage.class);
  public static final String FS_STORAGE_TASK_DIR_PATTERN ="%s" + File.separator + "%s" + File.separator + "%s";
  public static final String FS_STORAGE_TASK_URL_PATTERN = "%s" + File.separator + "%s";
  
  private final String FSTempStoragePath;
  private final String FSPermanentStoragePath;
  private final String tasksDirName;

  public FSPermanentStorage(Properties properties) throws InvalidPropertyException {
    this.FSTempStoragePath = properties.getProperty(SapsPropertiesConstants.SAPS_TEMP_STORAGE_PATH);
    this.FSPermanentStoragePath = properties.getProperty(SapsPropertiesConstants.FS_PERMANENT_STORAGE_PATH);
    this.tasksDirName = properties.getProperty(SapsPropertiesConstants.PERMANENT_STORAGE_TASKS_DIR); 
  }

  @Override
  public String delete(SapsImage task) throws IOException {
    String taskId = task.getTaskId();
    String taskDirPath = String.format(FS_STORAGE_TASK_DIR_PATTERN, FSPermanentStoragePath, tasksDirName, task.getTaskId());
    File taskDir = new File(taskDirPath);
    FileUtils.deleteDirectory(taskDir);
    
    return taskId;
  }

  @Override
  public boolean archive(SapsImage task) throws IOException {
      String taskId = task.getTaskId();
      LOGGER.info("Archiving task [" + taskId + "] to permanent storage.");
      String FSTaskDirPath = createTaskDir(tasksDirName, taskId);
 
      String[] dirsToCopy = {INPUTDOWNLOADING_DIR, PREPROCESSING_DIR, PROCESSING_DIR};

      for (String dir : dirsToCopy) {
          String localDir = String.format(SAPS_TASK_STAGE_DIR_PATTERN, FSTempStoragePath, taskId, dir);
          copyDirToDir(localDir, FSTaskDirPath);
      }
      return true;
  }
  
  private String createTaskDir(String tasksDir, String taskId) throws IOException {
    File FSTaskDir = new File(String.format(FS_STORAGE_TASK_DIR_PATTERN, FSPermanentStoragePath, tasksDir, taskId));
    FSTaskDir.mkdir();
    return FSTaskDir.getAbsolutePath();
  }

  private void copyDirToDir(String src, String dest) throws IOException {
    LOGGER.debug("Copying [" + src + "] into [" + dest + "]");
    FileUtils.copyDirectoryToDirectory(new File(src), new File(dest));
  }
}

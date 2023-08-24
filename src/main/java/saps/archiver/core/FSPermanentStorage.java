/* (C)2020 */
package saps.archiver.core;

import static saps.common.core.storage.PermanentStorageConstants.INPUTDOWNLOADING_DIR;
import static saps.common.core.storage.PermanentStorageConstants.PREPROCESSING_DIR;
import static saps.common.core.storage.PermanentStorageConstants.PROCESSING_DIR;
import static saps.common.core.storage.PermanentStorageConstants.SAPS_TASK_STAGE_DIR_PATTERN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import saps.common.core.model.SapsImage;
import saps.archiver.interfaces.PermanentStorage;
import saps.common.core.storage.AccessLink;
import saps.common.core.storage.exceptions.InvalidPropertyException;
import saps.common.utils.SapsPropertiesConstants;


public class FSPermanentStorage implements PermanentStorage {

  public static final Logger LOGGER = Logger.getLogger(FSPermanentStorage.class);
  public static final String FS_STORAGE_TASK_DIR_PATTERN ="%s" + File.separator + "%s" + File.separator + "%s";
  public static final String FS_STORAGE_TASK_URL_PATTERN = "%s" + File.separator + "%s";
  
  private final String FSTempStoragePath;
  private final String FSPermanentStoragePath;
  private final String tasksDirName;
  private final String baseUrl;

  public FSPermanentStorage(Properties properties) throws InvalidPropertyException {
    this.FSTempStoragePath = properties.getProperty(SapsPropertiesConstants.SAPS_TEMP_STORAGE_PATH);
    this.FSPermanentStoragePath = properties.getProperty(SapsPropertiesConstants.FS_PERMANENT_STORAGE_PATH);
    this.tasksDirName = properties.getProperty(SapsPropertiesConstants.PERMANENT_STORAGE_TASKS_DIR);
    this.baseUrl = properties.getProperty(SapsPropertiesConstants.PERMANENT_STORAGE_BASE_URL); 
  }

  @Override
  public String delete(SapsImage task) throws IOException {
    String taskId = task.getTaskId();
    String taskDirPath = String.format(FS_STORAGE_TASK_DIR_PATTERN, FSPermanentStoragePath, tasksDirName, task.getTaskId());
    File taskDir = new File(taskDirPath);
    FileUtils.deleteDirectory(taskDir);
    
    return taskId;
  }

  //TODO CHECK METHOD FOR DISPATCHER (WAITING FOR MANEL)
  @Override
  public List<AccessLink> generateAccessLinks(SapsImage task) {
    String taskId = task.getTaskId();
    List<AccessLink> taskDataLinks = new LinkedList<>();

    String dirAccessLink = String.format(FS_STORAGE_TASK_URL_PATTERN, this.baseUrl, taskId);

    // TODO check dirs
    AccessLink inputDownloadingDirAccessLink =
        new AccessLink(INPUTDOWNLOADING_DIR, dirAccessLink + File.separator + INPUTDOWNLOADING_DIR);
    AccessLink preprocessingDirAccessLink =
        new AccessLink(PREPROCESSING_DIR, dirAccessLink + File.separator + PREPROCESSING_DIR);
    AccessLink processingDirAccessLink =
        new AccessLink(PROCESSING_DIR, dirAccessLink + File.separator + PROCESSING_DIR);

    taskDataLinks.add(inputDownloadingDirAccessLink);
    taskDataLinks.add(preprocessingDirAccessLink);
    taskDataLinks.add(processingDirAccessLink);

    return taskDataLinks;
  }

  @Override
  public boolean archive(SapsImage task) throws IOException {
      String taskId = task.getTaskId();
      LOGGER.info("Archiving task [" + taskId + "] to permanent storage.");
      String FSTaskDirPath = createTaskDir(tasksDirName, taskId);
 
      String[] dirsToCopy = {INPUTDOWNLOADING_DIR, PREPROCESSING_DIR, PROCESSING_DIR};

      for (String dir : dirsToCopy) {
          String localDir = String.format(SAPS_TASK_STAGE_DIR_PATTERN, FSTempStoragePath, taskId, dir);
          try {
            copyDirToDir(localDir, FSTaskDirPath);
          } catch (FileNotFoundException e) {
            LOGGER.error("Unable to copy dir, because it was not found");
          }
      }
      return true;
  }
  
  private String createTaskDir(String tasksDir, String taskId) throws IOException {
    File FSTaskDir = new File(String.format(FS_STORAGE_TASK_DIR_PATTERN, FSPermanentStoragePath, tasksDir, taskId));
    FSTaskDir.mkdir();
    return FSTaskDir.getAbsolutePath();
  }

  private void copyDirToDir(String src, String dest) throws IOException {
    File srcDir = new File(src);
    File destDir = new File(dest);
    
    if (!srcDir.exists() || !destDir.exists()) {  
      throw new FileNotFoundException("The fs storage directory was not found");
    }

    LOGGER.debug("Copying [" + src + "] into [" + dest + "]");
    FileUtils.copyDirectoryToDirectory(srcDir, destDir);
  }
}

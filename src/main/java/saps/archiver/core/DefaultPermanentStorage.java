/* (C)2020 */
package saps.archiver.core;

import static saps.common.core.storage.PermanentStorageConstants.INPUTDOWNLOADING_DIR;
import static saps.common.core.storage.PermanentStorageConstants.PREPROCESSING_DIR;
import static saps.common.core.storage.PermanentStorageConstants.PROCESSING_DIR;
import static saps.common.core.storage.PermanentStorageConstants.SAPS_TASK_STAGE_DIR_PATTERN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import saps.common.core.model.SapsImage;
import saps.archiver.interfaces.PermanentStorage;
import saps.common.core.storage.exceptions.InvalidPropertyException;
import saps.common.utils.SapsPropertiesConstants;


public class DefaultPermanentStorage implements PermanentStorage {

  public static final Logger LOGGER = Logger.getLogger(DefaultPermanentStorage.class);
  public static final String NFS_STORAGE_TASK_DIR_PATTERN ="%s" + File.separator + "%s" + File.separator + "%s";
  public static final String NFS_STORAGE_TASK_URL_PATTERN = "%s" + File.separator + "%s";

  private final String nfsTempStoragePath;
  private final String nfsPermanentStoragePath;
  private final String tasksDirName;
 

  public DefaultPermanentStorage(Properties properties) throws InvalidPropertyException {
  
    this.nfsTempStoragePath = properties.getProperty(SapsPropertiesConstants.SAPS_TEMP_STORAGE_PATH);
    this.nfsPermanentStoragePath = properties.getProperty(SapsPropertiesConstants.NFS_PERMANENT_STORAGE_PATH);
    this.tasksDirName = properties.getProperty(SapsPropertiesConstants.PERMANENT_STORAGE_TASKS_DIR); 

  }

  @Override
  public String delete(SapsImage task) throws IOException {
    String taskId = task.getTaskId();
    String taskDirPath = String.format(NFS_STORAGE_TASK_DIR_PATTERN, nfsPermanentStoragePath, tasksDirName, task.getTaskId());
    File taskDir = new File(taskDirPath);
    FileUtils.deleteDirectory(taskDir);
    
    return taskId;
  }

  @Override
  public boolean archive(SapsImage task) throws IOException {
      String taskId = task.getTaskId();
      LOGGER.info("Archiving task [" + taskId + "] to permanent storage.");
      String nfsTaskDirPath = createTaskDir(tasksDirName, taskId);
  
      // Define the list of directories to copy
      String[] dirsToCopy = {INPUTDOWNLOADING_DIR, PREPROCESSING_DIR, PROCESSING_DIR};
      
      // Copy each directory in the list to the NFS task directory
      for (String dir : dirsToCopy) {
          String localDir = String.format(SAPS_TASK_STAGE_DIR_PATTERN, nfsTempStoragePath, taskId, dir);
          copyDirToDir(localDir, nfsTaskDirPath);
      }
      return true;
  }
  

  private String createTaskDir(String tasksDir, String taskId) throws IOException {
    File storageDir = new File(nfsPermanentStoragePath);
    
    if (!storageDir.exists()) {
      throw new FileNotFoundException(
          "The nfs storage directory [" + nfsPermanentStoragePath + "] was not found");
    }
    File nfsTaskDir = new File(String.format(NFS_STORAGE_TASK_DIR_PATTERN, nfsPermanentStoragePath, tasksDir, taskId));
    FileUtils.forceMkdir(nfsTaskDir);
    return nfsTaskDir.getAbsolutePath();
  }


  private void copyDirToDir(String src, String dest) throws IOException {
    LOGGER.debug("Copying [" + src + "] into [" + dest + "]");
    FileUtils.copyDirectoryToDirectory(new File(src), new File(dest));
  }

}









/*
*Retirei o método CheckPropeties, pois as propriedades definidas era para verificar se algum
* tipo de armazenamento havia sido selecionado (SWIFT/NFS) como usaremos apenas nfs, não tem
* necessidade dessa validação.
*/

/*
 * O debugMode, serva para armazenar o output de tasks que falharam, nesse caso optei por
 * tirar essa opção, não tera DebugMode, ela será apenas excluida
 */

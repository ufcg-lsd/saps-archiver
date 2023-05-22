package saps.archiver.core;

public class DefaultArchiver implements Archiver {
  private final Properties properties;
  private final Catalog catalog;
  private final PermanentStorage PermanentStorage;

  private final String tempStoragePath;

  private final long archiverDelayPeriod;
  private final long gcDelayPeriod;

  private static final Logger LOGGER = LOGGER.getLogger(Archiver.class);

  public Archiver(
    Properties properties,
    Catalog catalog,
    PermanentStorage permanentStorage) throws WrongConfigurationException {

    if (!checkProperties(properties)
      throw new WrongConfigurationException("Error on validate the file. Missing properties for start Saps Controller."));
        
    this.catalog = catalog;
    this.permanentStorage = permanentStorage;
    this.tempStoragePath = properties.getProperty(SapsPropertiesConstants.SAPS_TEMP_STORAGE_PATH);
    this.tempStoragePath = properties.getProperty(SapsPropertiesConstants.SAPS_TEMP_STORAGE_PATH);
    this.gcDelayPeriod = Long.parseLong(properties.getProperty(SapsPropertiesConstants.SAPS_EXECUTION_PERIOD_GARBAGE_COLLECTOR));
    this.archiverDelayPeriod = Long.parseLong(properties.getProperty(SapsPropertiesConstants.SAPS_EXECUTION_PERIOD_ARCHIVER));
  }


  public void archive() {

    List <SapsImage> tasksToArchive = CatalogUtils.getTask(catalog, ImageTaskState.FINISHED);
    for (SapsImage task: tasksToArchive) {
      updateTaskState(task, ImageTaskState.ARCHIVING);
      if (archive(task)) {
        updateTaskState(task, ImageTaskState.ARCHIVED); 
      } else {
        updateTaskState(task, ImageTaskState.FAILED);
      } deleteTempData(task);
    } 
  }

  private boolean archive(SapsImage task) {
    try {
      permanentStorage.archive(task);
      return true;
    } catch (IOException e) {
      LOGGER.error("Error archiving task [" + task.getTaskId() + "]", e);
      return false;
    }
  }

  public void gc() {

    List<SapsImage> failedTasks = CatalogUtils.getTasks(catalog, ImageTaskState.FAILED);
    failedTasks.forEach(this::deleteTempData);
  };

  private boolean checkProperties(Properties properties) {
    String[] propertiesSet = {
      SapsPropertiesConstants.IMAGE_DATASTORE_IP,
      SapsPropertiesConstants.IMAGE_DATASTORE_PORT,
      SapsPropertiesConstants.SAPS_EXECUTION_PERIOD_GARBAGE_COLLECTOR,
      SapsPropertiesConstants.SAPS_EXECUTION_PERIOD_ARCHIVER,
      SapsPropertiesConstants.SAPS_TEMP_STORAGE_PATH,
      SapsPropertiesConstants.SAPS_PERMANENT_STORAGE_TYPE
  };

  return SapsPropertiesUtil.checkProperties(properties, propertiesSet);
  }

  /**
   * It updates {@code SapsImage} state in {@code Catalog}.
   *
   * @param task task to be updated
   * @param state new task state
   * @param status new task status
   * @param error new error message
   * @param arrebolJobId new Arrebol job id
   * @return boolean representation reporting success (true) or failure (false) in update {@code
   *     SapsImage} state in {@code Catalog}
   */
  private boolean updateTaskState(SapsImage task, ImageTaskState state, String status, String error, String arrebolJobId) {
    task.setState(state);
    task.setStatus(SapsImage.NON_EXISTENT_DATA);
    task.setError(SapsImage.AVAILABLE);
    task.setArrebolJobId(SapsImage.NONE_ARREBOL_JOB_ID);

    CatalogUtils.addTimestampTask(catalog, task);

    return CatalogUtils.updateState(catalog, task);
  }

  /**
   * It deletes the data generated by {@code SapsImage} in the temp storage.
   *
   * @param task {@code SapsImage}
   */
  private void deleteTempData(SapsImage task) {    
    String taskDirPath = tempStoragePath + File.separator + task.getTaskId();
    
    File taskDir = new File(taskDirPath);
    if (taskDir.exists() && taskDir.isDirectory()) {
      LOGGER.info("Deleting temp data from task [" + task.getTaskId() + "]");
      try {
        FileUtils.deleteDirectory(taskDir);
      } catch (IOException e) {
        LOGGER.error("Error while delete task [" + task.getTaskId() + "] files from disk: ", e);
      }
    } 
  }

  /**
   * It returns the maximum value between garbage collector and archiver delay constants
   */
  public long getDelayMilis() {
    return Math.max(this.archiverDelayPeriod, this.gcDelayPeriod);
  }

}

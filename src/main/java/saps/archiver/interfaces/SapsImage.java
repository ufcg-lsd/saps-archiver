package saps.archiver.interfaces;

public interface SapsImage {

	String NON_EXISTENT_DATA = null;
	String AVAILABLE = null;
	String NONE_ARREBOL_JOB_ID = null;

	String getTaskId();
	
	String getStatus();

	void setState(ImageTaskState state);

	void setStatus(String nonExistentData);

	void setError(String available2);

	void setArrebolJobId(String noneArrebolJobId);

}

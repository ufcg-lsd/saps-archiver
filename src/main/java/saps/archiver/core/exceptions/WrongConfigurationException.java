/* (C)2020 */
package saps.archiver.core.exceptions;

public class WrongConfigurationException extends RuntimeException {

  private static final long serialVersionUID = -2520888793776997437L;

  public WrongConfigurationException(String msg) {
    super(msg);
  }
}

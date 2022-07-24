package de.bastiankrol.startexplorer.crossplatform;

import java.io.File;

/**
 * A wrapper for the call to {@link Runtime#exec(String)}.
 */
public interface IRuntimeExecDelegate
{

  /**
   * Executes the command given by <code>cmdArray</code> via
   * Runtime.exec(String).
   * 
   * @param cmdArray the shell command to execute and its arguments
   * @param workingDirectory the working directory for the command, can be null
   * @param isWindows are we running on a Windows right now? (special handling
   *          required)
   */
  public void exec(String[] cmdArray, File workingDirectory, boolean isWindows);

  void exec(String[] cmdArray, File workingDirectory);

}

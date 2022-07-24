package de.bastiankrol.startexplorer.crossplatform;

import static de.bastiankrol.startexplorer.Activator.getLogFacility;

import java.io.File;
import java.io.IOException;

import de.bastiankrol.startexplorer.util.MessageDialogHelper;

/**
 * A wrapper for the call to {@link Runtime#exec(String)}.
 */
class RuntimeExecDelegate implements IRuntimeExecDelegate
{
  private static final Runtime RUNTIME = Runtime.getRuntime();

  private MessageDialogHelper messageDialogHelper;

  /**
   * Creates an instance.
   */
  RuntimeExecDelegate()
  {
    this.messageDialogHelper = new MessageDialogHelper();
  }

  /**
   * Do not use this constructor.
   */
  // Provided for RuntimeExecDelegateForTests
  RuntimeExecDelegate(boolean dontInitializeMessageHelper)
  {
    super();
    if (!dontInitializeMessageHelper)
    {
      // does not make sense, but... whatever
      this.messageDialogHelper = new MessageDialogHelper();
    }
  }

  Runtime getRuntime()
  {
    return RUNTIME;
  }

  /**
   * {@inheritDoc}
   * 
   * @see de.bastiankrol.startexplorer.crossplatform.IRuntimeExecDelegate#exec(java.lang.String,
   *      java.io.File, boolean)
   */
  @Override
  public void exec(String[] cmdArray, File workingDirectory, boolean isWindows)
  {
    logCommand(cmdArray, workingDirectory);
    try
    {
      if (!isWindows)
      {
        this.getRuntime().exec(cmdArray, null, workingDirectory);
        // this.getRuntime().exec(
        // "dde-file-manager -n
        // /home/sky198/developer/workspace/runtime-EclipseApplication/test/src/test");
      }
      else
      {
        // Use the non-array version for windows. Contrary to intuition, this is
        // safer when it comes to paths with spaces.
        this.getRuntime().exec(cmdArray[0], null, workingDirectory);
      }
    }
    catch (IOException e)
    {
      alertMsg(e);
    }
  }

  @Override
  public void exec(String[] cmdArray, File workingDirectory)
  {
    logCommand(cmdArray, workingDirectory);
    try
    {
      // deepin 的文件管理器使用命令拼接的方式打开目录，目前未提供打开选中文件的选项
      StringBuilder command = new StringBuilder(cmdArray[0]);
      command.append(" ").append(workingDirectory.getPath());
      this.getRuntime().exec(command.toString());
    }
    catch (IOException e)
    {
      alertMsg(e);
    }
  }

  private void alertMsg(IOException e)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("The command could not be executed.");
    builder.append("\n");
    if (e.getMessage() != null)
    {
      builder.append(" Message: ");
      builder.append(e.getMessage());
      builder.append("\n");
    }
    this.messageDialogHelper.displayErrorMessage(
        "Command could not be executed", builder.toString());
  }

  private void logCommand(String[] cmdArray, File workingDirectory)
  {
    StringBuilder cmd = new StringBuilder();
    for (String cmdPart : cmdArray)
    {
      cmd.append(cmdPart);
      cmd.append(" ");
    }
    // remove last space
    if (cmd.length() > 0)
    {
      cmd.setLength(cmd.length() - 1);
    }
    getLogFacility()
        .logDebug("Executing command <" + cmd + "> in working directory <"
            + (workingDirectory != null ? workingDirectory.getAbsolutePath()
                : "null")
            + ">.");
  }
}

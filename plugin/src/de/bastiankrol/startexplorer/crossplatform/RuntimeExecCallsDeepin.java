package de.bastiankrol.startexplorer.crossplatform;

import java.io.File;

class RuntimeExecCallsDeepin extends AbstractRuntimeExecCallsLinux
{

  RuntimeExecCallsDeepin()
  {
    super();
  }

  RuntimeExecCallsDeepin(RuntimeExecDelegate delegate)
  {
    super(delegate);
  }

  @Override
  public void startFileManagerForFile(File file, boolean selectFile)
  {
    this.runtimeExecDelegate.exec(
        this.getCommandForStartFileManager(file, selectFile),
        this.getWorkingDirectoryForStartFileManager(file));
  }

  @Override
  String[] getCommandForStartFileManager(File file, boolean selectFile)
  {
    // TODO Auto-generated method stub
    return new String[] { "dde-file-manager -n", getPath(file) };
  }

  @Override
  File getWorkingDirectoryForStartFileManager(File file)
  {
    // TODO Auto-generated method stub
    return file;
  }

  @Override
  String[] getCommandForStartShell(File file)
  {
    // TODO Auto-generated method stub
    return new String[] { "deepin-terminal" };
  }

  @Override
  File getWorkingDirectoryForForStartShell(File file)
  {
    // TODO Auto-generated method stub
    return file;
  }

  @Override
  String[] getCommandForStartSystemApplication(File file)
  {
    // TODO Auto-generated method stub
    return new String[] { "deepin-terminal -w", getPath(file) };
  }

  @Override
  File getWorkingDirectoryForForStartSystemApplication(File file)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  File getWorkingDirectoryForCustomCommand(File file)
  {
    // TODO Auto-generated method stub
    return null;
  }

}

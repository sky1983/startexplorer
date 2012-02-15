package de.bastiankrol.startexplorer.preferences;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

import de.bastiankrol.startexplorer.ResourceType;
import de.bastiankrol.startexplorer.customcommands.CommandConfig;

/**
 * Dialog window to edit a custom command.
 * 
 * @author Bastian Krol
 * @version $Revision:$ $Date:$
 */
public class EditCommandConfigPane extends Dialog
{
  private CommandConfig commandConfig;
  private Text textCommand;
  private Button checkboxEnabledForResources;
  private Text textNameForResources;
  private Button checkboxEnabledForTextSelection;
  private Text textNameForTextSelection;
  private Button checkboxPassSelectedText;
  private Combo comboResourceType;
  private List<CommandConfig> commandConfigList;

  private static final Map<ResourceType, String> RESOURCE_TYPE_TO_STRING;
  private static final Map<String, ResourceType> STRING_TO_RESOURCE_TYPE;

  static
  {
    // Apache commons collection's bidirectional map would be nicer :-(
    RESOURCE_TYPE_TO_STRING = new LinkedHashMap<ResourceType, String>();
    STRING_TO_RESOURCE_TYPE = new LinkedHashMap<String, ResourceType>();
    put(ResourceType.FILE);
    put(ResourceType.DIRECTORY);
    put(ResourceType.BOTH);
  }

  private static void put(ResourceType resourceType)
  {
    RESOURCE_TYPE_TO_STRING.put(resourceType, resourceType.getLabel());
    STRING_TO_RESOURCE_TYPE.put(resourceType.getLabel(), resourceType);
  }

  /**
   * Creates a new EditCommandConfigPane to create and edit a <b>new</b> command
   * config. The CommandConfig will be added to <code>commandConfigList</code>.
   * 
   * @param parentShell the parent shell
   * @param commandConfigList the list of CommandConfigs to add the new
   *          CommandConfig to.
   */
  public EditCommandConfigPane(Shell parentShell,
      List<CommandConfig> commandConfigList)
  {
    super(parentShell);
    this.commandConfig = new CommandConfig();
    this.commandConfigList = commandConfigList;
  }

  /**
   * Creates a new EditCommandConfigPane to edit an existing commandConfig.
   * 
   * @param parentShell the parent shell
   * @param commandConfig the list of CommandConfigs to initialize the dialog
   *          with
   */
  public EditCommandConfigPane(Shell parentShell, CommandConfig commandConfig)
  {
    super(parentShell);
    this.commandConfig = commandConfig;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite dialogArea = (Composite) super.createDialogArea(parent);
    this.getShell().setText("Command Configuration");
    ((GridLayout) dialogArea.getLayout()).numColumns = 2;
    GridData gridData = new GridData(300, 13);

    Label labelCommand = new Label(dialogArea, SWT.HORIZONTAL | SWT.SHADOW_NONE);
    labelCommand.setText("Command: ");
    this.textCommand = new Text(dialogArea, SWT.SINGLE | SWT.BORDER);
    this.textCommand.setLayoutData(gridData);

    Map<String, String> proposals = new LinkedHashMap<String, String>();
    proposals.put("${resource_path}", "Absolute path to selected resource. For \"C:\\path\\to\\resource.txt\" this would be \"C:\\path\\to\\resource.txt\".");
    proposals.put("${resource_name}", "File name or directory name of the resource, without path. For \"C:\\path\\to\\resource.txt\" this would be \"resource.txt\".");
    proposals.put("${resource_parent}", "Absolute path to parent of selected resource. For \"C:\\path\\to\\resource.txt\" this would be \"C:\\path\\to\".");
    proposals.put("${resource_name_without_extension}", "File name or directory name of the resource, without path and without extension. For \"C:\\path\\to\\resource.txt\" this would be \"resource\".");
    proposals.put("${resource_extension}", "Only the file's extension, without leading dot. For \"C:\\path\\to\\resource.txt\" this would be \"txt\".");
    
    new ContentAssistCommandAdapter(this.textCommand, new TextContentAdapter(),
        new StartExplorerContentProposalProvider(proposals), null, new char[] {
            '$', '{' }, true);

    Label labelEnabledForResources = new Label(dialogArea, SWT.HORIZONTAL
        | SWT.SHADOW_NONE);
    labelEnabledForResources.setText("Enabled for resources: ");
    this.checkboxEnabledForResources = new Button(dialogArea, SWT.CHECK);

    Label labelNameForResource = new Label(dialogArea, SWT.HORIZONTAL
        | SWT.SHADOW_NONE);
    labelNameForResource.setText("Name for resources menu: ");
    this.textNameForResources = new Text(dialogArea, SWT.SINGLE | SWT.BORDER);
    this.textNameForResources.setLayoutData(gridData);

    Label labelEnabledForTextSelection = new Label(dialogArea, SWT.HORIZONTAL
        | SWT.SHADOW_NONE);
    labelEnabledForTextSelection.setText("Enabled for text selections: ");
    this.checkboxEnabledForTextSelection = new Button(dialogArea, SWT.CHECK);

    Label labelNameForTextSelection = new Label(dialogArea, SWT.HORIZONTAL
        | SWT.SHADOW_NONE);
    labelNameForTextSelection.setText("Name for text selection menu: ");
    this.textNameForTextSelection = new Text(dialogArea, SWT.SINGLE
        | SWT.BORDER);
    this.textNameForTextSelection.setLayoutData(gridData);

    Label labelResourceType = new Label(dialogArea, SWT.HORIZONTAL
        | SWT.SHADOW_NONE);
    labelResourceType.setText("Resource Type: ");
    this.comboResourceType = new Combo(dialogArea, SWT.DROP_DOWN
        | SWT.READ_ONLY);
    this.comboResourceType.setItems(RESOURCE_TYPE_TO_STRING.values().toArray(
        new String[RESOURCE_TYPE_TO_STRING.values().size()]));

    Label labelPassSelectedText = new Label(dialogArea, SWT.HORIZONTAL
        | SWT.SHADOW_NONE);
    labelPassSelectedText.setText("Pass selected text to application: ");
    this.checkboxPassSelectedText = new Button(dialogArea, SWT.CHECK);

    this.refreshViewFromModel();
    return dialogArea;
  }

  private void refreshViewFromModel()
  {
    this.textCommand.setText(this.commandConfig.getCommand());
    this.checkboxEnabledForResources.setSelection(this.commandConfig
        .isEnabledForResourcesMenu());
    this.textNameForResources.setText(this.commandConfig
        .getNameForResourcesMenu());
    this.checkboxEnabledForTextSelection.setSelection(this.commandConfig
        .isEnabledForTextSelectionMenu());
    this.textNameForTextSelection.setText(this.commandConfig
        .getNameForTextSelectionMenu());
    this.comboResourceType.setText(RESOURCE_TYPE_TO_STRING
        .get(this.commandConfig.getResourceType()));
    this.checkboxPassSelectedText.setSelection(this.commandConfig
        .isPassSelectedText());
    if (this.commandConfigList != null)
    {
      this.commandConfigList.add(this.commandConfig);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed()
  {
    this.commandConfig.setCommand(this.textCommand.getText());
    this.commandConfig
        .setEnabledForResourcesMenu(this.checkboxEnabledForResources
            .getSelection());
    this.commandConfig.setNameForResourcesMenu(this.textNameForResources
        .getText());
    this.commandConfig
        .setEnabledForTextSelectionMenu(this.checkboxEnabledForTextSelection
            .getSelection());
    ResourceType resourceType = STRING_TO_RESOURCE_TYPE
        .get(this.comboResourceType.getText());
    if (resourceType != null)
    {
      this.commandConfig.setResourceType(resourceType);
    }
    this.commandConfig
        .setNameForTextSelectionMenu(this.textNameForTextSelection.getText());
    this.commandConfig.setPassSelectedText(this.checkboxPassSelectedText
        .getSelection());
    this.close();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
   */
  @Override
  protected void cancelPressed()
  {
    this.close();
  }

  /**
   * Just for testing the page layout.
   * 
   * @param args ...
   */
  public static void main(String[] args)
  {
    Display display = Display.getDefault();
    Shell shell = new Shell(display);
    shell.open();
    EditCommandConfigPane pane = new EditCommandConfigPane(shell,
        new CommandConfig("command", ResourceType.BOTH, true,
            "name for resources", true, "name for text selection", false));
    pane.open();
    while (!shell.isDisposed())
    {
      if (!display.readAndDispatch())
      {
        display.sleep();
      }
    }
  }
}

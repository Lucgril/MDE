package mde.customrunconfigtable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

public class CustomTab extends AbstractLaunchConfigurationTab {
	
	private Text sourcemText;
	private Text targetmText;

	@Override
	public void createControl(Composite parent) {
		
		Composite comp = new Group(parent, SWT.BORDER);
        setControl(comp);

        comp.setLayout(new GridLayout());
        comp.setFont(parent.getFont());
        
        
        // Create a listener for user modifications of text fields
        ModifyListener modTextListener = new ModifyListener() {
            
          @Override
            public void modifyText(ModifyEvent e) {
                updateLaunchConfigurationDialog();
          }
        };
        
     // Models selection form
        // Group
        Group modelsGroup = new Group(comp, SWT.NONE);
        modelsGroup.setFont(comp.getFont());
        modelsGroup.setLayout(new GridLayout(3, false));
        modelsGroup.setLayoutData(
            new GridData(SWT.FILL, SWT.CENTER, true, false));
        modelsGroup.setText("Models");
        
     // Source models folder
        new Label(modelsGroup, SWT.NONE).setText("Source folder:");
        sourcemText = new Text(modelsGroup, SWT.BORDER);
        sourcemText.setLayoutData(
            new GridData(SWT.FILL, SWT.CENTER, true, false));
        sourcemText.addModifyListener(modTextListener);
        Button sourcetmButton = new Button(modelsGroup, SWT.PUSH);
        sourcetmButton.setText("Browse...");
        sourcetmButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	sourcemText.setText(selectResource(
                "Select a folder where you can get the source models",
                IResource.FOLDER));
            updateLaunchConfigurationDialog();
          }
        }); 


        // Target models folder
        new Label(modelsGroup, SWT.NONE).setText("Target folder:");
        targetmText = new Text(modelsGroup, SWT.BORDER);
        targetmText.setLayoutData(
            new GridData(SWT.FILL, SWT.CENTER, true, false));
        targetmText.addModifyListener(modTextListener);
        Button targetmButton = new Button(modelsGroup, SWT.PUSH);
        targetmButton.setText("Browse...");
        targetmButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            targetmText.setText(selectResource(
                "Select a folder where to store target model",
                IResource.FOLDER));
            updateLaunchConfigurationDialog();
          }
        });        	
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {		
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
	    try {
	      sourcemText.setText(configuration
	        .getAttribute(LaunchConfigurationAttributes.SOURCEM_TEXT, ""));
	      
	      targetmText.setText(configuration
	        .getAttribute(LaunchConfigurationAttributes.TARGETM_TEXT, ""));
	  
	    } catch (CoreException e) {
	      System.out.println("Unable to load the configuration data.");
	      e.printStackTrace();
	    }
	  }

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
				
		configuration.setAttribute(LaunchConfigurationAttributes.SOURCEM_TEXT,
				sourcemText.getText());
		
		configuration.setAttribute(LaunchConfigurationAttributes.TARGETM_TEXT,
				targetmText.getText());
				
	}

	@Override
	public String getName() {
		 return "JTL Table";
	}
	
	private String selectResource(final String title, final int resourceType) {
		ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(
				getShell(),
				ResourcesPlugin.getWorkspace().getRoot(),
				resourceType);
		dialog.setTitle(title);
		if (dialog.open() == ResourceListSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			return ((IResource) result[0]).getFullPath().toString();
		}
		return null;
	}
	
}

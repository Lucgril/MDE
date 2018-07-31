package mde.customrunconfigtable;

import java.io.File;
import java.util.LinkedList;
import java.util.TreeMap;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.ecore.EObject;

import mde.createtable.GenerateTable;
import mde.createtable.Table;

public class CustomLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration,
					   String mode,
					   ILaunch launch,
					   IProgressMonitor monitor)
			throws CoreException {

		// Workspace absolute path
		final String wsPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();

		// Source models folder
		final File sourcemFolder = new File(configuration
				.getAttribute(LaunchConfigurationAttributes.SOURCEM_TEXT, ""));

		// Target model folder
		final File targetmFolder = new File(configuration
				.getAttribute(LaunchConfigurationAttributes.TARGETM_TEXT, ""));

		
		Table createTable = new Table(new File(wsPath + sourcemFolder.getAbsolutePath()), new File(wsPath + targetmFolder.getAbsolutePath()));
		TreeMap<String, LinkedList<LinkedList<EObject>>> res = createTable.createTable();
		
		GenerateTable table = new GenerateTable("file:" + wsPath + targetmFolder.getAbsolutePath() + "/Table.xmi");		
		table.createTableModel(res, createTable.getModelNameList());
		
		System.out.println("");
		System.out.println("Finished!");
	
	}

}

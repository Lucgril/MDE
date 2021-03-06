package mde.customrunconfigtable.util;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class RegisterMetamodel {

	  public static void registerMetamodel(final File file) {
	    // Create a Resource Set
	    ResourceSet rs = new ResourceSetImpl();

	    // Get the EPackage element
	    URI uri = URI.createURI(file.getPath());
	    Resource mmResource;
	    try {
	      mmResource = rs.getResource(uri, true);
	    } catch(Exception e) {
	      uri = URI.createFileURI(uri.path());
	      mmResource = rs.getResource(uri, true);
	    }
	    EObject mmPackage = mmResource.getContents().get(0);

	    // Register the metamodel namespace URI
	    if (mmPackage.eClass().getName().equals("EPackage")) {
	      EPackage p = (EPackage) mmPackage;
	      String nsURI = p.getNsURI();
	      if (nsURI == null) {
	        nsURI = p.getName();
	        p.setNsURI(nsURI);
	      }
	      EPackage.Registry.INSTANCE.put(nsURI, p);
	    }
	  }
	}
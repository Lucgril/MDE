package mde.createtable;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import Table.Cell;
import Table.Element;
import Table.Row;
import Table.Table;
import Table.TableFactory;


public class GenerateTable {
	
	Table table;
	TableFactory tableFactory;
	Row row;
	Cell cell;
	Element element;
	LinkedList<LinkedList<EObject>> list = new LinkedList<LinkedList<EObject>>();

	String outputFilePath;
	
	/*
	 * Create Table
	 */
	public GenerateTable (String outputFilePath) {
		
		this.outputFilePath = outputFilePath;
		
		tableFactory = TableFactory.eINSTANCE;	
		
		table = tableFactory.createTable();
		table.setName("UTable");
	}
	
	/*
	 * Create Table Elements
	 */
	public void createTableModel (TreeMap<String, LinkedList<LinkedList<EObject>>> map, LinkedList<String> modelName) {
		
		/*
		 * Create Rows 
		 */
		
		for(String key: map.keySet()) {
			
			int i = 0;
			
			row = tableFactory.createRow();
			row.setName(key);
			table.getRows().add(row);
			list = map.get(key);
			
			//LinkedList<LinkedList<String>> help = new LinkedList<LinkedList<String>>();
			
			/*
			 * Create Columns
			 */
		
			for(LinkedList<EObject> x: list) {

					cell = tableFactory.createCell();
					
					/*Settare il nome della colonna*/
					cell.setName(modelName.get(i));
					//cell.setName("NameModel_" + i);
					
					/*
					 * Create Cell Elements
					 */
					for (EObject e : x) {
						
						element = tableFactory.createElement();
						if(e != null) {
							element.setElement(e);	
						}
						cell.getContains().add(element);					
					}
					
					row.getCells().add(cell);
					i++;
				}				
		}	
		serializeModel(this.outputFilePath, table);
	}
	
	
	@SuppressWarnings({"unchecked", "rawtypes" })
	void serializeModel(String modelURI, EObject element) {
		
		ResourceSet resourceSet;
		Resource resource; 
		
		/* Map of the options to be included in the XML file
		 * e. g. xsi:schemaLocation
		 */
		Map options;
		
		options = new HashMap();
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new  XMLResourceFactoryImpl());
		
		resource = resourceSet.createResource(URI.createURI(modelURI));
		resource.getContents().add(element);
		
		options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
		
		try {
			resource.save(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}	
}

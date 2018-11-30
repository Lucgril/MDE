package mde.createtable;

import java.io.File;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;

public class Table {
	
	
	File inputFolderPath;
	File outputFolderPath;
	
	XMIResource leftRes;

	private LinkedList<String> modelNames = new LinkedList<String>();
	
	public Table(File inputFolderPath, File outputFolderPath) {
		this.inputFolderPath = inputFolderPath;
		this.outputFolderPath = outputFolderPath;
	}

	public TreeMap<String, LinkedList<LinkedList<EObject>>> createTable() {  
		
		/*
		 * Metodo utilizzato per comporre la struttura dati che verrà utilizzata per creare
		 * 
		 * la tabella. Selezionata la cartella con i modelli, ogni file verrà 'parsato'
		 * 
		 * restituendo una mappa avente come chiave il numero del modello corrente e come valore il risultato del parsing
		 * 
		 * di quel file
		 */

		ResourceSet resourceSet1;
		ResourceSet resourceSet2;
		
		resourceSet1 = new ResourceSetImpl();
		
		TreeMap<Integer, TreeMap<String, LinkedList<EObject>>> allModels = new TreeMap<Integer, TreeMap<String, LinkedList<EObject>>>();
		int numModel = 0;

		for (final File fileEntry : this.inputFolderPath.listFiles()) {

			print("file:" + this.inputFolderPath.toString() + "/" + fileEntry.getName());
			Resource resource = resourceSet1.getResource(
					URI.createURI("file:" + this.inputFolderPath.toString() + "/" + fileEntry.getName()), true);
			
			addToModelNameList(fileEntry.getName());
			
			resourceSet2 = new ResourceSetImpl();
				
			load(fileEntry.getAbsolutePath(), resourceSet2);
			
			leftRes = ((XMIResource)resourceSet2.getResources().get(0));
			print("IL NOME DEL FILE ");
			print(fileEntry.getName());

			TreeIterator<EObject> object1 = resource.getAllContents();
			
			TreeMap<String, LinkedList<EObject>> res = createModelMap(object1);
			
			allModels.put(numModel, res);

			numModel++;
			
		}

		return parseMaps(allModels);		
	}

	private TreeMap<String, LinkedList<LinkedList<EObject>>> parseMaps(
			TreeMap<Integer, TreeMap<String, LinkedList<EObject>>> allModels) {
		
	
		/*
		 * Prende in input la mappa con tutti i modelli e li combina per creare la struttura da trasformare in tabella
		 *
		 *
		 *
		 */

		TreeMap<String, LinkedList<LinkedList<EObject>>> resFinal = new TreeMap<String, LinkedList<LinkedList<EObject>>>();

		for (int x : allModels.keySet()) {
			
			for (String key : allModels.get(x).keySet()) {

				LinkedList<EObject> values = allModels.get(x).get(key);

				if (resFinal.containsKey(key)) {
					resFinal.get(key).set(x, values);
				} else {

					resFinal.put(key, new LinkedList<LinkedList<EObject>>());

					for (int i = 0; i < allModels.size(); i++) {

						LinkedList<EObject> tmp = new LinkedList<EObject>();

						if (i == x) {
							tmp = values;
						} else {
							tmp.add(null);
						}
						resFinal.get(key).add(tmp);
					}
				}
			}
		}

		for (String key : resFinal.keySet()) {
			for (LinkedList<EObject> s : resFinal.get(key)) {
				if (s.size() == 0) {
					s.add(null);
				}
			}
		}
		return resFinal;
	}

	
	private TreeMap<String, LinkedList<EObject>> createModelMap(TreeIterator<EObject> object1) {
		
		TreeMap<String, LinkedList<EObject>> result = new TreeMap<String, LinkedList<EObject>>();
		EObject curr;
		
		String name = "";
		
		boolean rootFound = false;
		
		while(object1.hasNext()){
			
			name = "";
			
			curr = object1.next();
			
			if(!rootFound){
				
				name = curr.eClass().getName();
				rootFound = true;
				
			}
			else {
				name = composeName(curr);	
			}
			
			if (result.containsKey(name)) {
				
				result.get(name).add(curr);
				
			}
			else {
				LinkedList<EObject> tmp = new LinkedList<EObject>();
				tmp.add(curr);
				result.put(name, tmp);
			}
			
			List<EStructuralFeature> listAttribute = curr.eClass().getEStructuralFeatures();
					
			for(EStructuralFeature esf: listAttribute){
				
				LinkedList<EObject> val = new LinkedList<EObject>();
				
				if(curr.eGet(esf) instanceof EList){
					
					for(EObject x: (EList<EObject>)curr.eGet(esf)){
						print("Se è una lista");
						print(x);
						val.add(x);
					}
						
					print(name +  "." + esf.getName());
					print(val);
					
					result.put(name +  "." + esf.getName(), val);
				}
				else {
					if(curr.eGet(esf) == null){
						val.add(null);
					}
					else {
						if(esf.getName().equals("father") || esf.getName().equals("mother")) {
							val.add(retrieveEObject(getName(curr.eGet(esf).toString()), esf));
						}
					}
					result.put(name + "." + esf.getName(), val);
				}
			}	
		}	
		return result;
	}
	
	
	private String composeName(EObject curr){
		String result = "";
		String name = "";
		List<EStructuralFeature> listAttribute = curr.eClass().getEStructuralFeatures();
		
		for(EStructuralFeature esf: listAttribute) {
			if(curr.eGet(esf) instanceof String) {
				name = (String) curr.eGet(esf);
			}	
		}
		
		//result = curr.eClass().getName() + "." + getValue(curr.toString()) + "." + retrieveID(curr);
		result = curr.eClass().getName() + "." + name + "." + retrieveID(curr);
	
		return result;
	}

	
	public String retrieveID(EObject object) {
		
		TreeIterator <EObject> leftIterator = leftRes.getAllContents();
		
		Random rand = new Random();
		EObject temp;
		String idStr;
		
		idStr = null;
		
		while(leftIterator.hasNext()) {
			temp = leftIterator.next();
			if(EcoreUtil.equals(object, temp))
				idStr = leftRes.getID(temp);
		}
		
		if(idStr != null){ 

			return idStr.split("_")[1];
		}
		
		return String.valueOf(rand.nextInt(100));
	}
	
	public EObject retrieveEObject(String object, EStructuralFeature feature) {
		
		TreeIterator <EObject> leftIterator = leftRes.getAllContents();
		
		EObject temp = null;
				
		while(leftIterator.hasNext()) {
			temp = leftIterator.next();
			print("temp: " + temp);
			if(getName(temp.toString()) != "" && getName(temp.toString()).equals(object)) {
				return temp;
			}
		}
		temp = null;
		return temp;
	}

	
	public String getName(String match){
		Pattern pattern = Pattern.compile("[(]name(.*)");
		Matcher matcher = pattern.matcher(match);
		String result = "";
		
		if(matcher.find()){
			result = matcher.group(1);
			result = result.substring(2, result.length() - 1);
		}
		
		return result;
	}

	private void print(Object e) {
		if (e instanceof TreeMap) {
			for (Object x : ((TreeMap) e).keySet()) {
				System.out.println("key " + x);
				System.out.println("value " + ((TreeMap) e).get(x));
			}
		} else {
			System.out.println(e);
		}
	}
	
	
	private void addToModelNameList(String name){
		this.modelNames.add(name);	
	}
	
	
	public LinkedList<String> getModelNameList(){
		return this.modelNames;	
	}
	
	
	public void load(String absolutePath, ResourceSet resourceSet) {
		URI uri;
		uri = URI.createFileURI(absolutePath);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());		  
		
		/* Resource will be loaded within the resource set */
		resourceSet.getResource(uri, true);
	}	
}

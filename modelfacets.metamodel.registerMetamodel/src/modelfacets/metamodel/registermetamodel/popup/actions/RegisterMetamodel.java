package modelfacets.metamodel.registermetamodel.popup.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Javier Canovas (me@jlcanovas.es)
 *
 */
public class RegisterMetamodel implements IObjectActionDelegate{

	protected ISelection selection;

	public RegisterMetamodel() {
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	public void run(IAction action){
		Iterator it = ((IStructuredSelection) selection).iterator();
		while (it.hasNext()) {
			IFile file = (IFile) it.next(); 
			String fileName = file.getFullPath().toOSString();
			try {
				register(URI.createPlatformResourceURI(fileName, true), EPackage.Registry.INSTANCE);
			}
			catch (Exception ex) {
				System.err.println("Metamodel " + fileName + " could not be registered: " + ex);
			}
		}
	}

	private List<EPackage> register(URI uri, EPackage.Registry registry) throws Exception {

		List<EPackage> ePackages = new ArrayList();

		Map etfm = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
		if (!etfm.containsKey("*")) {
			etfm.put("*", new XMIResourceFactoryImpl());
		}

		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		Resource metamodel = resourceSet.createResource(uri);
		metamodel.load(Collections.EMPTY_MAP);

		setDataTypesInstanceClasses(metamodel);

		Iterator it = metamodel.getAllContents();
		while (it.hasNext()) {
			Object next = it.next();
			if (next instanceof EPackage) {
				EPackage p = (EPackage) next;
				if (p.getNsURI() == null) {
					if (p.getESuperPackage() == null) {
						p.setNsURI(p.getName());
					}
					else {
						p.setNsURI(p.getESuperPackage().getNsURI() + "/" + p.getName());
					}
				}
				if (p.getNsPrefix() == null) {
					if (p.getESuperPackage() != null) {
						if (p.getESuperPackage().getNsPrefix()!=null) {
							p.setNsPrefix(p.getESuperPackage().getNsPrefix() + "." + p.getName());
						}
						else {
							p.setNsPrefix(p.getName());
						}
					}
				}
				registry.put(p.getNsURI(), p);
				metamodel.setURI(URI.createURI(p.getNsURI()));
				ePackages.add(p);
			}
		}

		return ePackages;

	}

	private void setDataTypesInstanceClasses(Resource metamodel) {
		Iterator it = metamodel.getAllContents();
		while (it.hasNext()) {
			EObject eObject = (EObject) it.next();
			if (eObject instanceof EEnum) {
				// ((EEnum) eObject).setInstanceClassName("java.lang.Integer");
			} else if (eObject instanceof EDataType) {
				EDataType eDataType = (EDataType) eObject;
				String instanceClass = "";
				if (eDataType.getName().equals("String")) {
					instanceClass = "java.lang.String";
				} else if (eDataType.getName().equals("Boolean")) {
					instanceClass = "java.lang.Boolean";
				} else if (eDataType.getName().equals("Integer")) {
					instanceClass = "java.lang.Integer";
				} else if (eDataType.getName().equals("Float")) {
					instanceClass = "java.lang.Float";
				} else if (eDataType.getName().equals("Double")) {
					instanceClass = "java.lang.Double";
				}
				eDataType.setInstanceClassName(instanceClass);
			}
		}
	}
}

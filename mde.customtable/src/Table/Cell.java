/**
 */
package Table;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Cell</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link Table.Cell#getContains <em>Contains</em>}</li>
 * </ul>
 *
 * @see Table.TablePackage#getCell()
 * @model
 * @generated
 */
public interface Cell extends Named {
	/**
	 * Returns the value of the '<em><b>Contains</b></em>' containment reference list.
	 * The list contents are of type {@link Table.Element}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Contains</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Contains</em>' containment reference list.
	 * @see Table.TablePackage#getCell_Contains()
	 * @model containment="true"
	 * @generated
	 */
	EList<Element> getContains();

} // Cell

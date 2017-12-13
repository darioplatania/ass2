
package it.polito.dp2.NFV.sol2;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.polito.dp2.NFV.sol2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.polito.dp2.NFV.sol2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Nodes }
     * 
     */
    public Nodes createNodes() {
        return new Nodes();
    }

    /**
     * Create an instance of {@link Relationships }
     * 
     */
    public Relationships createRelationships() {
        return new Relationships();
    }

    /**
     * Create an instance of {@link Labels }
     * 
     */
    public Labels createLabels() {
        return new Labels();
    }

    /**
     * Create an instance of {@link it.polito.dp2.NFV.sol2.Node }
     * 
     */
    public it.polito.dp2.NFV.sol2.Node createNode() {
        return new it.polito.dp2.NFV.sol2.Node();
    }

    /**
     * Create an instance of {@link Properties }
     * 
     */
    public Properties createProperties() {
        return new Properties();
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link Nodes.Node }
     * 
     */
    public Nodes.Node createNodesNode() {
        return new Nodes.Node();
    }

    /**
     * Create an instance of {@link it.polito.dp2.NFV.sol2.Relationship }
     * 
     */
    public it.polito.dp2.NFV.sol2.Relationship createRelationship() {
        return new it.polito.dp2.NFV.sol2.Relationship();
    }

    /**
     * Create an instance of {@link Relationships.Relationship }
     * 
     */
    public Relationships.Relationship createRelationshipsRelationship() {
        return new Relationships.Relationship();
    }

}

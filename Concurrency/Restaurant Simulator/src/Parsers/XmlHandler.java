package Parsers;
import java.io.File;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


/**
 * This class represents the read from an XML File
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class XmlHandler {
	/**
	 * This method reads an xml file and returns its elements
	 * @param strXmlFile - a given xml file
	 * @return list of the elements under the root in the file
	 */
	public static NodeList readXML(String strXmlFile, String strXsd) throws Exception
	{
		NodeList nlChildNodes = null;
		try
		{			
			// Reads the xml file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setIgnoringElementContentWhitespace(true);
			Schema schema = SchemaFactory.newInstance(
	                XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(strXsd));
			dbFactory.setSchema(schema);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			
			// Parses the xml file into an xml document
			Document docFile = dBuilder.parse(strXmlFile);
			
			// Normalizes the document (clear spaces and ect..)
			docFile.getDocumentElement().normalize();
			
			nlChildNodes = docFile.getChildNodes().item(0).getChildNodes();
			
			
		}
		catch (Exception ex)
		{
			Logger.getLogger(XmlHandler.class.getName()).warning("Could not open file " +
					strXmlFile + " or file " + strXsd);
			throw ex;
		}
		return (nlChildNodes);
	}

	
	/**
	 * This method receives element node and returns its child elements nodes
	 * @param ndElement - element node
	 * @return the given's node elements children
	 */
	public static NodeList getChildNodes(Node ndElement)
	{
		NodeList nlChildElements = null;
		
		if (ndElement.getNodeType() == Node.ELEMENT_NODE)
		{
			// Gets the element's children if exist
			if (ndElement.hasChildNodes())
			{
				nlChildElements = ndElement.getChildNodes();
			}
		}
		
		return (nlChildElements);
	}

	/**
	 * This method gets a given node's attributes
	 * @param ndElement - given element ndoe
	 * @return NamedNodeMap contains all the given node's attributes
	 */
	public static NamedNodeMap getNodeAttribute(Node ndElement)
	{
		NamedNodeMap nnmNodeAttributes = null;
		
		if (ndElement.getNodeType() == Node.ELEMENT_NODE)
		{
			// If the node has attributes - gets them
			if (ndElement.hasAttributes())
			{
				nnmNodeAttributes = ndElement.getAttributes();
			}
		}
		
		return (nnmNodeAttributes);
	}
	
	/**
	 * This method returns a given node's content
	 * @param ndElement
	 * @return
	 */
	public static String getContent(Node ndElement)
	{
		String strContent = "";
		
		if (ndElement.getNodeType() == Node.ELEMENT_NODE)
		{
			// Gets the text content
			strContent = ndElement.getTextContent();
		}
		
		return (strContent);
	}
	
	/**
	 * This method returns all the nodes that in a given node's list that contains the same
	 * tag as the given tag name
	 * @param ndNodes - list of nodes
	 * @param strTag - the wanted nodes tag
	 * @return the node that has the given tag name  
	 */
	public static Node findNodes(NodeList nlNodes, String strTag)
	{
		Node ndWantedNode = null;
		
		// Goes over the nodes unil found the wanted node
		for (int nCurNodeIndex = 0; nCurNodeIndex < nlNodes.getLength(); nCurNodeIndex++)
		{
			Node ndCurNode = nlNodes.item(nCurNodeIndex);
			
			if (ndCurNode.getNodeType() == Node.ELEMENT_NODE)
			{
				// Saves the node and stop searching
				if (ndCurNode.getNodeName().equals(strTag))
				{
					ndWantedNode = ndCurNode;
					break;
				}
			}
		}
		
		return (ndWantedNode);
	}
}

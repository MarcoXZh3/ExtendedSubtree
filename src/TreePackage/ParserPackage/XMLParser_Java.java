/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TreePackage.ParserPackage;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document; 
import org.xml.sax.SAXException;
/**
 *
 * @author Ali
 */
public class XMLParser_Java {
    
    DocumentBuilder docBuilder;
            
    public XMLParser_Java(){
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();      
        } 
        catch (ParserConfigurationException e) {
            System.err.println("DocumentBuilder error in XMLParser_Java");
        }
    }
    
    public Document parse(String xmlFileName){
        try {
            return docBuilder.parse(xmlFileName);
        } 
        catch (SAXException | IOException ex) {
            throw new RuntimeException("XMLParser_Java: parse error, "+ex.toString());
            
        } 
    }
    
    public void save(Document doc, String xmlFileName){
        
        try {
            File f = new File(xmlFileName);
             // Use a Transformer for output
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            //transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        }
        catch (Exception ex) {
            System.err.println("XMLParser_Java: save to file error, "+ex.toString());
        } 
    }
}

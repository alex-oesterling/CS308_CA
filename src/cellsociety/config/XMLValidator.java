package cellsociety.config;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

import cellsociety.exceptions.InvalidXMLStructureException;
import org.xml.sax.SAXException;


/**
 * Validates an XML configuration file against a defined XML Schema File
 * Example: The Config class calls the XML validator to validate the XMl structure before parsing.
 * @author Alex Xu, aqx
 */
public class XMLValidator {
    public static final String XSD_SCHEMA_FILEPATH = "src\\cellsociety\\config\\schema_v2.xsd";
    public static final String INVALID_XML_STRUCTURE = "(Invalid XML Config Structure)";

    private XMLValidator(){}

    /**
     * Validates a XML file against the XSD file that is given as part of the program.
     * @param xmlFile the file to be validated
     * @return true if the document structure is valid, false otherwise.
     */
    public static boolean validateXMLStructure(File xmlFile) throws InvalidXMLStructureException{
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(XSD_SCHEMA_FILEPATH));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlFile));
        }catch(IOException | SAXException e){
            throw new InvalidXMLStructureException(e, INVALID_XML_STRUCTURE+e.getMessage());
        }
        return true;
    }
}
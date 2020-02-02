package cellsociety;

import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads an XML file and sets up the Grid.
 * @author Alex Xu aqx
 */
public class Config {
    private String ConfigNodeName = "ConfigInfo";

    private Grid myGrid;

    private File myFile;
    private Document doc;

    private String myTitle;
    private String myAuthor;

    private Map<Integer, Color> myStates;
    private Map<String, Double> myParameters;
    //private String defaultState;

    private int mySpeed;
    private int myWidth;
    private int myHeight;

    /**
     * Constructor for the Config object. Sets the filepath and sets up the documentBuilder.
     * @param xmlFile File object passed in, in XML format
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Config(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
        myFile = xmlFile;
        setupDocument();
        System.out.println("Document Setup Complete");                                                                  //Debugging Purposes Only.
        extractConfigInfo();
        System.out.println("Config Info Load Complete");                                                                     //Debugging Purposes Only.
    }

    /**
     * Create and set up the Grid based on stored information, and then return it.
     * @return
     */
    public Grid loadFile(){
        createGrid();
        return myGrid;
    }

    private void setupDocument() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(myFile);
        doc.getDocumentElement().normalize();                                                                           //optional, might remove later.
    }

    /**
     * Extracts all information in the XML Document that lies within <ConfigInfo>.
     */
    private void extractConfigInfo(){
        NodeList configNodeList = doc.getElementsByTagName(ConfigNodeName);
        Node configNode = configNodeList.item(0);

        if(configNode.getNodeType() == Node.ELEMENT_NODE) {
            Element configElement = (Element) configNode;
            extractTitle(configElement);
            extractAuthor(configElement);
            extractDimensions(configElement);
            extractStates(configElement);
            extractParameters(configElement);
        }
    }

    private void extractTitle(Element startingElement){
        myTitle = startingElement.getElementsByTagName("Title").item(0).getTextContent();
        System.out.println("Simulation Name: "+ myTitle);                                                               //For Debugging Purposes.
    }
    private void extractAuthor(Element startingElement){
        myAuthor = startingElement.getElementsByTagName("Author").item(0).getTextContent();
        System.out.println("Author: "+ myAuthor);
    }

                                                                                                                        //Note to self: VERY SIMILAR CODE TO EXTRACT STATES
    private void extractParameters(Element configElement) {
        myParameters = new HashMap<>();

        Node parametersNode = configElement.getElementsByTagName("SpecialParameters").item(0);
        if(parametersNode.getNodeType() == Node.ELEMENT_NODE){
            Element parametersElement = (Element) parametersNode;

            NodeList parametersNodeList = parametersElement.getElementsByTagName("Parameter");                          //Gets the list of Nodes

            for(int i = 0; i<parametersNodeList.getLength(); i++){
                Node singleParameterNode = parametersNodeList.item(i);
                if(singleParameterNode.getNodeType() == Node.ELEMENT_NODE){
                    Element singleParameterElement = (Element) singleParameterNode;
                    String parameterName = singleParameterElement.getAttribute("name");
                    Double parameterValue = Double.valueOf(singleParameterElement.getTextContent());
                    myParameters.put(parameterName, parameterValue);
                }
            }
        }
        printParameters();
    }

    private void printParameters() {                                                                                    //For Debug Purposes
        System.out.println("All Parameters Set (Debug):");
        for (Map.Entry me : myParameters.entrySet()) {
            System.out.println("Name: "+me.getKey() + " & Value: " + me.getValue());
        }
    }

    private void extractStates(Element startingElement){
        myStates = new HashMap<>();

        Node statesNode = startingElement.getElementsByTagName("States").item(0);
        if(statesNode.getNodeType() == Node.ELEMENT_NODE){
            Element statesElement = (Element) statesNode;

            NodeList statesNodeList = statesElement.getElementsByTagName("State");

            for(int i=0; i<statesNodeList.getLength(); i++) {
                Node singleStateNode = statesNodeList.item(i);
                if (singleStateNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element singleStateElement = (Element) singleStateNode;
                    Integer stateID = Integer.valueOf(singleStateElement.getElementsByTagName("ID").item(0).getTextContent());
                    String stateColor = singleStateElement.getElementsByTagName("Color").item(0).getTextContent();
                    myStates.put(stateID, Color.web(stateColor));
                }
            }
        }
        printStates();
    }

    private void printStates() {
        System.out.println("All States (Debug):");
        for (Map.Entry me : myStates.entrySet()) {
            System.out.println("State: "+me.getKey() + " & Value: " + me.getValue());
        }
    }

    private void extractDimensions(Element startingElement){
        Node dimensionsNode = startingElement.getElementsByTagName("Dimensions").item(0);
        if(dimensionsNode.getNodeType() == Node.ELEMENT_NODE){
            Element dimensionsElement = (Element) dimensionsNode;
            extractHeight(dimensionsElement);
            extractWidth(dimensionsElement);
            extractSpeed(dimensionsElement);

            System.out.println("Height:" + myHeight);                                                                       //Debug
            System.out.println("Width:" + myWidth);                                                                       //Debug
            System.out.println("Speed:" + mySpeed);                                                                       //Debug
        }
    }

    private void extractSpeed(Element dimensionsElement) {
        mySpeed = Integer.parseInt(dimensionsElement.getElementsByTagName("Speed").item(0).getTextContent().trim());        //REFACTOR These 3 later on.
    }

    private void extractWidth(Element dimensionsElement) {
        myWidth = Integer.parseInt(dimensionsElement.getElementsByTagName("Width").item(0).getTextContent().trim());
    }

    private void extractHeight(Element dimensionsElement) {
        myHeight = Integer.parseInt(dimensionsElement.getElementsByTagName("Height").item(0).getTextContent().trim());
    }


    public String getTitle(){
        return myTitle;
    }
    public String getAuthor(){
        return myAuthor;
    }
    public int getWidth(){
        return myWidth;
    }
    public int getHeight(){
        return myHeight;
    }
    public Map<Integer, Color> getStates(){
        return myStates;
    }
    /*
    public String getDefaultState(){
        return defaultState;
    }
    */
    public int getSpeed(){
        return mySpeed;
    }

    //Take file input in
    //private void readFile throws FileNotFoundException(String path){

    //}

    private void createGrid(){
        myGrid = new Grid();
        //setParameters();                        //use set parameter (Cell Class)
        //setStateColors();                       //use set statecolor (Cell Class)
        //setCellStates();                        //use Cell.setState

        myGrid.setRandomGrid(myTitle, myParameters, new double[]{.2,.7,0}, myWidth, myHeight); //Random Grid, for testing purposes.
    }
}
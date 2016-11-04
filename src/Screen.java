import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Screen {
    String screenName;
    String screenElements;
    String command;
    Map<String, Element>  elementsMap = null;
    List<List<String>> routes = null;


    public Screen(String command, String screenElements) throws IOException, SAXException, ParserConfigurationException {
        this.command =command;
        this.screenName = command.substring(command.indexOf("=")+1).replace("'","").replace(":","_").trim();

        this.screenElements = screenElements;

        Document elementsDoc = Utilities.getDocumentFromString(screenElements);
        elementsMap = GetVIPElementsFromDoc(elementsDoc);

        routes = new ArrayList<>();

    }
    public static Map<String, Element> GetVIPElementsFromDoc(Document elementsDoc) {
        Map<String,Element> MAP =new HashMap<>();

        try {
            NodeList nodeList = elementsDoc.getElementsByTagName("node");
            for (int temp = 0; temp < nodeList.getLength(); temp++) {

                Node node = nodeList.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                  //  System.out.println(printElement(element));
                    if(element.getAttribute("class").contains("TextView"))
                        MAP.put("TextView_"+element.getAttribute("id") + " "+temp,element);
                    if(element.getAttribute("class").contains("ImageView"))
                        MAP.put("ImageView_"+element.getAttribute("id") + " "+temp,element);
                    if(element.getAttribute("class").contains("EditText"))
                        MAP.put("EditText_"+element.getAttribute("id") + " "+temp,element);
                    if(element.getAttribute("class").contains("Button"))
                        MAP.put("Button_"+element.getAttribute("id") + " "+temp,element);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MAP;
    }
    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }
    public static String printElement(Element node) throws IOException, TransformerException {
        DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer serializer = lsImpl.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", false); //by default its true, so set it to false to get String without xml-declaration
       return serializer.writeToString(node);
    }


    public void AddRoute(List<String> commandList){
        routes.add(commandList);
    }

    public List<String> getShortestRoute() {
        List<String> shortestRoute = null;
        int minRouteLength = 1000;
        for ( List<String> route : routes) {
            if (route.size()<minRouteLength){
                minRouteLength=route.size();
                shortestRoute = route;
            }

        }
        return shortestRoute;
    }
}

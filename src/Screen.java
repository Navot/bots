import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Screen {
    String screenName;
    String screenElements;
    String command;
    Map<String, Element>  elementsMap = null;
    List<List<String>> rouths = null;

    public Screen(String command, String screenElements) throws IOException, SAXException, ParserConfigurationException {
        this.command =command;
        this.screenName = command.substring(command.indexOf("=")+1).replace("'","").trim();

        this.screenElements =screenElements;

        Document elementsDoc = Utilities.getDocumentFromString(screenElements);
        elementsMap = GetVIPElementsFromDoc(elementsDoc);

        rouths = new ArrayList<>();

    }
    public static Map<String, Element> GetVIPElementsFromDoc(Document elementsDoc) {
        Map<String,Element> MAP =new HashMap<>();

        try {
            NodeList nList = elementsDoc.getElementsByTagName("node");
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if(eElement.getAttribute("class").contains("TextView"))MAP.put("TextView_"+eElement.getAttribute("id") + " "+temp,eElement);
                    if(eElement.getAttribute("class").contains("ImageView"))MAP.put("ImageView_"+eElement.getAttribute("id") + " "+temp,eElement);
                    if(eElement.getAttribute("class").contains("EditText"))MAP.put("EditText_"+eElement.getAttribute("id") + " "+temp,eElement);
                    if(eElement.getAttribute("class").contains("Button"))MAP.put("Button_"+eElement.getAttribute("id") + " "+temp,eElement);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MAP;
    }
}

import com.experitest.client.Client;
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
 * Created by navot on 11/10/2016.
 */
public class RunSetUp {

    public static  Map<String,String> getRunDetails() throws IOException, SAXException, ParserConfigurationException {
        Document propertiesDoc = getDocument(new File("Creds.xml"));

        Map<String,String> MAP = getDetails(propertiesDoc);

        System.out.println("WTF - "+MAP.get("device")+" "+MAP.get("username")+" "+MAP.get("password")+" "+MAP.get("appPath")+" "+MAP.get("appString"));

        return MAP;
    }

    public static Document getDocument(File fileToParse) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document dDoc = builder.parse(fileToParse);
        dDoc.getDocumentElement().normalize();
        return dDoc;
    }

    private static Map<String, String> getDetails(Document dDoc) {
        Map<String,String> MAP =new HashMap<>();
        try {
            NodeList nList = dDoc.getElementsByTagName("Cred");
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    MAP.put("device",eElement.getElementsByTagName("device").item(0).getTextContent());
                    MAP.put("username",eElement.getElementsByTagName("username").item(0).getTextContent());
                    MAP.put("password",eElement.getElementsByTagName("password").item(0).getTextContent());
                    MAP.put("appPath",eElement.getElementsByTagName("appPath").item(0).getTextContent());
                    MAP.put("appString",eElement.getElementsByTagName("appString").item(0).getTextContent());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MAP;
    }



}

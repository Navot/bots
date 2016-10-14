import com.experitest.client.Client;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by navot on 12/10/2016.
 */
public class Utilities {

    public static String MapToString( Map<String,Element> elementsMap) {

        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String,Element>> iter = elementsMap.entrySet().iterator();
        sb.append("------------------------------------------------------------\n");
        while (iter.hasNext()) {
            Map.Entry<String,Element> entry = iter.next();
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue().getAttribute("class")+"; TEXT:"+entry.getValue().getTextContent());
            if (iter.hasNext()) {
                sb.append("\n");
            }
        }
        sb.append("\n------------------------------------------------------------");
        return sb.toString();

    }

    public static Document getDocument(File fileToParse) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document dDoc = builder.parse(fileToParse);
        dDoc.getDocumentElement().normalize();
        return dDoc;
    }

    public static Document getDocumentFromString(String screenElements) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document dDoc = builder.parse(new InputSource(new StringReader(screenElements)));
        dDoc.getDocumentElement().normalize();
        return dDoc;
    }

    public static String GetLastCommandString(Client client){
        Map map = client.getLastCommandResultMap();
        return (String) map.get("logLine");
    }
}

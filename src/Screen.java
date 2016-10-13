import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Screen {
    String screenName;
    String screenElements;
    String command;
    Map<String, Element>  elementsMap = null;
    List<List<Screen>> rouths = null;

    public Screen(String command, String screenElements) throws IOException, SAXException, ParserConfigurationException {
        this.command =command;

        this.screenName = command.substring(command.indexOf("=")+1).replace("'","").trim();

        this.screenElements =screenElements;
        Document elementsDoc = Utilities.getDocumentFromString(screenElements);
        elementsMap = ScreensManager.GetVIPElementsFromDoc(elementsDoc);

        rouths = new ArrayList<>();
        List<Screen> firstRout = new ArrayList<>();
        
        rouths.add(firstRout);
    }
}

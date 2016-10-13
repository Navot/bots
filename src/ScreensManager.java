import com.experitest.client.Client;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by navot on 12/10/2016.
 */
public class ScreensManager {
    private Client client=null;
    private List<Screen> screenList = null;
    private int index = 0;

    public ScreensManager(Client client){
        this.client=client;
        screenList = new ArrayList<>();
    }

    public Screen CheckIfBeenHereBefore(Screen currentScreen) {
        System.out.println("Checking If We Have Been Here Before");
        Screen VisitedScreen=null;
        for (Screen repoScreen : screenList) {
            if (ChangeChecker.CheckDumpChanged(currentScreen,repoScreen))
            {
                VisitedScreen = repoScreen;
            }
        }
        if (VisitedScreen!=null){
            System.out.println("We Were Here Before - It Was Called "+VisitedScreen.screenName);
            return VisitedScreen;
        }
        else{
            System.out.println("We Weren't Here");
            return null;
        }
    }

    public Map<String, Element> AddScreen(Screen currentScreen) throws IOException, SAXException, ParserConfigurationException {

        Document elementsDoc = Utilities.getDocument(AddToRepo(currentScreen));
        Map<String, Element> elementsMap = GetVIPElementsFromDoc(elementsDoc);
        return elementsMap;
    }

    public File AddToRepo(Screen currentScreen)  {
        System.out.println("Writing Files To Repo");
        File file = new File("dumps\\"+currentScreen.screenName+"_"+index+".xml");
        index++;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(currentScreen.screenElements);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done Writing - "+file.getAbsolutePath());
        try {
            Thread.sleep(100);
            String capture = client.capture();
            File repoFile =new File("dumps\\"+currentScreen.screenName+"_"+index+".png");
            FileUtils.copyFile(new File (capture), repoFile);
            System.out.println("Done Writing - "+repoFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return file;
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

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
   
    private static List<Screen> screenList = null;
    private int index = 0;
    private Client client = null;
   
    public ScreensManager(Client client){
        System.out.println("Building a Screen Manager");
        screenList = new ArrayList<>();
        this.client = client;
    }

    public static Screen GetScreenByName(String screenName){
        for (int i = 0; i < screenList.size(); i++) {
            if (screenList.get(i).screenName.equals(screenName)){
                return screenList.get(i);
            }
        }
        return null;
    }



    public boolean AddScreen(Screen currentScreen) throws IOException, SAXException, ParserConfigurationException {

        screenList.add(currentScreen);
        AddToRepo(currentScreen);

        return true;
    }

    public boolean AddToRepo(Screen currentScreen)  {
        System.out.println("Writing Files To Repo");
        File file = new File("C:\\Users\\navot\\IdeaProjects\\bots\\Results\\dumps\\"+currentScreen.screenName+"_"+index+".xml");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(currentScreen.screenElementsString);
            writer.close();
            System.out.println("Done Writing - "+file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            Thread.sleep(200);
            String capture = client.capture();
            File repoFile =new File("C:\\Users\\navot\\IdeaProjects\\bots\\Results\\captures\\"+currentScreen.screenName+"_"+index+".png");
            FileUtils.copyFile(new File (capture), repoFile);
            System.out.println("Done Writing - "+repoFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        index++;
        return true;
    }


    public Screen CheckIfBeenHereBefore(String elementsString) throws IOException, SAXException, ParserConfigurationException {
        System.out.println("Checking If We Have Been Here Before");
        Screen VisitedScreen=null;
        Document elementsDoc = Utilities.getDocumentFromString(elementsString);
        Map elementsMap = GetVIPElementsFromDoc(elementsDoc);
        for (Screen repoScreen : screenList) {
            if (!ChangeChecker.IsDumpDifferentByElementsMap(elementsMap,repoScreen.screenName))
            {
                VisitedScreen = repoScreen;
                System.out.println("We Were Here Before - It Was Called "+VisitedScreen.screenName);
                return VisitedScreen;
            }
        }

        System.out.println("We Weren't Here");
        return null;
    }
    public static Map<String, Element> GetVIPElementsFromDoc(Document elementsDoc) {
        Map<String,Element> MAP =new HashMap<>();

        try {
            NodeList nodeList = elementsDoc.getElementsByTagName("node");
            for (int index = 0; index < nodeList.getLength(); index++) {

                Node node = nodeList.item(index);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    //  System.out.println(printElement(element));
                    if(element.getAttribute("class").contains("ListView"))
                        MAP.put("ListView_"+element.getAttribute("id") + " "+index,element);
                    if(element.getAttribute("class").contains("TextView"))
                        MAP.put("TextView_"+element.getAttribute("id") + " "+index,element);
                    if(element.getAttribute("class").contains("ImageView"))
                        MAP.put("ImageView_"+element.getAttribute("id") + " "+index,element);
                    if(element.getAttribute("class").contains("EditText"))
                        MAP.put("EditText_"+element.getAttribute("id") + " "+index,element);
                    if(element.getAttribute("class").contains("Button"))
                        MAP.put("Button_"+element.getAttribute("id") + " "+index,element);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MAP;
    }
}

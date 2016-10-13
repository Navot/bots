import com.experitest.client.Client;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

/**
 * Created by navot on 11/10/2016.
 */
public class BotObject {
    Screen lastScreen =null;
    List<Screen> screensList = null;
    Client client = null;
    String command =null;
    Clicker clicker = null;
    ChangeChecker CC = null;
    ScreensManager SM =null;
    TestFactory TF = null;

    public BotObject(String command, Client client, String appNameFromDevice) throws IOException, SAXException, ParserConfigurationException {

        this.command =command;
        this.client=client;
        screensList = new ArrayList<>();
        SM = new ScreensManager(client);
        clicker = new Clicker(client);
        CC = new ChangeChecker(client,appNameFromDevice);
        TF = new TestFactory();


    }

    public boolean BotRun(String command) throws ParserConfigurationException, SAXException, IOException {
        //client.syncElements(1000,10000);
        Screen currentScreen = new Screen(command,CC.GetElements());
        System.out.println("Checking if the Dump Changed After - " + currentScreen.screenName);
        boolean dumpChangeFlag = CC.CheckDumpChanged(currentScreen,lastScreen);
        if (dumpChangeFlag) {
            if (!CC.StillInApp()) return true;

            Screen VisitedScreen = SM.CheckIfBeenHereBefore(currentScreen);

            if (VisitedScreen==null) {
                System.out.println("Adding a new Screen: "+currentScreen.screenName);
                SM.AddScreen(currentScreen);
                TF.CreateFunctionalTest(currentScreen,lastScreen);
                TF.CreateLayoutTest(currentScreen);
                lastScreen = currentScreen;

                System.out.println("All Elements On the Screen:\n"+Utilities.MapToString(currentScreen.elementsMap));

                StartWorkingOnElementsMap(currentScreen);

                return true;
            } else {
                System.out.println("VisitedScreen - " + VisitedScreen);
                SM.AddScreen(currentScreen);
                return true;
            }
        } else {
            System.out.println("This Action - " + command + "  Apparently (!!!) Does Nothing");
            return false;
        }
    }

    private void StartWorkingOnElementsMap(Screen currentScreen) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("Trying To click in the new screen");
        Iterator<Map.Entry<String,Element>> iter = currentScreen.elementsMap.entrySet().iterator();
        Map<String,String> clickResult = new HashMap<>();

        while (iter.hasNext()) {
            System.out.println("Get Ready For A Click!");
            Map.Entry<String, Element> UIElement = iter.next();
            if(!UIElement.getValue().getAttribute("x").contains("-")){
                System.out.println("IT'S A - "+UIElement.getKey().toString());
                clickResult = clicker.ClickingOnElement(UIElement);
            }
            if(clickResult.get("result").equals("true")){
                boolean actionStatus = BotRun(clickResult.get("command"));
                if (!actionStatus){
                    System.out.println("Still On Screen: "+currentScreen.screenName);
                }
                else{
                    if (Navigate(currentScreen)){
                        System.out.println("Got Back To: "+currentScreen.screenName);
                    }
                    else{
                        System.out.println("Can't Get Back To: "+ currentScreen.screenName);
                    }
                }
            }
            else{
                System.out.println(UIElement.getKey()+" - DOES NOTHING?");
            }
        }

    }

    private boolean Navigate(Screen currentScreen) {

        return false;
    }


}

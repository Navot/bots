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

    public String BotRun(String command) throws ParserConfigurationException, SAXException, IOException {

        client.syncElements(1000,10000);
        if (!CC.StillInApp()) return "WE ARE OUT!!";

        Screen currentScreen = new Screen(command,CC.GetElements());
        System.out.println("Checking if the Dump Changed After - " + currentScreen.screenName);
        boolean dumpChangeFlag = CC.CheckDumpChanged(currentScreen,lastScreen);
        if (dumpChangeFlag) {

            Screen VisitedScreen = SM.CheckIfBeenHereBefore(currentScreen);

            if (VisitedScreen==null) {
                System.out.println("Adding a new Screen: "+currentScreen.screenName);
                SM.AddScreen(currentScreen);
                TF.CreateFunctionalTest(currentScreen,lastScreen);
                TF.CreateLayoutTest(currentScreen);
                lastScreen = currentScreen;

                System.out.println("All Elements On the Screen:\n"+Utilities.MapToString(currentScreen.elementsMap));

                StartWorkingOnElementsMap(currentScreen.elementsMap);

                return "-> ";
            } else {
                System.out.println("VisitedScreen - " + VisitedScreen);
                SM.AddScreen(currentScreen);
                return "-> ";
            }
        } else {
            System.out.println("This Action - " + command + "  Apparently (!!!) Does Nothing");
            return "-> This Action - " + command + "  Apparently (!!!) Does Nothing\n";
        }
    }

    private void StartWorkingOnElementsMap(Map<String, Element> elementsMap) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("Trying To click in the new screen");
        Iterator<Map.Entry<String,Element>> iter = elementsMap.entrySet().iterator();
        boolean wasClicked=false;

        while (iter.hasNext()) {
            Map.Entry<String, Element> UIElement = iter.next();
            if (UIElement.getKey().contains("Button")& !UIElement.getValue().getAttribute("x").contains("-")){
                System.out.println("IT'S A BUTTON - "+UIElement.getKey().toString());
                wasClicked = clicker.ClickingOnElementWithProperty(UIElement);
            }
            else if (UIElement.getKey().contains("TextView") & !UIElement.getValue().getAttribute("x").contains("-")) {
                System.out.println("IT'S A TextView - " + UIElement.getKey().toString());
                wasClicked = clicker.ClickingOnElementWithProperty(UIElement);
            }


            if(wasClicked){
                BotRun(Utilities.GetLastCommandString(client));
            }
            else{
                System.out.println(UIElement.getKey()+" - DOES NOTHING?");
            }
        }

    }






}

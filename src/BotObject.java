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

    Clicker clicker = null;
    ChangeChecker CC = null;
    ScreensManager SM =null;
    TestFactory TF = null;



    public BotObject(Client client, String appNameFromDevice) throws IOException, SAXException, ParserConfigurationException {
        this.client=client;
        screensList = new ArrayList<>();
        SM = new ScreensManager(client);
        clicker = new Clicker(client);
        CC = new ChangeChecker(client,appNameFromDevice);
        TF = new TestFactory();


    }

    public String BotRun(List<String> commandList) throws ParserConfigurationException, SAXException, IOException {

        System.out.println("Checking if the Dump Changed After - " + commandList.get(commandList.size()-1));
        Screen currentScreen = new Screen(commandList.get(commandList.size()-1),CC.GetElements());

        boolean dumpChangeFlag = CC.IsDumpDifferent(currentScreen,lastScreen);

        if (!dumpChangeFlag){
            System.out.println("This Action - " + commandList.get(commandList.size()-1) + "  Apparently (!!!) Does Nothing");
            return "nothing";
        }
        if (!CC.StillInApp()) return "out";

        Screen VisitedScreen = SM.CheckIfBeenHereBefore(currentScreen);
        if (VisitedScreen==null) {
            System.out.println("Adding a new Screen: "+currentScreen.screenName);
            SM.AddScreen(currentScreen);
            currentScreen.AddRoute(commandList);
            TF.CreatePathTest(currentScreen);
            TF.CreateLayoutTest(currentScreen);
            lastScreen = currentScreen;

            System.out.println("All Elements On the Screen:\n"+Utilities.MapToString(currentScreen.elementsMap));
            StartWorkingOnElementsMap(currentScreen);
            return "done";
        } else {
            System.out.println("VisitedScreen - " + VisitedScreen.screenName);
            VisitedScreen.AddRoute(commandList);
            TF.CreatePathTest(VisitedScreen);
            return "visited";
        }

    }

    private void StartWorkingOnElementsMap(Screen currentScreen) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("Trying To click in the new screen");
        Iterator<Map.Entry<String,Element>> iter = currentScreen.elementsMap.entrySet().iterator();
        Map<String,String> clickResult = new HashMap<>();

        while (iter.hasNext()) {
            System.out.println("Get Ready For A Click!");
            Map.Entry<String, Element> UIElement = iter.next();
            if(!UIElement.getValue().getAttribute("x").contains("-")&& !(Integer.parseInt(UIElement.getValue().getAttribute("y"))>1920)){
                System.out.println("IT'S A - "+UIElement.getKey().toString());
                clickResult = clicker.ClickingOnElement(UIElement);
            }
            if(clickResult.get("result").equals("true")){
                System.out.println("Click Was OK on " + clickResult.get("step"));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<String> commandsList = new ArrayList (currentScreen.routes.get(currentScreen.routes.size()-1));
                commandsList.add(clickResult.get("step"));
                String botResult = BotRun(commandsList);
                switch (botResult){
                    case "nothing":
                        System.out.println("Still On Screen: "+currentScreen.screenName);
                        break;
                    case "out":
                        System.out.println("Got Out Of The App!! -> Getting Back");
                        Navigate(currentScreen);
                        break;
                    case "done":
                        System.out.println("Done Here!!");
                        Navigate(currentScreen);
                        break;
                    case "visited":
                        System.out.println("Been Here!! -> Getting Back");
                        Navigate(currentScreen);
                        break;
                }
            }
            else{
                System.out.println("Click Was NOT GOOD on " + clickResult.get("step"));
            }
        }

    }

    private boolean Navigate(Screen currentScreen) {
      /*  for (List list : currentScreen.routes) {
            System.out.println("Printing Routs: ");
            System.out.println();
            for (int i = 0; i < list.size(); i++) {
                System.out.print(list.get(i)+"->");
            }
            System.out.println();
        }*/
        System.out.println(currentScreen.routes);
        List<String> commandList = currentScreen.getShortestRoute();
        for (int i = 0; i < commandList.size(); i++) {
            Perform(commandList.get(i));
        }
        lastScreen=currentScreen;
        return false;
    }

    private void Perform(String command) {
        if (command.contains("Launching")){
            client.launch(Start.getMAP().get("appString"),false,true);
            client.syncElements(2000,20000);
        }
        if (command.contains("ClickingOnElement")){
            client.click("NATIVE",command.substring(command.indexOf('-')+2),0,1);
            client.syncElements(2000,20000);
        }
    }


}

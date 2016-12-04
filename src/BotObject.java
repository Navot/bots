import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

/**
 * Created by navot on 11/10/2016.
 */
public class BotObject {



    public String BotRun(List<String> route, String lastScreenName) throws ParserConfigurationException, SAXException, IOException  {
        System.out.println("Starting A New Potential Screen");
        if (!Runner.CC.StillInApp()) return "out";

        Screen currentScreen = new Screen(route.get(route.size()-1), Runner.CC.GetElements());

        if (!currentScreen.screenName.contains("Text")) {
            boolean dumpChangeFlag = Runner.CC.IsDumpDifferent(currentScreen, Runner.lastScreen);

            if (!dumpChangeFlag){
                System.out.println("This Action - " + route.get(route.size()-1) + "  Apparently (!!!) Does Nothing");
                return "nothing";
            }


            Screen VisitedScreen = Runner.SM.CheckIfBeenHereBefore(currentScreen);
            if (VisitedScreen!=null) {
                System.out.println("Visited Screen - " + VisitedScreen.screenName);
                VisitedScreen.AddRoute(route);
                Runner.TF.CreatePathTest(VisitedScreen);
                return "visited";
            }


    }
        System.out.println("Changing LAST SCREE to - "+ lastScreenName);
        Runner.lastScreen = lastScreenName;
        NewScreenProtocol(route, currentScreen);
        StartWorkingOnElementsMap(currentScreen);
        return "done";

    }

    private boolean OverWorkedTheElement(List<String> routeList, String[] identifier) {


        System.out.println(routeList.toString());
        try{
            System.out.println("element!!!!!! - "+identifier[0]+" = "+identifier[1]);
        }catch (NullPointerException e){
            System.out.println("NullPointerException");
            e.printStackTrace();
            return false;
        }
        if (routeList.size()>=2) {
            for (String route : routeList) {
                if(route.contains(identifier[1])){
                    System.out.println("We OverWorked The Element");
                    System.out.println("Continuing To Next Element..");
                    return true;
                }
            }
        }
        System.out.println("We DID NOT OverWorked The Element");
        return false;
    }

    private void NewScreenProtocol(List<String> route, Screen currentScreen) throws IOException, SAXException, ParserConfigurationException {
        System.out.println("Adding a new Screen: "+currentScreen.screenName);
        Runner.SM.AddScreen(currentScreen);
        currentScreen.AddRoute(route);
        Runner.TF.CreatePathTest(currentScreen);
        Runner.TF.CreateLayoutTest(currentScreen);

        System.out.println("All Elements On the Screen:\n"+Utilities.MapToString(currentScreen.elementsMap));

    }

    private void StartWorkingOnElementsMap(Screen currentScreen) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("Trying To Work The New Screen");
        Iterator<Map.Entry<String,Element>> iterator = currentScreen.elementsMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String,Element> entry = iterator.next();
            if (OverWorkedTheElement(currentScreen.liveRoute, Runner.worker.GetIdentifier(entry))) continue;

            Map<String,String> ActionResult = Runner.worker.WorkTheElement(entry);

            if(ActionResult.get("result").equals("true")){
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<String> route = BuildRouteForNewBot(currentScreen, ActionResult);
                String botResult = BotRun(route,currentScreen.screenName);
                ReadResult(currentScreen, botResult);
            }
            else{
                System.out.println("Continuing");
            }
        }

    }

    private void ReadResult(Screen currentScreen, String botResult) {
        System.out.println("Reading Result - "+botResult);
        switch (botResult){
            case "nothing":
                System.out.println("Still On Screen: "+currentScreen.screenName);
                break;
            case "out":
                System.out.println("Got Out Of The App!! -> Getting Back");
                Runner.NGR.Navigate(currentScreen);
                break;
            case "done":
                System.out.println("Done Here!!");
                Runner.NGR.Navigate(ScreensManager.GetScreenByName(Runner.lastScreen));
                break;
            case "visited":
                System.out.println("Been Here!! -> Getting Back");
                Runner.NGR.Navigate(currentScreen);
                break;
            case "over":
                System.out.println("Over Worked The Element -> Stopping");
                //NGR.Navigate(currentScreen);
                break;
        }
    }

    private List<String> BuildRouteForNewBot(Screen currentScreen, Map<String, String> actionResult) {
        List<String> commandsList = new ArrayList(currentScreen.routes.get(currentScreen.routes.size()-1));
        commandsList.add(actionResult.get("step"));
        return commandsList;
    }

}

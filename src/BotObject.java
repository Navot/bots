import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

/**
 * Created by navot on 11/10/2016.
 */
public class BotObject {
    Screen VisitedScreen=null;

    public void BotRun(List<String> route, String elementsString) throws ParserConfigurationException, SAXException, IOException  {
        System.out.println("Starting A New Potential Screen - "+route.get(route.size()-1));
        Screen currentScreen = new Screen(route,elementsString);
        StartWorkingOnElementsMap(currentScreen);
        System.out.println("Done Here!!");
        Runner.NGR.Navigate(ScreensManager.GetScreenByName(Runner.lastScreen), currentScreen);
    }

    private void StartWorkingOnElementsMap(Screen currentScreen) throws ParserConfigurationException, SAXException, IOException {

        System.out.println("Trying To Work The New Screen");
        Iterator<Map.Entry<String,Element>> iterator = currentScreen.elementsMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String,Element> entry = iterator.next();
            if (Runner.worker.GetIdentifier(entry)!= null && OverWorkedTheElement(currentScreen.liveRoute, Runner.worker.GetIdentifier(entry))) continue;

            Map<String,String> ActionResult = Runner.worker.WorkTheElement(entry);

            if(ActionResult.get("result").equals("true")){
                Utilities.Sleep(500);
                if (Runner.CC.StillInApp()) {
                    String elementsString =Runner.CC.GetElements();
                    if (Runner.CC.IsDumpDifferentByElementsString(elementsString, Runner.lastScreen)){
                        System.out.println("Changing LAST SCREEN to - "+ currentScreen.screenName);
                        Runner.lastScreen = currentScreen.screenName;
                        List<String> route = BuildRouteForNewBot(currentScreen, ActionResult);

                        VisitedScreen = Runner.SM.CheckIfBeenHereBefore(elementsString);

                        if (VisitedScreen==null) {
                            BotRun(route,elementsString);
                        }else{
                            VisitedScreenProtocol(route);
                            Runner.NGR.Navigate(currentScreen, VisitedScreen);
                            VisitedScreen=null;
                        }
                    }else{
                        System.out.println("This Action - " + ActionResult.get("step") + "  Apparently (!!!) Does Nothing");
                    }
                }else{
                    System.out.println("Got Out Of The App!! -> Getting Back");
                    Runner.NGR.Navigate(currentScreen, null);
                }
            }
            else{
                System.out.println("Could Not Work The Element -> Continuing To Next Element");
            }
        }

    }

    private void VisitedScreenProtocol(List<String> route) {
        System.out.println("Visited Screen - " + VisitedScreen.screenName);
        System.out.println("Adding Route - "+route);
        VisitedScreen.AddRoute(route);
        Runner.TF.CreatePathTest(VisitedScreen);
    }

    private boolean OverWorkedTheElement(List<String> liveRoute, String[] identifier) {

        try{
            System.out.println("element!!!!!! - "+identifier[0]+" = "+identifier[1]);
        }catch (NullPointerException e){
            System.out.println("No identifier found - NullPointerException");
            e.printStackTrace();
            return false;
        }
        if (liveRoute.size()>=2) {
            for (String route : liveRoute) {
                if(route.contains(identifier[1])){
                    System.out.println("We OverWorked The Element");
                    System.out.println("Live Route - "+liveRoute.toString());
                    System.out.println("Continuing To Next Element..");
                    return true;
                }
            }
        }
        //System.out.println("We DID NOT OverWorked The Element");
        return false;
    }

    private List<String> BuildRouteForNewBot(Screen currentScreen, Map<String, String> actionResult) {
        List<String> commandsList = new ArrayList(currentScreen.allRoutes.get(currentScreen.allRoutes.size()-1));
        commandsList.add(actionResult.get("step"));
        return commandsList;
    }

}

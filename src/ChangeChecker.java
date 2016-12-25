
import com.experitest.client.Client;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by navot on 12/10/2016.
 */
public class ChangeChecker {
  

    private String appNameFromDevice=null;
    private Client client = null;
    public ChangeChecker(Client client, String appNameFromDevice){
        System.out.println("Building a Change Checker");
        this.appNameFromDevice =appNameFromDevice;
        this.client = client;
    }

    public static boolean IsDumpDifferent(Screen currentScreen, String lastScreen) {
        if (lastScreen != "")
        {
            //double result = getWeight(lastScreen.screenElementsString, currentScreen.screenElementsString);
            double result = getSecondWeight(currentScreen.elementsMap,ScreensManager.GetScreenByName(lastScreen).elementsMap);
            System.out.println("The Distance From "+currentScreen.screenName+" To Previous - " + ScreensManager.GetScreenByName(lastScreen).screenName+ "  Is: " + result );
            if (result > 0.97)
                return false;
            else {
                return true;
            }
        }else {
            System.out.println("We have no previous screen!");
            return true;
        }
    }

    private static double getSecondWeight(Map<String, Element> currentScreenElementsMap, Map<String, Element> oldScreenElementsMap) {

        int strike=0;

        for (Map.Entry<String, Element> currentElementEntry : currentScreenElementsMap.entrySet()){
            for (Map.Entry<String, Element> lastScreenEntry : oldScreenElementsMap.entrySet()){

                NamedNodeMap currentEntryAttributes = currentElementEntry.getValue().getAttributes();
                int count=0;
                for (int i = 0; i < currentEntryAttributes.getLength(); i++) {
                    Attr attr = (Attr) currentEntryAttributes.item(i);
                    String attrName = attr.getNodeName();
                    String attrValue = attr.getNodeValue();
                    //System.out.println("Found attribute: " + attrName + " with value: " + attrValue);
                    try{
                    if (attrValue.equals(lastScreenEntry.getValue().getAttribute(attrName))){
                      count++;
                    }else{
                        //System.out.println("Difference" - );
                    }
                    }catch(Exception e){

                    }
                }
                if (count/currentEntryAttributes.getLength()>0.99 && (currentEntryAttributes.getLength()==lastScreenEntry.getValue().getAttributes().getLength())){
                    strike++;
                }
            }

        }
        int max = (oldScreenElementsMap.size()>currentScreenElementsMap.size()) ? oldScreenElementsMap.size() :currentScreenElementsMap.size();

        double result = (double)strike/(double)max;
        return result;
    }

    public String GetElements() {

        return client.getVisualDump("Native");
    }

    public static double getWeight(String str1, String str2) {
        int maxLen = Math.max(str1.length(), str2.length());
        if (maxLen == 0) {
            return 1.0;
        } else {
            final int levenshteinDistance = StringUtils.getLevenshteinDistance(str1, str2);
            return 1.0 - ((double) levenshteinDistance / maxLen);
        }
    }

    public boolean StillInApp() {
        if (client.getCurrentApplicationName().equals(appNameFromDevice)){
            return true;
        }
        else return false;

    }

    public static boolean IsDumpDifferentByElementsMap(Map elementsMap, String repoScreenName) {
            //double result = getWeight(lastScreen.screenElementsString, currentScreen.screenElementsString);
            double result = getSecondWeight(elementsMap,ScreensManager.GetScreenByName(repoScreenName).elementsMap);
            System.out.println("The Distance From currentScreen To Previous - " + repoScreenName+ "  Is: " + result );
            if (result > 0.90)
                return false;
            else {
                return true;
            }

    }
    public static boolean IsDumpDifferentByElementsString(String elementsString, String repoScreenName) throws IOException, SAXException, ParserConfigurationException {
        //double result = getWeight(lastScreen.screenElementsString, currentScreen.screenElementsString);
        if (!repoScreenName.equals("")) {
            Document elementsDoc = Utilities.getDocumentFromString(elementsString);
            double result = getSecondWeight(ScreensManager.GetVIPElementsFromDoc(elementsDoc),ScreensManager.GetScreenByName(repoScreenName).elementsMap);
            System.out.println("The Distance From currentScreen To Previous - " + repoScreenName+ "  Is: " + result );
            if (result > 0.99)
                return false;
            else {
                return true;
            }
        } else {
            return true;
        }

    }
}

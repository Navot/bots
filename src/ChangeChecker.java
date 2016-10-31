import com.experitest.client.Client;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
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
    private Client client=null;

    private String appNameFromDevice=null;

    public ChangeChecker(Client client, String appNameFromDevice){
        this.client=client;
        this.appNameFromDevice =appNameFromDevice;
    }

    public static boolean IsDumpDifferent(Screen currentScreen, Screen lastScreen) {
        if (lastScreen != null)
        {
            //double result = getWeight(lastScreen.screenElements, currentScreen.screenElements);
            double result = getSecondWeight(lastScreen.elementsMap, currentScreen.elementsMap);
            System.out.println("The Distance From "+currentScreen.screenName+" To Previous - " + lastScreen.screenName+ "  Is: " + result );
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

    private static double getSecondWeight(Map<String, Element> lastScreenElementsMap, Map<String, Element> currentScreenElementsMap) {

        int strike=0;

        for (Map.Entry<String, Element> currentEntry : currentScreenElementsMap.entrySet()){
            for (Map.Entry<String, Element> lastScreenEntry : lastScreenElementsMap.entrySet()){

                NamedNodeMap currentEntryAttributes = currentEntry.getValue().getAttributes();
                int count=0;
                for (int i = 0; i < currentEntryAttributes.getLength(); i++) {
                    Attr attr = (Attr) currentEntryAttributes.item(i);
                    String attrName = attr.getNodeName();
                    String attrValue = attr.getNodeValue();
                    //System.out.println("Found attribute: " + attrName + " with value: " + attrValue);
                    if (attrValue.equals(lastScreenEntry.getValue().getAttribute(attrName))){
                      count++;
                    }
                }
                if (count/currentEntryAttributes.getLength()>0.9){
                    strike++;
                    break;
                }
            }

        }
        int max = (currentScreenElementsMap.size()>lastScreenElementsMap.size()) ? currentScreenElementsMap.size() :lastScreenElementsMap.size();

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
}

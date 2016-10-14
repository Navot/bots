import com.experitest.client.Client;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

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

    public static boolean CheckDumpChanged(Screen currentScreen,Screen lastScreen) {
        if (lastScreen != null)
        {
            double result = getWeight(lastScreen.screenElements, currentScreen.screenElements);
            System.out.println("The Distance From "+currentScreen.screenName+" To Previous - " + lastScreen.screenName+ "  Is: " + result );
            if (result > 0.95)
                return false;
            else {
                return true;
            }
        }else {
            System.out.println("We have no previous screen!");
            return true;
        }
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

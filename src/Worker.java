import com.experitest.client.Client;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by navot on 12/10/2016.
 */
public class Worker {
    private Client client = null;
    
    public Worker(Client client){
        System.out.println("Building a Worker");
        this.client = client;
    }

    public Map<String, String> WorkTheElement(Map.Entry<String, Element> next) throws ParserConfigurationException, SAXException, IOException {
        //System.out.println("We Have An Element!!");
        Map.Entry<String, Element> UIElement = next;
        if(UIElement.getValue().getAttribute("top").contains("true") && !UIElement.getValue().getAttribute("x").contains("-")&& !(Integer.parseInt(UIElement.getValue().getAttribute("y"))>1920)){
            String elementKey = UIElement.getKey().toString();
            System.out.println("IT'S A - " + elementKey);
            if (elementKey.startsWith("Button")){
                System.out.println("Get Ready For A Click!");
                return ClickingOnElement(UIElement);
            }else{
                if (elementKey.startsWith("EditText")){
                    System.out.println("Get Ready To Send Text!");
                    return SendTextToElement(UIElement);
                }else {
                    System.out.println("Doing Nothing With This Element");

                }
            }
        }
        Map<String, String> nullResultMap = new HashMap<>();
        nullResultMap.put("result","false");
        return nullResultMap;
    }


    public Map<String, String> ClickingOnElement(Map.Entry<String, Element> entry) throws IOException, ParserConfigurationException, SAXException {
        Map<String, String> resultMap = new HashMap<>();
        String[] identifier = GetIdentifier(entry);
        if (identifier!= null){
            System.out.println("Found a good identifier -" + identifier[0] +" = "+identifier[1]);
            if(Click(identifier)){
                resultMap.put("result","true");
                resultMap.put("step","Click on - "+identifier[0] +" = "+identifier[1]);
                return resultMap;
            }
        }
        resultMap.put("result","false");
        resultMap.put("step",null);
        return resultMap;

    }

    private boolean Click(String[] identifier) {
        try {
            client.click("Native", "//*[@"+identifier[0]+"='"+identifier[1]+"']", 0, 1);
            System.out.println("GOOD - Click on - "+identifier[0]+"="+identifier[1]);
            return true;

        } catch (Exception e) {
            System.out.println("BAD - Could not SClick on - "+identifier[0]+"="+identifier[1]);
            System.out.println("Returning FALSE");
            return false;
        }
    }

    public Map<String, String> SendTextToElement(Map.Entry<String, Element> entry) {
        Map<String, String> resultMap = new HashMap<>();
        String[] identifier = GetIdentifier(entry);
        if (identifier!= null){
            System.out.println("Found a good identifier -" + identifier[0] +" = "+identifier[1]);
            if(SendText(identifier)){
                resultMap.put("result","true");
                resultMap.put("step","SendText To - "+identifier[0] +" = "+identifier[1]);
                return resultMap;
            }
        }
        resultMap.put("result","false");
        resultMap.put("step",null);
        return resultMap;
    }

    private boolean SendText(String[] identifier) {
        try {
            client.click("Native", "//*[@"+identifier[0]+"='"+identifier[1]+"']", 0, 1);
            client.sendText("company");
            client.closeKeyboard();
            System.out.println("GOOD - SendText to - "+identifier[0]+"="+identifier[1]);
            return true;

        } catch (Exception e) {
            System.out.println("BAD - Could not SendText to - "+identifier[0]+"="+identifier[1]);
            System.out.println("Returning FALSE");
            return false;
        }
    }

    public String[] GetIdentifier(Map.Entry<String, Element> entry) {
        String[] result = null;
        NamedNodeMap Attributes = entry.getValue().getAttributes();
        for (int i = 0; i < Attributes.getLength(); i++) {
            Attr attr = (Attr) Attributes.item(i);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            if (attrName.equals("id")){
                result = new String[]{attrName, attrValue};
                break;
            }
            else if (attrName.equals("contentDescription")){
                result = new String[]{attrName, attrValue};
                break;
            }
            else if (attrName.equals("text")){
                result = new String[]{attrName, attrValue};
                break;
            }
        }
        return result;
    }

    public void deviceAction(String command) {
        client.deviceAction(command);
    }
}

import org.apache.commons.lang3.StringUtils;

/**
 * Created by navot on 11/10/2016.
 */
public class play {
    public static void main(String[] args) {
        String s = "Click 'xpath=//*[@contentDescription='All apps']' in zone NATIVE, index: 0, click count: 1";
        String fileName="";
        //fileName = s.substring(0,s.indexOf(" "));
        if (s.contains("='")) {
            System.out.println("contains");
            fileName=s.substring(s.indexOf("='",1)+2,s.indexOf("']",1));
        }
        System.out.println("s: "+fileName);

        //System.out.println(getWeight("aaaxfdsgfsbvbdgbdfhbfsbg","aaxx"));
    }
    public static double getWeight(String str1, String str2) {
        // get the max possible levenstein distance score for string
        int maxLen = Math.max(str1.length(), str2.length());

        // check for 0 maxLen
        if (maxLen == 0) {
            return 1.0; // as both strings identically zero length
        } else {
            final int levenshteinDistance = StringUtils.getLevenshteinDistance(str1, str2);
            // return actual / possible levenstein distance to get 0-1 range
            return 1.0 - ((double) levenshteinDistance / maxLen);
        }
    }
}

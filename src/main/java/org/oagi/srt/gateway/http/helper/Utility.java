package org.oagi.srt.gateway.http.helper;

import java.util.Arrays;
import java.util.List;

public class Utility {

    public static String spaceSeparator(String str) {//Assume that we only take into account TypeName
        if (str.equals("mimeCode")) {
            return "MIME Code";
        }
        if (str.equals("uri")) {
            return "URI";
        }

        String result = sparcing(str);
        return result;
    }

    private static final List<String> ABBR_LIST = Arrays.asList("BOM", "UOM", "WIP", "RFQ", "BOD", "IST", "MSDS");

    private static String sparcing(String str) {
        for (String abbr : ABBR_LIST) {
            if (str.contains(abbr)) {
                str = str.replace(abbr, abbr + " ");
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (Character.isUpperCase(str.charAt(i)) && i != 0) {
                if (Character.isUpperCase(str.charAt(i - 1)))
                    if (i < str.length() - 1 && Character.isLowerCase(str.charAt(i + 1)) && (str.charAt(i) != 'D' && str.charAt(i - 1) != 'I'))
                        sb.append(" " + str.charAt(i));
                    else
                        sb.append(str.charAt(i));
                else
                    sb.append(" " + str.charAt(i));
            } else if (Character.isLowerCase(str.charAt(i)) && i == 0) {
                sb.append(String.valueOf(str.charAt(i)).toUpperCase());
            } else {
                sb.append(str.charAt(i));
            }
        }

        String result = sb.toString();
        // #Issue 435: Exceptional Case
        result = result.replace("E Mail", "EMail")
                .replace("Co A", "CoA")
                .replace("GLDestination", "GL Destination");

        if (result.endsWith(" Code Type"))
            result = result.substring(0, result.indexOf((" Code Type"))).concat(" Code Type");
        result = result.replaceAll("\\s{2,}", " ");
        result = IDtoIdentifier(result);
        result = processBODs(result);
        return result.trim();
    }

    public static String IDtoIdentifier(String space_separated_str) {
        String[] delim = space_separated_str.split(" ");
        String ret = "";
        for (int i = 0; i < delim.length; i++) {
            if (delim[i].startsWith("ID")) {
                delim[i] = delim[i].replace("ID", "Identifier");
            }
            ret = ret + " " + delim[i];
        }
        return ret.trim();
    }

    public static String processBODs(String str) {
        if (str != null) {
            str = str.replace("BOD s", "BODs");
        }
        return str;
    }
}

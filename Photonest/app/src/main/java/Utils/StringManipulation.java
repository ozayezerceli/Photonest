package Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringManipulation {
    public static String expandUsername(String username){
        return username.replace(".", " ");
    }

    public static String condenseUsername(String username){
        return username.replace(" " , ".");
    }

    public static List<String> getHashTags(String string){
        List<String> lstTag = new ArrayList<>();
        String[] words = string.split(" ");
        for(String word:words){
            if(word.substring(0,1).equals("#")){
                lstTag.add(word.substring(1));
            }
        }
        return lstTag;
    }
}

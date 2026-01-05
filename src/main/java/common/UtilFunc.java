package common;

public class UtilFunc {
    public static String getRestStr(String wholeStr, String splitParam, int idx){
        if(wholeStr == null || splitParam == null || wholeStr.isBlank() || splitParam.isBlank()) return "";
        String[] strs = wholeStr.split(splitParam);
        if(strs.length <= idx) return "";

        StringBuilder sb = new StringBuilder();
        for(int i=idx;i< strs.length;i++){
            sb.append(strs[i]);
            if(i+1< strs.length){
                sb.append(splitParam);
            }
        }
        return sb.toString();
    }
}

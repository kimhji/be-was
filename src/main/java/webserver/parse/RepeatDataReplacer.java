package webserver.parse;

import common.Config;
import common.Utils;

import java.util.Collection;

public class RepeatDataReplacer {
    String replacerName;
    String format;
    String noDataFormat;

    public RepeatDataReplacer(String replacerName, String format, String noDataFormat) {
        this.replacerName = replacerName;
        this.format = format;
        this.noDataFormat = noDataFormat;
    }

    public String repeatReplace(Collection<?> objects, String template){
        StringBuilder dataInputs = new StringBuilder();
        StringBuilder tns;
        StringBuilder result = new StringBuilder(template);
        DataReplacer dataReplacer = new DataReplacer(replacerName);
        for(Object object: objects){
            tns = new StringBuilder(format);
            Utils.replaceAll(tns, Config.REPEAT_FORMAT_PLACEHOLDER, replacerName);
            dataInputs.append(dataReplacer.replace(object, tns.toString()));
        }
        Utils.replaceAll(result, "{{" + this.replacerName + "}}", !objects.isEmpty() ?dataInputs.toString():noDataFormat);
        return result.toString();
    }
}

package com.fr.plugin.takeup.utils;

import java.util.List;

/**
 * @author lidongy
 * @version 10.0
 * Created by lidongy on 2019/11/18
 */
public class TakeupDetectionMessageUtils {
    public static String buildTakeupMessage(List<String> machineCodeList) {
        String messageContent = "检测到Finedb占用，机器码为：";
        for(String machineCode : machineCodeList) {
            messageContent += "[";
            messageContent += machineCode;
            messageContent += "];";
        }
        messageContent += "请及时调整。";
        return messageContent;
    }
}

package com.learning.section6.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;

@Component
public class TimeTools {

    Logger logger = LoggerFactory.getLogger(TimeTools.class);

    @Tool(name = "getCurrentLocalTime", description = "Get the current time in user's timezone")
    public String getCurrentLocalTime() {
        logger.info("Returning current time in user's timezone");
        LocalTime localTime = LocalTime.now();
        return localTime.toString();
    }

    @Tool(name = "getCurrentTime", description = "Get the current time in the specified timezone")
    public String getCurrentTime(@ToolParam(description = "The timezone for which to get the current time") String timezone) {
        logger.info("Returning current time in the timezone {}", timezone);
        LocalTime localTime = LocalTime.now(ZoneId.of(timezone));
        return localTime.toString();
    }
}

package com.springboot.project.common.TimeZoneUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class TimeZoneUtil {

    /**
     * return value like +08:00
     * 
     * @param timeZone
     * @return
     */
    public String getTimeZone(String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime zonedDateTime = Instant.now().atZone(zoneId);
        timeZone = String.format("%tz", zonedDateTime);
        timeZone = timeZone.substring(0, 3) + ":" + timeZone.substring(3, 5);
        if (timeZone.length() != 6) {
            throw new RuntimeException("invalid time zone");
        }
        if (!Pattern.compile(
                "^[" + Pattern.quote("+") + Pattern.quote("-") + "]{1}" + "[0-9]{2}" + Pattern.quote(":") + "[0-9]{2}$")
                .matcher(timeZone).find()) {
            throw new RuntimeException("invalid time zone");
        }
        return timeZone;
    }

    /**
     * return value like +00:00
     * 
     * @param timeZone
     * @return
     */
    public String getTimeZoneFromUTC() {
        return this.getTimeZone("UTC");
    }

}

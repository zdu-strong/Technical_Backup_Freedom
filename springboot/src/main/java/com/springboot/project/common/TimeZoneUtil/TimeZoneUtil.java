package com.springboot.project.common.TimeZoneUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TimeZoneUtil {

    /**
     * return value like +08:00
     * 
     * @param timeZone
     * @return
     */
    public String getTimeZoneString(String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime zonedDateTime = Instant.now().atZone(zoneId);
        timeZone = String.format("%tz", zonedDateTime);
        timeZone = timeZone.substring(0, 3) + ":" + timeZone.substring(3, 5);
        if (timeZone.length() != 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time zone");
        }
        if (!Pattern.compile(
                "^[" + Pattern.quote("+") + Pattern.quote("-") + "]{1}" + "[0-9]{2}" + Pattern.quote(":") + "[0-9]{2}$")
                .matcher(timeZone).find()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time zone");
        }
        return timeZone;
    }

    /**
     * return value like +08:00
     * 
     * @param timeZone
     * @return
     */
    public String getTimeZoneString(TimeZone timeZone) {
        var zoneId = timeZone.toZoneId();
        return this.getTimeZoneString(zoneId.getId());
    }

    public TimeZone getTimeZone(String timeZone) {
        var zoneId = ZoneId.of(this.getTimeZoneString(timeZone));
        return TimeZone.getTimeZone(zoneId);
    }

    /**
     * return value like +00:00
     * 
     * @param timeZone
     * @return
     */
    public TimeZone UTC() {
        return this.getTimeZone("UTC");
    }

    /**
     * return value like +00:00
     * 
     * @param timeZone
     * @return
     */
    public String UTCString() {
        return this.getTimeZoneString("UTC");
    }

}

#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.dao.mongo.converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author Mickael Dubois
 */
public class DateToLocalTimeConverter implements Converter<Date, LocalTime> {

    @Override
    public LocalTime convert(Date source) {
        Instant instant = Instant.ofEpochMilli(source.getTime());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();
    }

}

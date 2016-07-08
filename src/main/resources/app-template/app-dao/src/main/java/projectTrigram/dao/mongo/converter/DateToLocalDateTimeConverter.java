#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao.mongo.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author Mickael Dubois
 */
public class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {

    @Override
    public LocalDateTime convert(Date source) {
        return LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
    }

}

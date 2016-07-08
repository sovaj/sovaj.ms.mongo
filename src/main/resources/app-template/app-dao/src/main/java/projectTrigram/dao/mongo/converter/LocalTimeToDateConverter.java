#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao.mongo.converter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author Mickael Dubois
 */
public class LocalTimeToDateConverter implements Converter<LocalTime, Date> {

    private static final int A_YEAR = 2015;
    private static final int A_MONTH = 01;
    private static final int A_DAY = 01;

    @Override
    public Date convert(LocalTime source) {
        return Date.from(source.atDate(LocalDate.of(A_YEAR, A_MONTH, A_DAY)).
                atZone(ZoneId.systemDefault()).toInstant());
    }

}

#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;


/**
 *
 * @author Mickael Dubois
 */
public class Candidate extends UserDetails {

    private Long candidateNum;
    private UserMinimal referenceBy;
    private String referenceFrom;
    private String curriculum;
    private String description;
    private Long actualSalary;
    private Long requestedSalary;
    private Integer experience;
    private Integer frenchLevel;
    private Integer englishLevel;
    private String address;
    private String status;
    private Date statusUpdateDate;
    private UserMinimal recruiter;
    private String facebook;
    private String linkedin;
    private String monster;
    private String twitter;
    private String google;
    private String viadeo;

}

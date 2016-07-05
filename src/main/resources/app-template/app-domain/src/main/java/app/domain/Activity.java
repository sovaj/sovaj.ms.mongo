#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.domain;

import ${package}.app.domain.enumeration.ActivityAction;
import ${package}.app.domain.enumeration.ActivityType;

/**
 *
 * @author Mickael Dubois
 */
public class Activity extends BusinessObject {

    private UserMinimal originator;
    private ActivityType activityType;
    private ActivityAction activityAction;
    private ActivityObject targetObject;

    public UserMinimal getOriginator() {
        return originator;
    }

    public void setOriginator(UserMinimal originator) {
        this.originator = originator;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public ActivityAction getActivityAction() {
        return activityAction;
    }

    public void setActivityAction(ActivityAction activityAction) {
        this.activityAction = activityAction;
    }

    public ActivityObject getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(ActivityObject targetObject) {
        this.targetObject = targetObject;
    }

}

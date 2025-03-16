package org.ecosync.model;

import org.ecosync.storage.StorageName;

@StorageName("tc_ticketstatuss")
public class TicketStatus extends BaseModel {

    public static final String TICKET_NEW     = "new";            /* The ticket has been created and is awaiting review.           */
    public static final String TICKET_ASSIGNED = "assigned";      /* The ticket has been assigned to a support agent or team.      */
    public static final String TICKET_IN_PROGRESS = "inProgress"; /* Work on the ticket is currently underway.                     */
    public static final String TICKET_PENDING = "pending";        /* Waiting for user/customer response or additional information. */
    public static final String TICKET_ON_HOLD     = "onHold";     /* The issue is temporarily paused, possibly due to external dependencies.  */
    public static final String TICKET_ESCALATED = "escalated";    /* The ticket has been forwarded to a higher support level or management.   */
    public static final String TICKET_RESOLVED = "resolved";      /* The issue has been fixed, but the ticket is still open for confirmation. */
    public static final String TICKET_CLOSED = "closed";          /* The issue has been resolved and confirmed by the requester, or it has been closed */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
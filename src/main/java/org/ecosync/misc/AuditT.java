package org.ecosync.misc;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.ecosync.storage.Storage;
import org.ecosync.storage.StorageException;
import org.ecosync.storage.query.Columns;
import org.ecosync.storage.query.Condition;
import org.ecosync.storage.query.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ecosync.model.Audittrail;
import org.ecosync.model.User;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuditT {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditT.class);
    public static final Map<String, String> RCompMap = new HashMap<>();
    public static final Map<String, String> RMethodMap = new HashMap<>();
    public static final String PLATFORM = "Navi Trace VTS API" ;

    public static final String K_ATTRIBUTE = "attribute" ;
    public static final String K_AUDIT = "audittrail";
    public static final String K_CALENDAR = "calendar" ;
    public static final String K_COMMAND = "command" ;
    public static final String K_DEVICE = "device" ;
    public static final String K_DRIVER = "driver" ;
    public static final String K_EVENT = "event";
    public static final String K_GEOFENCE = "geofence" ;
    public static final String K_GROUP = "group" ;
    public static final String K_MAINTENANCE = "maintenance" ;
    public static final String K_NOTIFICATION = "notification" ;
    public static final String K_ORDER = "order" ;
    public static final String K_PERMISSION = "permission" ;
    public static final String K_POSITION = "position";
    public static final String K_REPORT = "report" ;
    public static final String K_ROUTE = "route" ;
    public static final String K_SCHEDULE = "schedulereport" ;
    public static final String K_SERVER = "server" ;
    public static final String K_SESSION = "session" ;
    public static final String K_SOS = "sos" ;
    public static final String K_TRIP = "trip";
    public static final String K_USER = "user" ;
    public static final String K_USER_ROLE = "userrole" ;
    public static final String K_VEHICLE_REG = "vehiclereg" ;


    public static final String K_GET = "@GET" ;
    public static final String K_POST = "@POST" ;
    public static final String K_PUT = "@PUT" ;
    public static final String K_DELETE = "@DELETE" ;

    public static final String DESC_RN_ASSIGN = "Role not assigned." ;
    public static final String DESC_NO_PERMS  = "Permission not given" ;
    public static final String DESC_NET_ERR = "Network Error" ;
    public static final String DESC_CREATED = "Resource created" ;
    public static final String DESC_UPDATED = "Resource updated" ;
    public static final String DESC_DELETED = "Resource deleted" ;
    public static final String DESC_FAILED_CREATED = "Resource creation failed" ;
    public static final String DESC_FAILED_UPDATED = "Resource updation failed" ;
    public static final String DESC_FAILED_DELETED = "Resource deletion failed" ;
    public static final String DESC_REQUESTED = "Resource requested" ;


    static {
        RCompMap.put(K_ATTRIBUTE, "Attribute Component");
        RCompMap.put(K_CALENDAR, "Calendar Component");
        RCompMap.put(K_COMMAND, "Command Component");
        RCompMap.put(K_DEVICE, "Device Component");
        RCompMap.put(K_GEOFENCE, "Geofence Component");
        RCompMap.put(K_GROUP, "Group Component");
        RCompMap.put(K_MAINTENANCE, "Maintenance Component");
        RCompMap.put(K_NOTIFICATION, "Notification Component");
        RCompMap.put(K_ORDER, "Order Component");
        RCompMap.put(K_PERMISSION, "Permission Component");
        RCompMap.put(K_REPORT, "Report Component");
        RCompMap.put(K_ROUTE, "Route Component");
        RCompMap.put(K_SCHEDULE, "Schedule Report Component");
        RCompMap.put(K_SESSION, "Session Component");
        RCompMap.put(K_SOS, "Sos Component");
        RCompMap.put(K_USER, "User Component");
        RCompMap.put(K_USER_ROLE, "User Role Component");
        RCompMap.put(K_VEHICLE_REG, "Vehicle Reg. Component");
    }

    static {
        RMethodMap.put(K_GET, "Requested ");
        RMethodMap.put(K_POST, "Created ");
        RMethodMap.put(K_PUT, "Updated ");
        RMethodMap.put(K_DELETE, "Deleted ");
    }

    @Inject
    protected Storage storage;

    public void write(long userId,
                             String resourceKey,
                             String method,
                             int Httpstatus,
                             HttpServletRequest request,
                             String description  ) throws SQLException, StorageException {
        Audittrail auditTrail = new Audittrail() ;
        auditTrail.setCreatedon(new Date());
        User user = storage.getObject(User.class, new Request(
                new Columns.All(),
                new Condition.Equals("id", userId)));
        auditTrail.setUserid(userId);
        auditTrail.setUsername(user.getName());
        auditTrail.setComponent(RCompMap.get(resourceKey));
        auditTrail.setActivity(RMethodMap.get(method) + resourceKey);
        Response.Status status = Response.Status.fromStatusCode(Httpstatus);
        if(status !=null) {
            auditTrail.setStatuscode(status.toString());
        } else {
            auditTrail.setStatuscode("Invalid HTTP status code: " + status.toString());
        }
        auditTrail.setPlatform(PLATFORM);
        auditTrail.setDescription(description) ;
        auditTrail.setIpaddress(getClientIp(request));
        storage.addObject(auditTrail, new Request(new Columns.Exclude("id")));
    }

    public static String getComponent(String resourceKey){
        if(RCompMap.containsKey(resourceKey))
            return RCompMap.get(resourceKey);
        return null ;
    }

    public static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr ;
    }
}

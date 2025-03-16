package org.ecosync.misc;

import jakarta.inject.Inject;
import org.ecosync.config.Config;
import org.ecosync.config.Keys;
import org.ecosync.model.ErrorLog;
import org.ecosync.storage.Storage;
import org.ecosync.storage.StorageException;
import org.ecosync.storage.query.Columns;
import org.ecosync.storage.query.Request;

import java.util.*;

public class ErrorLogManager {

    private static final String KEY_ENABLE = "enable";

    @Inject
    protected Storage storage;

    @Inject
    protected Config config;

    public void createErrLog(String component, String errMessage, long userId, long deviceId){
        String enable = config.getString(Keys.ERROR_LOGGER_ENABLE);
        if(enable.equals(KEY_ENABLE)) {  /*** Check error log feature is enable, ***/
            String moduleList = config.getString(Keys.ERROR_LOGGER_MODULE_LIST); // "device,geofence,group,user,notification" ;  //
            String[] modules = moduleList.split(",");
            List<String> modulesList = Arrays.asList(modules);
            if(modulesList.contains(component)){
                ErrorLog errLog = new ErrorLog(component, errMessage, userId, deviceId);
                try {
                    storage.addObject(errLog, new Request(new Columns.Exclude("id")));
                } catch (StorageException e) {
                    // no changes.
                }
            }
        }
    }
}

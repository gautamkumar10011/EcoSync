package org.ecosync.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.traccar.helper.LogAction;
import org.ecosync.model.Geofence;

public class ReadSpreadSheetFile {
    public static void  readExcelFile(String file, long userId) throws IOException, ParseException, IllegalStateException, SQLException {
        if(!isValidFile(file)){
            throw new IOException() ;
        }
        FileInputStream fis = new FileInputStream(new File(file));
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);
        FormulaEvaluator formulaEvaluator=wb.getCreationHelper().createFormulaEvaluator();
        LinkedList<Geofence> geofences = new LinkedList<>() ;
        int i = 0 ;
        for(Row row: sheet)
        {
            if(i != 0 && row!= null && row.getCell(0)!=null) {
                geofences.add(buildGeofenceObject(row));
            }
            i++  ;
        }

        for(Geofence geofence: geofences){
            geofence.setCreatedBy(userId);
            geofence.setCreatedOn(new Date());
            // Context.getGeofenceManager().addItem(geofence);
            // Context.getDataManager().linkObject(User.class, userId, Geofence.class, geofence.getId(), true);
            // LogAction.link(userId, User.class, userId, Geofence.class, geofence.getId());
        }
    }

    private static Geofence buildGeofenceObject(Row row) throws ParseException, IllegalStateException {
        String name = row.getCell(0).getStringCellValue() ;
        double lat = row.getCell(1).getNumericCellValue() ;
        double lng = row.getCell(2).getNumericCellValue() ;
        double radius = row.getCell(3).getNumericCellValue() ;
        String type = row.getCell(4).getStringCellValue() ;
        validateData(name, lat, lng, radius, type) ;
        Geofence geofence = new Geofence() ;
        geofence.setName(name);
        geofence.setArea("CIRCLE ("+ lat + " " + lng +"," + radius + ")");
        return geofence ;
    }

    private static void validateData(String name, double lat, double lng, double radius, String type) throws IllegalStateException {
        name = name.trim().replaceAll(" +", "");
    }

    private static String getFileExtension(String fileName) {
        String extension = "" ;
        int i = fileName.lastIndexOf('.');
        return fileName.substring(i+1);
    }

    public static boolean isValidFile(String fileName) {
        String extension = "" ;
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }
        return isValidExtension(extension.toLowerCase()) ;
    }

    private static boolean isValidExtension(String extension) {
        switch (extension){
            case "xlsx":
            case "xls":
                return true ;
            default:
                return false ;
        }
    }
}
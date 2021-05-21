package energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;

@RestController
public class Controller {

    LogedUser logedUser = null;

    public String generateString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 20;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        return generatedString;
    }

    @RequestMapping(path = "/ActualTotalLoad/{AreaName}/{Resolution}/date/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getActualTotalLoad(@PathVariable("AreaName") String areaName,
            @PathVariable("Resolution") String resolution, @PathVariable("Date") String date,
            @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8, 10));
            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "select AreaTypeCodeText,MapCodeText,DateTime,TotalLoadValue,UpdateTime from resolutionCode,actualtotalload,AreaTypeCode,MapCode where actualtotalload.AreaName='"
                                + areaName + "' and actualtotalload.Year = " + year + " and actualtotalload.Month="
                                + month + " and actualtotalload.Day=" + day + " and ResolutionCode.ResolutionCodeText='"
                                + resolution
                                + "' and ResolutionCode.id=actualtotalload.resolutioncodeid and AreaTypeCode.id=actualtotalload.areatypecodeid and mapcode.id=actualtotalload.mapcodeid order by datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, Day, DayTimeUTC, ActualTotalLoadValue, UpdateTimeUTC");
                    while (rs.next()) {
                        ActualTotalLoad a = new ActualTotalLoad(areaName, rs.getString(1), rs.getString(2), resolution,
                                year, month, day, rs.getString(3), rs.getFloat(4), rs.getString(5));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        ActualTotalLoad a = new ActualTotalLoad(areaName, rs.getString(1), rs.getString(2), resolution,
                                year, month, day, rs.getString(3), rs.getFloat(4), rs.getString(5));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/ActualTotalLoad/{AreaName}/{Resolution}/month/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getActualTotalLoad2(@PathVariable("AreaName") String areaName,
            @PathVariable("Resolution") String resolution, @PathVariable("Date") String date,
            @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));

            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "select AreaTypeCodeText,MapCodeText,Day,DateTime,SUM(TotalLoadValue),UpdateTime from resolutionCode,actualtotalload,AreaTypeCode,MapCode where actualtotalload.AreaName='"
                                + areaName + "' and actualtotalload.Year = " + year + " and actualtotalload.Month="
                                + month + " and ResolutionCode.ResolutionCodeText='" + resolution
                                + "' and ResolutionCode.id=actualtotalload.resolutioncodeid and AreaTypeCode.id=actualtotalload.areatypecodeid and mapcode.id=actualtotalload.mapcodeid group by day order by datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, Day, ActualTotalLoadByDayValue");
                    while (rs.next()) {
                        ActualTotalLoadByDay a = new ActualTotalLoadByDay(areaName, rs.getString(1), rs.getString(2),
                                resolution, year, month, rs.getInt(3), rs.getFloat(5));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        ActualTotalLoadByDay a = new ActualTotalLoadByDay(areaName, rs.getString(1), rs.getString(2),
                                resolution, year, month, rs.getInt(3), rs.getFloat(5));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/ActualTotalLoad/{AreaName}/{Resolution}/year/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getActualTotalLoad3(@PathVariable("AreaName") String areaName,
            @PathVariable("Resolution") String resolution, @PathVariable("Date") String date,
            @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));

            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "select AreaTypeCodeText,MapCodeText,Month,DateTime,SUM(TotalLoadValue),UpdateTime from resolutionCode,actualtotalload,AreaTypeCode,MapCode where actualtotalload.AreaName='"
                                + areaName + "' and actualtotalload.Year = " + year
                                + " and ResolutionCode.ResolutionCodeText='" + resolution
                                + "' and ResolutionCode.id=actualtotalload.resolutioncodeid and AreaTypeCode.id=actualtotalload.areatypecodeid and mapcode.id=actualtotalload.mapcodeid group by month order by datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, ActualTotalLoadByMonthValue");
                    while (rs.next()) {
                        ActualTotalLoadByMonth a = new ActualTotalLoadByMonth(areaName, rs.getString(1),
                                rs.getString(2), resolution, year, rs.getInt(3), rs.getFloat(5));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        ActualTotalLoadByMonth a = new ActualTotalLoadByMonth(areaName, rs.getString(1),
                                rs.getString(2), resolution, year, rs.getInt(3), rs.getFloat(5));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/AggregatedGenerationPerType/{AreaName}/{ProductionType}/{Resolution}/date/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getAggregatedGenerationPerType(@PathVariable("AreaName") String areaName,
            @PathVariable("ProductionType") String productionType, @PathVariable("Resolution") String resolution,
            @PathVariable("Date") String date, @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8, 10));

            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs;
                if (productionType.equals("AllTypes"))
                    rs = stmt.executeQuery(
                            "select AreaTypeCodeText,MapCodeText,DateTime,ProductionTypeText,ActualGenerationOutput,UpdateTime from resolutionCode,AggregatedGenerationPerType,AreaTypeCode,MapCode,ProductionType  where AggregatedGenerationPerType.AreaName='"
                                    + areaName + "'and AggregatedGenerationPerType.Year = " + year
                                    + " and AggregatedGenerationPerType.Month=" + month
                                    + " and AggregatedGenerationPerType.Day=" + day
                                    + " and ResolutionCode.ResolutionCodeText='" + resolution
                                    + "' and ResolutionCode.id=AggregatedGenerationPerType.resolutioncodeid and AreaTypeCode.id=AggregatedGenerationPerType.areatypecodeid and mapcode.id=AggregatedGenerationPerType.mapcodeid and AggregatedGenerationPerType.ProductionTypeId = ProductionType.id order by updatetime asc;");
                else
                    rs = stmt.executeQuery(
                            "select AreaTypeCodeText,MapCodeText,DateTime,ProductionTypeText,ActualGenerationOutput,UpdateTime from resolutionCode,AggregatedGenerationPerType,AreaTypeCode,MapCode,ProductionType  where AggregatedGenerationPerType.AreaName='"
                                    + areaName + "'and AggregatedGenerationPerType.Year = " + year
                                    + " and AggregatedGenerationPerType.Month=" + month
                                    + " and AggregatedGenerationPerType.Day=" + day
                                    + " and ResolutionCode.ResolutionCodeText='" + resolution
                                    + "' and ProductionType.ProductionTypeText='" + productionType
                                    + "' and ResolutionCode.id=AggregatedGenerationPerType.resolutioncodeid and AreaTypeCode.id=AggregatedGenerationPerType.areatypecodeid and mapcode.id=AggregatedGenerationPerType.mapcodeid and AggregatedGenerationPerType.ProductionTypeId = ProductionType.id order by datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, Day, DayTimeUTC, ProductionType, ActualGenerationOutputValue, UpdateTimeUTC");
                    while (rs.next()) {
                        AggregatedGenerationPerType a = new AggregatedGenerationPerType(areaName, rs.getString(1),
                                rs.getString(2), resolution, year, month, day, rs.getString(3), rs.getString(4),
                                rs.getFloat(5), rs.getString(6));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        AggregatedGenerationPerType a = new AggregatedGenerationPerType(areaName, rs.getString(1),
                                rs.getString(2), resolution, year, month, day, rs.getString(3), rs.getString(4),
                                rs.getFloat(5), rs.getString(6));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/AggregatedGenerationPerType/{AreaName}/{ProductionType}/{Resolution}/month/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getAggregatedGenerationPerType2(@PathVariable("AreaName") String areaName,
            @PathVariable("ProductionType") String productionType, @PathVariable("Resolution") String resolution,
            @PathVariable("Date") String date, @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));

            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs;
                if (productionType.equals("AllTypes"))
                    rs = stmt.executeQuery(
                            "select AreaTypeCodeText,MapCodeText,day,DateTime,ProductionTypeText,SUM(ActualGenerationOutput),UpdateTime from resolutionCode,AggregatedGenerationPerType,AreaTypeCode,MapCode,ProductionType  where AggregatedGenerationPerType.AreaName='"
                                    + areaName + "'and AggregatedGenerationPerType.Year = " + year
                                    + " and AggregatedGenerationPerType.Month=" + month
                                    + " and ResolutionCode.ResolutionCodeText='" + resolution
                                    + "' and ResolutionCode.id=AggregatedGenerationPerType.resolutioncodeid and AreaTypeCode.id=AggregatedGenerationPerType.areatypecodeid and mapcode.id=AggregatedGenerationPerType.mapcodeid and AggregatedGenerationPerType.ProductionTypeId = ProductionType.id group by day, productiontypetext order by updatetime asc;");
                else
                    rs = stmt.executeQuery(
                            "select AreaTypeCodeText,MapCodeText,day,DateTime,ProductionTypeText,SUM(ActualGenerationOutput),UpdateTime from resolutionCode,AggregatedGenerationPerType,AreaTypeCode,MapCode,ProductionType  where AggregatedGenerationPerType.AreaName='"
                                    + areaName + "'and AggregatedGenerationPerType.Year = " + year
                                    + " and AggregatedGenerationPerType.Month=" + month
                                    + " and ResolutionCode.ResolutionCodeText='" + resolution
                                    + "' and ProductionType.ProductionTypeText='" + productionType
                                    + "' and ResolutionCode.id=AggregatedGenerationPerType.resolutioncodeid and AreaTypeCode.id=AggregatedGenerationPerType.areatypecodeid and mapcode.id=AggregatedGenerationPerType.mapcodeid and AggregatedGenerationPerType.ProductionTypeId = ProductionType.id group by day order by datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, Day, ProductionType, ActualGenerationOutputByDayValue");
                    while (rs.next()) {
                        AggregatedGenerationPerTypeByDay a = new AggregatedGenerationPerTypeByDay(areaName,
                                rs.getString(1), rs.getString(2), resolution, year, month, rs.getInt(3),
                                rs.getString(5), rs.getFloat(6));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        AggregatedGenerationPerTypeByDay a = new AggregatedGenerationPerTypeByDay(areaName,
                                rs.getString(1), rs.getString(2), resolution, year, month, rs.getInt(3),
                                rs.getString(5), rs.getFloat(6));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/AggregatedGenerationPerType/{AreaName}/{ProductionType}/{Resolution}/year/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getAggregatedGenerationPerType3(@PathVariable("AreaName") String areaName,
            @PathVariable("ProductionType") String productionType, @PathVariable("Resolution") String resolution,
            @PathVariable("Date") String date, @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));

            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs;
                if (productionType.equals("AllTypes"))
                    rs = stmt.executeQuery(
                            "select AreaTypeCodeText,MapCodeText,month,DateTime,ProductionTypeText,SUM(ActualGenerationOutput),UpdateTime from resolutionCode,AggregatedGenerationPerType,AreaTypeCode,MapCode,ProductionType  where AggregatedGenerationPerType.AreaName='"
                                    + areaName + "'and AggregatedGenerationPerType.Year = " + year
                                    + " and ResolutionCode.ResolutionCodeText='" + resolution
                                    + "' and ResolutionCode.id=AggregatedGenerationPerType.resolutioncodeid and AreaTypeCode.id=AggregatedGenerationPerType.areatypecodeid and mapcode.id=AggregatedGenerationPerType.mapcodeid and AggregatedGenerationPerType.ProductionTypeId = ProductionType.id group by month, productiontypetext order by updatetime asc;");
                else
                    rs = stmt.executeQuery(
                            "select AreaTypeCodeText,MapCodeText,month,DateTime,ProductionTypeText,SUM(ActualGenerationOutput),UpdateTime from resolutionCode,AggregatedGenerationPerType,AreaTypeCode,MapCode,ProductionType  where AggregatedGenerationPerType.AreaName='"
                                    + areaName + "'and AggregatedGenerationPerType.Year = " + year
                                    + " and ResolutionCode.ResolutionCodeText='" + resolution
                                    + "' and ProductionType.ProductionTypeText='" + productionType
                                    + "' and ResolutionCode.id=AggregatedGenerationPerType.resolutioncodeid and AreaTypeCode.id=AggregatedGenerationPerType.areatypecodeid and mapcode.id=AggregatedGenerationPerType.mapcodeid and AggregatedGenerationPerType.ProductionTypeId = ProductionType.id group by month order by datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, Day, ProductionType, ActualGenerationOutputByMonthValue");
                    while (rs.next()) {
                        AggregatedGenerationPerTypeByMonth a = new AggregatedGenerationPerTypeByMonth(areaName,
                                rs.getString(1), rs.getString(2), resolution, year, rs.getInt(3), rs.getString(5),
                                rs.getFloat(6));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        AggregatedGenerationPerTypeByMonth a = new AggregatedGenerationPerTypeByMonth(areaName,
                                rs.getString(1), rs.getString(2), resolution, year, rs.getInt(3), rs.getString(5),
                                rs.getFloat(6));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/DayAheadTotalLoadForecast/{AreaName}/{Resolution}/date/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getDayAheadTotalLoadForecast(@PathVariable("AreaName") String areaName,
            @PathVariable("Resolution") String resolution, @PathVariable("Date") String date,
            @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8, 10));

            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "select AreaTypeCodeText,MapCodeText,DateTime,TotalLoadValue,UpdateTime from resolutionCode,DayAheadTotalLoadForecast,AreaTypeCode,MapCode where DayAheadTotalLoadForecast.AreaName='"
                                + areaName + "'and DayAheadTotalLoadForecast.Year =" + year
                                + " and DayAheadTotalLoadForecast.Month=" + month
                                + " and DayAheadTotalLoadForecast.Day=" + day
                                + "  and ResolutionCode.ResolutionCodeText='" + resolution
                                + "' and ResolutionCode.id=DayAheadTotalLoadForecast.resolutioncodeid and AreaTypeCode.id=DayAheadTotalLoadForecast.areatypecodeid and mapcode.id=DayAheadTotalLoadForecast.mapcodeid order by datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, Day, DayTimeUTC, DayAheadTotalLoadForecastValue, UpdateTimeUTC");
                    while (rs.next()) {
                        DayAheadTotalLoadForecast a = new DayAheadTotalLoadForecast(areaName, rs.getString(1),
                                rs.getString(2), resolution, year, month, day, rs.getString(3), rs.getFloat(4),
                                rs.getString(5));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        DayAheadTotalLoadForecast a = new DayAheadTotalLoadForecast(areaName, rs.getString(1),
                                rs.getString(2), resolution, year, month, day, rs.getString(3), rs.getFloat(4),
                                rs.getString(5));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/DayAheadTotalLoadForecast/{AreaName}/{Resolution}/month/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getDayAheadTotalLoadForecast2(@PathVariable("AreaName") String areaName,
            @PathVariable("Resolution") String resolution, @PathVariable("Date") String date,
            @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));

            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "select AreaTypeCodeText,MapCodeText,Day,DateTime,SUM(TotalLoadValue),UpdateTime from resolutionCode,DayAheadTotalLoadForecast,AreaTypeCode,MapCode where DayAheadTotalLoadForecast.AreaName='"
                                + areaName + "'and DayAheadTotalLoadForecast.Year =" + year
                                + " and DayAheadTotalLoadForecast.Month=" + month
                                + "  and ResolutionCode.ResolutionCodeText='" + resolution
                                + "' and ResolutionCode.id=DayAheadTotalLoadForecast.resolutioncodeid and AreaTypeCode.id=DayAheadTotalLoadForecast.areatypecodeid and mapcode.id=DayAheadTotalLoadForecast.mapcodeid group by day order by datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, Day, DayAheadTotalLoadForecastByDayValue");
                    while (rs.next()) {
                        DayAheadTotalLoadForecastByDay a = new DayAheadTotalLoadForecastByDay(areaName, rs.getString(1),
                                rs.getString(2), resolution, year, month, rs.getInt(3), rs.getFloat(5));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        DayAheadTotalLoadForecastByDay a = new DayAheadTotalLoadForecastByDay(areaName, rs.getString(1),
                                rs.getString(2), resolution, year, month, rs.getInt(3), rs.getFloat(5));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/DayAheadTotalLoadForecast/{AreaName}/{Resolution}/year/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getDayAheadTotalLoadForecast3(@PathVariable("AreaName") String areaName,
            @PathVariable("Resolution") String resolution, @PathVariable("Date") String date,
            @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));

            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "select AreaTypeCodeText,MapCodeText,Month,DateTime,SUM(TotalLoadValue),UpdateTime from resolutionCode,DayAheadTotalLoadForecast,AreaTypeCode,MapCode where DayAheadTotalLoadForecast.AreaName='"
                                + areaName + "'and DayAheadTotalLoadForecast.Year =" + year
                                + "  and ResolutionCode.ResolutionCodeText='" + resolution
                                + "' and ResolutionCode.id=DayAheadTotalLoadForecast.resolutioncodeid and AreaTypeCode.id=DayAheadTotalLoadForecast.areatypecodeid and mapcode.id=DayAheadTotalLoadForecast.mapcodeid group by month order by datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, DayAheadTotalLoadForecastByDayValue");
                    while (rs.next()) {
                        DayAheadTotalLoadForecastByMonth a = new DayAheadTotalLoadForecastByMonth(areaName,
                                rs.getString(1), rs.getString(2), resolution, year, rs.getInt(3), rs.getFloat(5));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        DayAheadTotalLoadForecastByMonth a = new DayAheadTotalLoadForecastByMonth(areaName,
                                rs.getString(1), rs.getString(2), resolution, year, rs.getInt(3), rs.getFloat(5));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/ActualvsForecast/{AreaName}/{Resolution}/date/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getActualvsForecast(@PathVariable("AreaName") String areaName,
            @PathVariable("Resolution") String resolution, @PathVariable("Date") String date,
            @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8, 10));
            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "select AreaTypeCodeText,MapCodeText,actualtotalload.DateTime,actualtotalload.TotalLoadValue,DayAheadTotalLoadForecast.TotalLoadValue,DayAheadTotalLoadForecast.DateTime from resolutionCode,actualtotalload,DayAheadTotalLoadForecast,AreaTypeCode,MapCode where actualtotalload.AreaName='"
                                + areaName + "' and actualtotalload.Year = " + year + " and actualtotalload.Month="
                                + month + " and actualtotalload.Day=" + day + " and ResolutionCode.ResolutionCodeText='"
                                + resolution + "' and DayAheadTotalLoadForecast.AreaName='" + areaName
                                + "' and DayAheadTotalLoadForecast.Year =" + year
                                + " and DayAheadTotalLoadForecast.Month=" + month
                                + " and DayAheadTotalLoadForecast.Day=" + day
                                + " and ResolutionCode.id=actualtotalload.resolutioncodeid and AreaTypeCode.id=actualtotalload.areatypecodeid and actualtotalload.DateTime = DayAheadTotalLoadForecast.DateTime and mapcode.id=actualtotalload.mapcodeid and DayAheadTotalLoadForecast.mapcodeid=actualtotalload.mapcodeid and DayAheadTotalLoadForecast.resolutioncodeid=actualtotalload.resolutioncodeid and DayAheadTotalLoadForecast.areatypecodeid=actualtotalload.areatypecodeid order by actualtotalload.datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, Day, DayTimeUTC, DayAheadTotalLoadForecastValue,  ActualTotalLoadValue");
                    while (rs.next()) {
                        ActualvsForecast a = new ActualvsForecast(areaName, rs.getString(1), rs.getString(2),
                                resolution, year, month, day, rs.getString(3), rs.getFloat(4), rs.getFloat(5));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        ActualvsForecast a = new ActualvsForecast(areaName, rs.getString(1), rs.getString(2),
                                resolution, year, month, day, rs.getString(3), rs.getFloat(4), rs.getFloat(5));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/ActualvsForecast/{AreaName}/{Resolution}/month/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getActualvsForecast2(@PathVariable("AreaName") String areaName,
            @PathVariable("Resolution") String resolution, @PathVariable("Date") String date,
            @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();

            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "select AreaTypeCodeText,MapCodeText,actualtotalload.day,actualtotalload.DateTime,SUM(actualtotalload.TotalLoadValue),SUM(DayAheadTotalLoadForecast.TotalLoadValue),DayAheadTotalLoadForecast.DateTime from resolutionCode,actualtotalload,DayAheadTotalLoadForecast,AreaTypeCode,MapCode where actualtotalload.AreaName='"
                                + areaName + "' and actualtotalload.Year = " + year + " and actualtotalload.Month="
                                + month + " and ResolutionCode.ResolutionCodeText='" + resolution
                                + "' and DayAheadTotalLoadForecast.AreaName='" + areaName
                                + "' and DayAheadTotalLoadForecast.Year =" + year
                                + " and DayAheadTotalLoadForecast.Month=" + month
                                + " and ResolutionCode.id=actualtotalload.resolutioncodeid and AreaTypeCode.id=actualtotalload.areatypecodeid and DayAheadTotalLoadForecast.DateTime=actualtotalload.DateTime and mapcode.id=actualtotalload.mapcodeid and DayAheadTotalLoadForecast.mapcodeid=actualtotalload.mapcodeid and DayAheadTotalLoadForecast.resolutioncodeid=actualtotalload.resolutioncodeid and DayAheadTotalLoadForecast.areatypecodeid=actualtotalload.areatypecodeid group by day order by actualtotalload.datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, Day, DayAheadTotalLoadForecastByDayValue,  ActualTotalLoadByDayValue");
                    while (rs.next()) {
                        ActualvsForecastByDay a = new ActualvsForecastByDay(areaName, rs.getString(1), rs.getString(2),
                                resolution, year, month, rs.getInt(3), rs.getFloat(5), rs.getFloat(6));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        ActualvsForecastByDay a = new ActualvsForecastByDay(areaName, rs.getString(1), rs.getString(2),
                                resolution, year, month, rs.getInt(3), rs.getFloat(5), rs.getFloat(6));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/ActualvsForecast/{AreaName}/{Resolution}/year/{Date}", method = RequestMethod.GET)
    public ResponseEntity<Object> getActualvsForecast3(@PathVariable("AreaName") String areaName,
            @PathVariable("Resolution") String resolution, @PathVariable("Date") String date,
            @RequestParam("format") String format,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) {

        if ((logedUser == null) || (!logedUser.getToken().equals(token))) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.UNAUTHORIZED);
        } else if (logedUser.getQuota() <= logedUser.getUsed_quota()) {
            List<Object> list = new ArrayList<Object>();
            list.add("error");
            return new ResponseEntity<Object>(list, HttpStatus.PAYMENT_REQUIRED);
        } else {
            List<Object> list = new ArrayList<Object>();
            int year = Integer.parseInt(date.substring(0, 4));
            try {

                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "root", "root");
                if (conn == null)
                    System.out.println("Connection problem");

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "select AreaTypeCodeText,MapCodeText,actualtotalload.month,actualtotalload.DateTime,SUM(actualtotalload.TotalLoadValue),SUM(DayAheadTotalLoadForecast.TotalLoadValue),DayAheadTotalLoadForecast.DateTime from resolutionCode,actualtotalload,DayAheadTotalLoadForecast,AreaTypeCode,MapCode where actualtotalload.AreaName='"
                                + areaName + "' and actualtotalload.Year = " + year
                                + " and ResolutionCode.ResolutionCodeText='" + resolution
                                + "' and DayAheadTotalLoadForecast.AreaName='" + areaName
                                + "' and DayAheadTotalLoadForecast.Year =" + year
                                + " and ResolutionCode.id=actualtotalload.resolutioncodeid and DayAheadTotalLoadForecast.DateTime=actualtotalload.DateTime and AreaTypeCode.id=actualtotalload.areatypecodeid and mapcode.id=actualtotalload.mapcodeid and DayAheadTotalLoadForecast.mapcodeid=actualtotalload.mapcodeid and DayAheadTotalLoadForecast.resolutioncodeid=actualtotalload.resolutioncodeid and DayAheadTotalLoadForecast.areatypecodeid=actualtotalload.areatypecodeid group by month order by actualtotalload.datetime asc;");

                if (format.equals("csv")) {
                    list.add(
                            "Source, Dataset, AreaName, AreaTypeCode, MapCode, ResolutionCode, Year, Month, DayAheadTotalLoadForecastByMonthValue,  ActualTotalLoadByMonthValue");
                    while (rs.next()) {
                        ActualvsForecastByMonth a = new ActualvsForecastByMonth(areaName, rs.getString(1),
                                rs.getString(2), resolution, year, rs.getInt(3), rs.getFloat(5), rs.getFloat(6));
                        list.add(a.toString());
                    }
                } else
                    while (rs.next()) {
                        ActualvsForecastByMonth a = new ActualvsForecastByMonth(areaName, rs.getString(1),
                                rs.getString(2), resolution, year, rs.getInt(3), rs.getFloat(5), rs.getFloat(6));
                        list.add(a);
                    }
                logedUser.setUsed_quota(logedUser.getUsed_quota() + 1);
                stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                        + logedUser.getUsername() + "';");
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/Login", method = RequestMethod.POST)
    public ResponseEntity<Token> LoginUser(@RequestParam("username") String username,
            @RequestParam("password") String password)
            throws SQLException, FileNotFoundException, UnsupportedEncodingException {

        if (logedUser != null)
            return new ResponseEntity<Token>(new Token("user already loged in"), HttpStatus.BAD_REQUEST);
        Token token = new Token("token");
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                "root", "root");
        if (conn == null)
            System.out.println("Connection problem");

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select quotas,used_quotas,datetimeaccess from users where username = '"
                + username + "' and password = md5('" + password + "');");

        if (rs.next()) {
            token.setToken(generateString());
            logedUser = new LogedUser(username, token.getToken(), rs.getInt(1), rs.getInt(2), rs.getDate(3));
            stmt.executeUpdate("update users set used_quotas=" + logedUser.getUsed_quota() + " where username='"
                    + logedUser.getUsername() + "';");
            stmt.executeUpdate("update users set datetimeaccess='" + logedUser.getDatetime() + "' where username='"
                    + logedUser.getUsername() + "';");
            conn.close();
            return new ResponseEntity<Token>(token, HttpStatus.OK);
        } else {
            conn.close();
            return new ResponseEntity<Token>(new Token("user not found"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(path = "/Logout", method = RequestMethod.POST)
    public ResponseEntity<Status> Logout(@RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token)
            throws FileNotFoundException, SQLException {

        if (logedUser != null) {
            if (logedUser.getToken().equals(token)) {
                logedUser = null;
                return new ResponseEntity<Status>(new Status("Logged Out"), HttpStatus.OK);
            } else
                return new ResponseEntity<Status>(new Status("user not found"), HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<Status>(new Status("user not found"), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(path = "/Admin/users", method = RequestMethod.POST)
    public ResponseEntity<Void> createNewUser(@RequestParam("username") String username,
            @RequestParam("password") String password, @RequestParam("email") String email,
            @RequestParam("quota") int quota,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) throws SQLException {

        if ((logedUser == null) || ((logedUser.getToken().equals(token)) && !(logedUser.isAdmin())))
            return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
        else {

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            int curr_year = Integer.parseInt(dateFormat.format(date).substring(0, 4));
            int curr_month = Integer.parseInt(dateFormat.format(date).substring(5, 7));
            int curr_day = Integer.parseInt(dateFormat.format(date).substring(8, 10));
            String datetime = curr_year + "/" + curr_month + "/" + curr_day;

            NewUser newusr = new NewUser(username, email, quota, 0, datetime);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                    "root", "root");
            if (conn == null)
                System.out.println("Connection problem");

            String query = "insert into users (Username, Password, Email, Quotas, DateTimeAccess) values (?, md5(?), ?, ?, curdate());";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, newusr.getUsername());
            preparedStmt.setString(2, password);
            preparedStmt.setString(3, email);
            preparedStmt.setInt(4, newusr.getQuota());
            preparedStmt.execute();
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/Admin/users/{username}", method = RequestMethod.PUT)
    public ResponseEntity<Void> modUser(@PathVariable("username") String username, @RequestBody String parameters,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) throws SQLException {

        if ((logedUser == null) || ((logedUser.getToken().equals(token)) && !(logedUser.isAdmin())))
            return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
        else {
            List<String> params = Arrays.asList(parameters.split(","));
            String password = params.get(0);
            String email = params.get(1);
            int quota = Integer.parseInt(params.get(2));
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                    "root", "root");
            if (conn == null)
                System.out.println("Connection problem");

            Statement stmt = conn.createStatement();
            String query = "update users set Password=md5('" + password + "'),Email='" + email + "',Quotas='" + quota
                    + "'where Username='" + username + "';";
            stmt.executeUpdate(query);
            stmt.close();
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/Admin/users/{username}", method = RequestMethod.GET)
    public ResponseEntity<NewUser> userstatus(@PathVariable("username") String username,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") String token) throws SQLException {

        if ((logedUser == null) || ((logedUser.getToken().equals(token)) && !(logedUser.isAdmin())))
            return new ResponseEntity<NewUser>(new NewUser(" ", " ", 1, 2, " "), HttpStatus.UNAUTHORIZED);
        else {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                    "root", "root");
            if (conn == null)
                System.out.println("Connection problem");

            ResultSet rst;
            Statement stmt = conn.createStatement();
            String query = "select  Username, Email, Quotas, Used_Quotas, DateTimeAccess from users where Username='"
                    + username + "';";
            rst = stmt.executeQuery(query);
            if (rst.next()) {
                NewUser user = new NewUser(username, rst.getString(2), rst.getInt(3), rst.getInt(4), rst.getString(5));
                stmt.close();
                return new ResponseEntity<NewUser>(user, HttpStatus.OK);
            } else {
                stmt.close();
                return new ResponseEntity<NewUser>(new NewUser("", "", 0, 0, ""), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @RequestMapping(path = "/Admin/{dataset}", method = RequestMethod.POST)
    public ResponseEntity<String> newdata(@PathVariable("dataset") final String dataset,
            @RequestPart("fileUpload") MultipartFile file,
            @RequestHeader(value = "X-OBSERVATORY-AUTH", defaultValue = " ") final String token)
            throws SQLException, IOException {

        if ((logedUser == null) || ((logedUser.getToken().equals(token)) && !(logedUser.isAdmin())))
            return new ResponseEntity<String>("error", HttpStatus.UNAUTHORIZED);
        else {
            File saveFile = new File("uploaded_file2.csv");

            // opens input stream of the request for reading data
            InputStream inputStream = file.getInputStream();

            // opens an output stream for writing file
            FileOutputStream outputStream = new FileOutputStream(saveFile);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            // System.out.println("File written to: " + saveFile.getAbsolutePath());

            final Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&allowLoadLocalInfile=true",
                    "root", "root");
            if (conn == null)
                System.out.println("Connection problem");

            int totalRecordsInFile = 0;
            int totalRecordsImported = 0;
            int totalRecordsInDatabase = 0;

            Scanner sc = new Scanner (new FileReader("uploaded_file2.csv"));
            PrintWriter pw = new PrintWriter("uploaded_file.csv");
            String line;
            while ((line = sc.nextLine()) != null) {
                line = line.replace(" +00:00", "");
                line = line.replace("NULL", "");
                if (!sc.hasNextLine()){
                    pw.print(line);
                    break;
                }
                pw.println(line);
                totalRecordsInFile++;
            }

            pw.close();
            sc.close();

            saveFile.delete();
            saveFile = new File("uploaded_file.csv");

            Statement stmt = conn.createStatement();
            String query = "load data local infile 'uploaded_file.csv' into table " + dataset
                    + " fields terminated by ';' enclosed by '' lines terminated by '\r\n' ignore 1 lines;";
            totalRecordsImported = stmt.executeUpdate(query);

            ResultSet rs = stmt.executeQuery("select count(id) from " + dataset + ";");
            rs.next();
            totalRecordsInDatabase = rs.getInt(1);
            final String result = "\nTotal Records In File: " + totalRecordsInFile + "\nTotal Records Imported: "
                    + totalRecordsImported + "\nTotal Records in Database: " + totalRecordsInDatabase;
            return new ResponseEntity<String>(result, HttpStatus.OK);
        }
    }

    @RequestMapping(path = "/HealthCheck")
    public Status getHealthCheck() throws SQLException {
        Status status;
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                "root", "root");
        if (conn == null) {
            System.out.println("Connection problem");
            status = new Status("Connection problem");
        } else
            status = new Status("OK");
        return status;
    }

    @RequestMapping(path = "/Reset")
    public Status Reset() throws SQLException {
        Status status;
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                "root", "root");
        if (conn == null) {
            System.out.println("Connection problem");
            // status = new Status("Connection problem");
        }

        Statement stmt = conn.createStatement();
        String query1 = "Delete from users where username!='admin';";
        String query2 = "Delete from actualtotalload ;";
        String query3 = "Delete from aggregatedgenerationpertype;";
        String query4 = "Delete from dayaheadtotalloadforecast ;";

        stmt.executeUpdate(query1);
        stmt.executeUpdate(query2);
        stmt.executeUpdate(query3);
        stmt.executeUpdate(query4);

        stmt.close();

        status = new Status("OK");
        return status;
    }
}
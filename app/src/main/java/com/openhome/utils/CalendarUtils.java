package com.openhome.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Bhargav on 4/25/2016.
 */
public class CalendarUtils {

    public static Intent addEventToCalendar(Calendar beginTime, Calendar endTime, String location) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", beginTime.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("endTime", endTime.getTimeInMillis());
        intent.putExtra("title", "Open Home");
        intent.putExtra("description", "You've booked to view open home");
        intent.putExtra("location", location);
        return intent;
    }

    public static Calendar getCalendarFromViewingTime(String dateTime) {
        Calendar beginTime = Calendar.getInstance();
        String dateStr = dateTime.split("::::")[0];
        String timeStr = dateTime.split("::::")[1];
        int year = Integer.parseInt(dateStr.split("-")[2]);
        int month = Integer.parseInt(dateStr.split("-")[1]);
        int date = Integer.parseInt(dateStr.split("-")[0]);

        String startTimeStr = timeStr.split("-")[0].trim();
        String endTimeStr = timeStr.split("-")[1].trim();

        int startHour = Integer.parseInt(startTimeStr.split(" ")[0].split(":")[0]);
        int startMinute = Integer.parseInt(startTimeStr.split(" ")[0].split(":")[1]);

        beginTime.set(year, month, date, (startHour <= 12) ? startHour : startHour + 12, startMinute, 0);
        return beginTime;
    }


    public static String addCalendarEvent(Context context, String calendarId,String location, String title, long startTime,
                                   long endTime, int allDay) {
        ContentValues event = new ContentValues();
        event.put("_id", calendarId); // "" for insert
        event.put("title", title);
        event.put("description", "");
        event.put("eventLocation", location);
        event.put("allDay", allDay);
        event.put("eventStatus", 1);
        //event.put("transparency", 0);
        event.put("dtstart", startTime);
        event.put("dtend", endTime);

        ContentResolver contentResolver = context.getContentResolver();
        Uri eventsUri = Uri.parse("content://com.android.calendar/calendars");
        Uri url = contentResolver.insert(eventsUri, event);
        String ret = url.toString();
        return ret;
    }

    public static void addCalendarContractEvents(String title, String description, String calendarID,String location, long startMillis, long endMillis, ContentResolver cr){

        ContentValues values = new ContentValues();
        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, calendarID);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

// Retrieve ID for new event
        String eventID = uri.getLastPathSegment();
    }

    public static String getCalendar(Context c) {

        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = c.getContentResolver();
        Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);
        String calName = null;
        String calID = null;
        if (managedCursor.moveToFirst()) {

            int cont = 0;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                if (cont == 0) {
                    calName = managedCursor.getString(nameCol);
                    calID = managedCursor.getString(idCol);
                    cont++;
                }
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }
        return calID;
    }
}

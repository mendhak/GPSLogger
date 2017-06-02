/*
 * Copyright (C) 2016 mendhak
 *
 * This file is part of GPSLogger for Android.
 *
 * GPSLogger for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * GPSLogger for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mendhak.gpslogger.loggers.gpx;

import android.location.Location;
import android.os.Bundle;

import com.mendhak.gpslogger.BuildConfig;
import com.mendhak.gpslogger.SensorDataObject;
import com.mendhak.gpslogger.common.BundleConstants;
import com.mendhak.gpslogger.common.Maths;
import com.mendhak.gpslogger.common.RejectionHandler;
import com.mendhak.gpslogger.common.Strings;
import com.mendhak.gpslogger.common.slf4j.Logs;
import com.mendhak.gpslogger.loggers.FileLogger;
import com.mendhak.gpslogger.loggers.Files;
import org.slf4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Gpx10FileLogger implements FileLogger {
    protected final static Object lock = new Object();

    private final static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(128), new RejectionHandler());
    private File gpxFile = null;
    private final boolean addNewTrackSegment;
    protected final String name = "GPX";

    public Gpx10FileLogger(File gpxFile, boolean addNewTrackSegment) {
        this.gpxFile = gpxFile;
        this.addNewTrackSegment = addNewTrackSegment;
    }


    public void write(Location loc) throws Exception {
        long time = loc.getTime();
        if (time <= 0) {
            time = System.currentTimeMillis();
        }
        String dateTimeString = Strings.getIsoDateTime(new Date(time));

        Gpx10WriteHandler writeHandler = new Gpx10WriteHandler(dateTimeString, gpxFile, loc, addNewTrackSegment);
        EXECUTOR.execute(writeHandler);
    }

    public void annotate(String description, Location loc) throws Exception {
        
        description = Strings.cleanDescription(description);
        
        long time = loc.getTime();
        if (time <= 0) {
            time = System.currentTimeMillis();
        }
        String dateTimeString = Strings.getIsoDateTime(new Date(time));

        Gpx10AnnotateHandler annotateHandler = new Gpx10AnnotateHandler(description, gpxFile, loc, dateTimeString);
        EXECUTOR.execute(annotateHandler);
    }

    @Override
    public String getName() {
        return name;
    }


}

class Gpx10AnnotateHandler implements Runnable {
    private static final Logger LOG = Logs.of(Gpx10AnnotateHandler.class);
    String description;
    File gpxFile;
    Location loc;
    String dateTimeString;

    public Gpx10AnnotateHandler(String description, File gpxFile, Location loc, String dateTimeString) {
        this.description = description;
        this.gpxFile = gpxFile;
        this.loc = loc;
        this.dateTimeString = dateTimeString;
    }

    @Override
    public void run() {

        synchronized (Gpx10FileLogger.lock) {
            if (!gpxFile.exists()) {
                return;
            }

            if (!gpxFile.exists()) {
                return;
            }



            String wpt = getWaypointXml(loc, dateTimeString, description);

            try {

                //write to a temp file, delete original file, move temp to original
                File gpxTempFile = new File(gpxFile.getAbsolutePath() + ".tmp");

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(gpxFile));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(gpxTempFile));

                int written = 0;
                int readSize;
                byte[] buffer = new byte[Gpx10WriteHandler.INITIAL_XML_LENGTH];
                while ((readSize = bis.read(buffer)) > 0) {
                    bos.write(buffer, 0, readSize);
                    written += readSize;

                    System.out.println(written);

                    if (written == Gpx10WriteHandler.INITIAL_XML_LENGTH) {
                        bos.write(wpt.getBytes());
                        buffer = new byte[20480];
                    }

                }

                bis.close();
                bos.close();

                gpxFile.delete();
                gpxTempFile.renameTo(gpxFile);

                LOG.debug("Finished annotation to GPX10 File");
            } catch (Exception e) {
                LOG.error("Gpx10FileLogger.annotate", e);
            }

        }
    }

    String getWaypointXml(Location loc, String dateTimeString, String description) {

        StringBuilder waypoint = new StringBuilder();

        waypoint.append("\n<wpt lat=\"")
                .append(String.valueOf(loc.getLatitude()))
                .append("\" lon=\"")
                .append(String.valueOf(loc.getLongitude()))
                .append("\">");

        if (loc.hasAltitude()) {
            waypoint.append("<ele>").append(String.valueOf(loc.getAltitude())).append("</ele>");
        }

        waypoint.append("<time>").append(dateTimeString).append("</time>\n");
        waypoint.append("<name>").append(description).append("</name>");

        waypoint.append("<src>").append(loc.getProvider()).append("</src>");
        waypoint.append("</wpt>\n");

        return waypoint.toString();
    }
}


class Gpx10WriteHandler implements Runnable {
    private static final Logger LOG = Logs.of(Gpx10WriteHandler.class);
    String dateTimeString;
    Location loc;
    private File gpxFile = null;
    private boolean addNewTrackSegment;
    static final int INITIAL_XML_LENGTH = 343;

    public Gpx10WriteHandler(String dateTimeString, File gpxFile, Location loc, boolean addNewTrackSegment) {
        this.dateTimeString = dateTimeString;
        this.addNewTrackSegment = addNewTrackSegment;
        this.gpxFile = gpxFile;
        this.loc = loc;
    }

    @Override
    public void run() {
        synchronized (Gpx10FileLogger.lock) {

            try {
                if (!gpxFile.exists()) {
                    gpxFile.createNewFile();

                    FileOutputStream initialWriter = new FileOutputStream(gpxFile, true);
                    BufferedOutputStream initialOutput = new BufferedOutputStream(initialWriter);

                    initialOutput.write(getBeginningXml(dateTimeString).getBytes());
                    initialOutput.write("<trk>".getBytes());
                    initialOutput.write(getEndXml().getBytes());
                    initialOutput.flush();
                    initialOutput.close();

                    //New file, so new segment.
                    addNewTrackSegment = true;
                }

                int offsetFromEnd = (addNewTrackSegment) ? getEndXml().length() : getEndXmlWithSegment().length();
                long startPosition = gpxFile.length() - offsetFromEnd;
                String trackPoint = getTrackPointXml(loc, dateTimeString);

                RandomAccessFile raf = new RandomAccessFile(gpxFile, "rw");
                raf.seek(startPosition);
                raf.write(trackPoint.getBytes());
                raf.close();
                Files.addToMediaDatabase(gpxFile, "text/plain");
                LOG.debug("Finished writing to GPX10 file");

            } catch (Exception e) {
                LOG.error("Gpx10FileLogger.write", e);
            }

        }

    }

    String getBeginningXml(String dateTimeString) {
        StringBuilder initialXml = new StringBuilder();
        initialXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        initialXml.append("<gpx version=\"1.0\" creator=\"GPSLogger " + BuildConfig.VERSION_CODE + " - http://gpslogger.mendhak.com/\" ");
        initialXml.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        initialXml.append("xmlns=\"http://www.topografix.com/GPX/1/0\" ");
        initialXml.append("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 ");
        initialXml.append("http://www.topografix.com/GPX/1/0/gpx.xsd\">");
        initialXml.append("<time>").append(dateTimeString).append("</time>\n");
        return initialXml.toString();
    }

    String getEndXml() {
        return "</trk></gpx>";
    }

    String getEndXmlWithSegment() {
        return "</trkseg></trk></gpx>";
    }

    String getTrackPointXml(Location loc, String dateTimeString) {

        StringBuilder track = new StringBuilder();

        if (addNewTrackSegment) {
            track.append("<trkseg>\n");
        }

        track.append("<trkpt lat=\"")
                .append(String.valueOf(loc.getLatitude()))
                .append("\" lon=\"")
                .append(String.valueOf(loc.getLongitude()))
                .append("\">");

        if (loc.hasAltitude()) {
            track.append("<ele>").append(String.valueOf(loc.getAltitude())).append("</ele>");
        }

        track.append("<time>").append(dateTimeString).append("</time>");

        if (loc.hasBearing()) {
            track.append("<course>").append(String.valueOf(loc.getBearing())).append("</course>");
        }

        if (loc.hasSpeed()) {
            track.append("<speed>").append(String.valueOf(loc.getSpeed())).append("</speed>");
        }

        if (loc.getExtras() != null) {
            String geoidheight = loc.getExtras().getString(BundleConstants.GEOIDHEIGHT);

            if (!Strings.isNullOrEmpty(geoidheight)) {
                track.append("<geoidheight>").append(geoidheight).append("</geoidheight>");
            }
        }

        track.append("<src>").append(loc.getProvider()).append("</src>");


        if (loc.getExtras() != null) {

            int sat = Maths.getBundledSatelliteCount(loc);

            if (sat > 0) {
                track.append("<sat>").append(String.valueOf(sat)).append("</sat>");
            }


            String hdop = loc.getExtras().getString(BundleConstants.HDOP);
            String pdop = loc.getExtras().getString(BundleConstants.PDOP);
            String vdop = loc.getExtras().getString(BundleConstants.VDOP);
            String ageofdgpsdata = loc.getExtras().getString(BundleConstants.AGEOFDGPSDATA);
            String dgpsid = loc.getExtras().getString(BundleConstants.DGPSID);

            if (!Strings.isNullOrEmpty(hdop)) {
                track.append("<hdop>").append(hdop).append("</hdop>");
            }

            if (!Strings.isNullOrEmpty(vdop)) {
                track.append("<vdop>").append(vdop).append("</vdop>");
            }

            if (!Strings.isNullOrEmpty(pdop)) {
                track.append("<pdop>").append(pdop).append("</pdop>");
            }

            if (!Strings.isNullOrEmpty(ageofdgpsdata)) {
                track.append("<ageofdgpsdata>").append(ageofdgpsdata).append("</ageofdgpsdata>");
            }

            if (!Strings.isNullOrEmpty(dgpsid)) {
                track.append("<dgpsid>").append(dgpsid).append("</dgpsid>");
            }
        }

        if (loc.getExtras() != null) {
            //Render our sensor data extensions here
            Bundle extras = loc.getExtras();
            StringBuilder accelString = new StringBuilder();
            StringBuilder compassString = new StringBuilder();
            StringBuilder orientationString = new StringBuilder();

            ArrayList<SensorDataObject.Accelerometer> accelerometer = (ArrayList<SensorDataObject.Accelerometer>) extras.getSerializable(BundleConstants.ACCELEROMETER);
            if (accelerometer != null && accelerometer.size() > 0) {
                accelString.append("<sensordata:accelerometer>");
                for (SensorDataObject.Accelerometer accel : accelerometer) {
                    accelString.append(String.format("(%1$.3f;%1$.3f;%1$.3f),", accel.x, accel.y, accel.z));
                }
                accelString.append("</sensordata:accelerometer>\n");
            }

            ArrayList<SensorDataObject.Compass> compass = (ArrayList<SensorDataObject.Compass>) extras.getSerializable(BundleConstants.COMPASS);
            if (compass != null && compass.size() > 0) {
                compassString.append("<sensordata:compass>");
                for (SensorDataObject.Compass comp : compass) {
                    compassString.append(String.format("%1$.3f;", comp.deg));
                }
                compassString.append("</sensordata:compass>\n");
            }

            ArrayList<SensorDataObject.Orientation> orientation = (ArrayList<SensorDataObject.Orientation>) extras.getSerializable(BundleConstants.ORIENTATION);
            if (orientation != null && orientation.size() > 0) {
                orientationString.append("<sensordata:orientation>");
                for (SensorDataObject.Orientation orient : orientation) {
                    orientationString.append(String.format("(%1$.3f;%1$.3f;%1$.3f),", orient.azimuth, orient.pitch, orient.roll));
                }
                orientationString.append("</sensordata:orientation>\n");
            }

            if (accelString.length() > 0 || compassString.length() > 0 || orientationString.length() > 0) {
                /*
                    GPX10 does not support the extensions container of GPX11.
                    However it does support custom "private extensions", this writer is GPX10 compliant, thus omit the extension container.
                    See also: http://www.topografix.com/GPX/1/0/gpx.xsd and http://www.topografix.com/GPX/1/1/gpx.xsd
                 */
                //track.append("<extensions>\n");
                track.append(accelString);
                track.append(compassString);
                track.append(orientationString);
                //track.append("</extensions>");
            }
        }


        track.append("</trkpt>\n");

        track.append("</trkseg></trk></gpx>");

        return track.toString();
    }
}



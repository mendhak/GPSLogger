/*
*    This file is part of GPSLogger for Android.
*
*    GPSLogger for Android is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 2 of the License, or
*    (at your option) any later version.
*
*    GPSLogger for Android is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.mendhak.gpslogger;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

class GeneralLocationListener implements LocationListener, GpsStatus.Listener
{

    private static GpsLoggingService loggingService;
    private static final org.slf4j.Logger tracer = LoggerFactory.getLogger(GeneralLocationListener.class.getSimpleName());

    GeneralLocationListener(GpsLoggingService activity)
    {
        tracer.debug("GeneralLocationListener constructor");
        loggingService = activity;
    }

    /**
     * Event raised when a new fix is received.
     */
    public void onLocationChanged(Location loc)
    {

        try
        {
            if (loc != null)
            {
                tracer.debug("GeneralLocationListener.onLocationChanged");
                loggingService.OnLocationChanged(loc);
            }

        }
        catch (Exception ex)
        {
            tracer.error("GeneralLocationListener.onLocationChanged", ex);
            loggingService.SetStatus(ex.getMessage());
        }

    }

    public void onProviderDisabled(String provider)
    {
        tracer.info("Provider disabled: " + provider);
        loggingService.RestartGpsManagers();
    }

    public void onProviderEnabled(String provider)
    {

        tracer.info("Provider enabled: " + provider);
        loggingService.RestartGpsManagers();
    }

    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        if (status == LocationProvider.OUT_OF_SERVICE)
        {
            tracer.debug(provider + " is out of service");
            loggingService.StopManagerAndResetAlarm();
        }

        if (status == LocationProvider.AVAILABLE)
        {
            tracer.debug(provider + " is available");
        }

        if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
        {
            tracer.debug(provider + " is temporarily unavailable");
            loggingService.StopManagerAndResetAlarm();
        }
    }

    public void onGpsStatusChanged(int event)
    {

        switch (event)
        {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                tracer.debug("GPS Event First Fix");
                loggingService.SetStatus(loggingService.getString(R.string.fix_obtained));
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                GpsStatus status = loggingService.gpsLocationManager.getGpsStatus(null);

                int maxSatellites = status.getMaxSatellites();

                Iterator<GpsSatellite> it = status.getSatellites().iterator();
                int count = 0;

                while (it.hasNext() && count <= maxSatellites)
                {
                    it.next();
                    count++;
                }

                tracer.debug(String.valueOf(count) + " satellites");
                loggingService.SetSatelliteInfo(count);
                break;

            case GpsStatus.GPS_EVENT_STARTED:
                tracer.info("GPS started, waiting for fix");
                loggingService.SetStatus(loggingService.getString(R.string.started_waiting));
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                tracer.info("GPS Event Stopped");
                loggingService.SetStatus(loggingService.getString(R.string.gps_stopped));
                break;

        }
    }

}

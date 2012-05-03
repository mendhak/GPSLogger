package com.mendhak.gpslogger.common;

import android.app.Application;

public class AppSettings extends Application
{
    // ---------------------------------------------------
    // User Preferences
    // ---------------------------------------------------
    private static boolean useImperial = false;
    private static boolean newFileOnceADay;
    private static boolean preferCellTower;
    private static boolean useSatelliteTime;
    private static boolean logToKml;
    private static boolean logToGpx;
    private static boolean logToPlainText;
    private static boolean showInNotificationBar;
    private static int minimumSeconds;
    private static String newFileCreation;
    private static Float autoEmailDelay = 0f;
    private static boolean autoEmailEnabled = false;
    private static String smtpServer;
    private static String smtpPort;
    private static String smtpUsername;
    private static String smtpPassword;
    private static String smtpFrom;
    private static String autoEmailTargets;
    private static boolean smtpSsl;
    private static boolean debugToFile;
    private static int minimumDistance;
    private static boolean shouldSendZipFile;


    /**
     * @return the useImperial
     */
    public static boolean shouldUseImperial()
    {
        return useImperial;
    }

    /**
     * @param useImperial the useImperial to set
     */
    static void setUseImperial(boolean useImperial)
    {
        AppSettings.useImperial = useImperial;
    }

    /**
     * @return the newFileOnceADay
     */
    public static boolean shouldCreateNewFileOnceADay()
    {
        return newFileOnceADay;
    }

    /**
     * @param newFileOnceADay the newFileOnceADay to set
     */
    static void setNewFileOnceADay(boolean newFileOnceADay)
    {
        AppSettings.newFileOnceADay = newFileOnceADay;
    }

    /**
     * @return the preferCellTower
     */
    public static boolean shouldPreferCellTower()
    {
        return preferCellTower;
    }

    /**
     * @param preferCellTower the preferCellTower to set
     */
    static void setPreferCellTower(boolean preferCellTower)
    {
        AppSettings.preferCellTower = preferCellTower;
    }

    /**
     * @return the useSatelliteTime
     */
    public static boolean shouldUseSatelliteTime()
    {
        return useSatelliteTime;
    }

    /**
     * @param useSatelliteTime the useSatelliteTime to set
     */
    static void setUseSatelliteTime(boolean useSatelliteTime)
    {
        AppSettings.useSatelliteTime = useSatelliteTime;
    }

    /**
     * @return the logToKml
     */
    public static boolean shouldLogToKml()
    {
        return logToKml;
    }

    /**
     * @param logToKml the logToKml to set
     */
    static void setLogToKml(boolean logToKml)
    {
        AppSettings.logToKml = logToKml;
    }

    /**
     * @return the logToGpx
     */
    public static boolean shouldLogToGpx()
    {
        return logToGpx;
    }

    /**
     * @param logToGpx the logToGpx to set
     */
    static void setLogToGpx(boolean logToGpx)
    {
        AppSettings.logToGpx = logToGpx;
    }

    public static boolean shouldLogToPlainText() {
	return logToPlainText;
    }

    static void setLogToPlainText(boolean logToPlainText) {
	AppSettings.logToPlainText = logToPlainText;
    }

    /**
     * @return the showInNotificationBar
     */
    public static boolean shouldShowInNotificationBar()
    {
        return showInNotificationBar;
    }

    /**
     * @param showInNotificationBar the showInNotificationBar to set
     */
    static void setShowInNotificationBar(boolean showInNotificationBar)
    {
        AppSettings.showInNotificationBar = showInNotificationBar;
    }


    /**
     * @return the minimumSeconds
     */
    public static int getMinimumSeconds()
    {
        return minimumSeconds;
    }

    /**
     * @param minimumSeconds the minimumSeconds to set
     */
    static void setMinimumSeconds(int minimumSeconds)
    {
        AppSettings.minimumSeconds = minimumSeconds;
    }


    /**
     * @return the minimumDistance
     */
    public static int getMinimumDistanceInMeters()
    {
        return minimumDistance;
    }

    /**
     * @param minimumDistance the minimumDistance to set
     */
    static void setMinimumDistanceInMeters(int minimumDistance)
    {
        AppSettings.minimumDistance = minimumDistance;
    }


    /**
     * @return the newFileCreation
     */
    static String getNewFileCreation()
    {
        return newFileCreation;
    }

    /**
     * @param newFileCreation the newFileCreation to set
     */
    static void setNewFileCreation(String newFileCreation)
    {
        AppSettings.newFileCreation = newFileCreation;
    }


    /**
     * @return the autoEmailDelay
     */
    public static Float getAutoEmailDelay()
    {
        if (autoEmailDelay >= 8f)
        {
            return 8f;
        }
        else
        {
            return autoEmailDelay;
        }


    }

    /**
     * @param autoEmailDelay the autoEmailDelay to set
     */
    static void setAutoEmailDelay(Float autoEmailDelay)
    {

        if (autoEmailDelay >= 8f)
        {
            AppSettings.autoEmailDelay = 8f;
        }
        else
        {
            AppSettings.autoEmailDelay = autoEmailDelay;
        }


    }

    /**
     * @return the autoEmailEnabled
     */
    public static boolean isAutoEmailEnabled()
    {
        return autoEmailEnabled;
    }

    /**
     * @param autoEmailEnabled the autoEmailEnabled to set
     */
    static void setAutoEmailEnabled(boolean autoEmailEnabled)
    {
        AppSettings.autoEmailEnabled = autoEmailEnabled;
    }


    static void setSmtpServer(String smtpServer)
    {
        AppSettings.smtpServer = smtpServer;
    }

    public static String getSmtpServer()
    {
        return smtpServer;
    }

    static void setSmtpPort(String smtpPort)
    {
        AppSettings.smtpPort = smtpPort;
    }

    public static String getSmtpPort()
    {
        return smtpPort;
    }

    static void setSmtpUsername(String smtpUsername)
    {
        AppSettings.smtpUsername = smtpUsername;
    }

    public static String getSmtpUsername()
    {
        return smtpUsername;
    }


    static void setSmtpPassword(String smtpPassword)
    {
        AppSettings.smtpPassword = smtpPassword;
    }

    public static String getSmtpPassword()
    {
        return smtpPassword;
    }

    static void setSmtpSsl(boolean smtpSsl)
    {
        AppSettings.smtpSsl = smtpSsl;
    }

    public static boolean isSmtpSsl()
    {
        return smtpSsl;
    }

    static void setAutoEmailTargets(String autoEmailTargets)
    {
        AppSettings.autoEmailTargets = autoEmailTargets;
    }

    public static String getAutoEmailTargets()
    {
        return autoEmailTargets;
    }

    public static boolean isDebugToFile()
    {
        return debugToFile;
    }

    public static void setDebugToFile(boolean debugToFile)
    {
        AppSettings.debugToFile = debugToFile;
    }


    public static boolean shouldSendZipFile()
    {
        return shouldSendZipFile;
    }

    public static void setShouldSendZipFile(boolean shouldSendZipFile)
    {
        AppSettings.shouldSendZipFile = shouldSendZipFile;
    }

    private static String getSmtpFrom()
    {
        return smtpFrom;
    }

    public static void setSmtpFrom(String smtpFrom)
    {
        AppSettings.smtpFrom = smtpFrom;
    }

    /**
     * Returns the from value to use when sending an email
     *
     * @return
     */
    public static String getSenderAddress()
    {
        if (getSmtpFrom() != null && getSmtpFrom().length() > 0)
        {
            return getSmtpFrom();
        }

        return getSmtpUsername();
    }
}

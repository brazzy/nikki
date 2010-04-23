package de.brazzy.nikki.util;
/*
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import java.util.List;

import javax.swing.SwingWorker;

import org.joda.time.DateTimeZone;

import de.brazzy.nikki.model.Directory;

/**
 * Scans directory for new image and GPS files
 *
 * @author Michael Borgwardt
 */
public class ScanWorker extends SwingWorker<Void, Void>
{
    private Dialogs dialogs;
    private Directory dir;
    private TimezoneFinder finder;

    private DateTimeZone zone = null;
    private Object zoneLock = new Object();
    private Thread thread;

    /**
     * @param dir directory to scan
     * @param dialogs used to ask the timezone from the user
     * @param finder for assigning timezones to waypoints
     */
    public ScanWorker(Directory dir, Dialogs dialogs, TimezoneFinder finder)
    {
        super();
        this.dir = dir;
        this.dialogs = dialogs;
        this.finder = finder;
    }

    @Override
    protected Void doInBackground() throws Exception
    {
        thread = Thread.currentThread();
        if(dir.scan(this, null, finder)==ScanResult.TIMEZONE_MISSING)
        {
            try
            {
                synchronized(zoneLock)
                {
                    publish();
                    while(zone==null)
                    {
                        zoneLock.wait();
                    }
                }
                dir.scan(this, zone, finder);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void process(List<Void> chunks)
    {
        synchronized(zoneLock)
        {
            zone = dialogs.askTimeZone(DateTimeZone.getDefault());
            if(zone!=null)
            {
                zoneLock.notifyAll();
            }
            else
            {
                thread.interrupt();
            }
        }
    }
}

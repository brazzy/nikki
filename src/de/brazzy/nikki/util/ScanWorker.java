package de.brazzy.nikki.util;

/*   
 *   Copyright 2010 Michael Borgwardt
 *   Part of the Nikki Photo GPS diary:  http://www.brazzy.de/nikki
 *
 *  Nikki is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Nikki is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Nikki.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;

import de.brazzy.nikki.model.Directory;

/**
 * Scans directory for new image and GPS files
 * 
 * @author Michael Borgwardt
 */
public class ScanWorker extends SwingWorker<Void, Void> {
    private Dialogs dialogs;
    private Directory dir;
    private DirectoryScanner scanner;

    private DateTimeZone zone = null;
    private Object zoneLock = new Object();
    private Thread thread;

    /**
     * @param dir
     *            directory to scan
     * @param dialogs
     *            used to ask the timezone from the user
     * @param scanner
     *            does the scanning
     */
    public ScanWorker(Directory dir, Dialogs dialogs, DirectoryScanner scanner) {
        super();
        this.dir = dir;
        this.dialogs = dialogs;
        this.scanner = scanner;
    }

    @Override
    protected Void doInBackground() throws Exception {
        thread = Thread.currentThread();
        if (scanner.scan(dir, this) == ScanResult.TIMEZONE_MISSING) {
            try {
                synchronized (zoneLock) {
                    publish();
                    while (zone == null) {
                        zoneLock.wait();
                    }
                }
                scanner.setZone(zone);
                scanner.scan(dir, this);
            } catch (InterruptedException e) {
                // intentional - happens when time zone dialog was aborted
            } finally {
                scanner.setZone(null);
            }
        }
        return null;
    }

    @Override
    protected void process(List<Void> chunks) {
        synchronized (zoneLock) {
            zone = dialogs.askTimeZone(DateTimeZone.getDefault());
            if (zone != null) {
                zoneLock.notifyAll();
            } else {
                thread.interrupt();
            }
        }
    }
}

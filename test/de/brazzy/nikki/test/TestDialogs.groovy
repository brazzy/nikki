package de.brazzy.nikki.test
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

import java.lang.Thread.UncaughtExceptionHandler;

import de.brazzy.nikki.util.ConfirmResult;
import de.brazzy.nikki.util.Dialogs
import de.brazzy.nikki.util.NikkiWorker;
import de.brazzy.nikki.view.NikkiFrame;

import javax.swing.SwingWorker


import org.joda.time.DateTimeZone
import org.joda.time.ReadablePeriod

/**
 * @author Michael Borgwardt
 */
class TestDialogs extends Dialogs{
    def queue = []
    def opened
    
    @Override
    public File askDirectory(File startDir) {
        return queue.remove(0)
    }
    
    @Override
    public File askFile(File dir, String defaultFileName) {
        return queue.remove(0)
    }
    
    @Override
    public ReadablePeriod askOffset() {
        return queue.remove(0)
    }
    
    @Override
    public DateTimeZone askTimeZone(DateTimeZone defaultZone) {
        return queue.remove(0)
    }
    
    @Override
    public void open(File f) {
        if(opened) {
            throw new IllegalStateException("Already present: "+opened)
        }
        opened = f
    }
    
    @Override
    public void registerWorker(NikkiWorker worker) {
        assert worker.header != null
        worker.doInBackground()
        worker.done()
    }
    
    public boolean isQueueEmpty() {
        return queue.isEmpty()
    }
    
    public void add(stuff) {
        queue.add(stuff)
    }
    
    public File getOpened() {
        def result = opened
        opened = null
        return result
    }
    
    @Override
    public void showAboutBox() {
        queue.remove(0)
    }
    
    @Override
    public ConfirmResult confirm(String message, int optionType) {
        assert queue[0] != null
        queue.remove(0)
    }
    
    @Override
    public void error(String message) {
        assert queue.remove(0) != null
    }    
    
    @Override
    public UncaughtExceptionHandler getExceptionHandler() {
        return {} as UncaughtExceptionHandler
    }
}


package de.brazzy.nikki.test
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

import de.brazzy.nikki.util.Dialogs
import javax.swing.SwingWorker

import junit.framework.AssertionFailedError;

import org.joda.time.DateTimeZone
import org.joda.time.ReadablePeriod

/**
 * @author Michael Borgwardt
 */
class TestDialogs extends Dialogs{
    def queue = []
    def worker
    def opened

    @Override
    public File askDirectory(File startDir)
    {
        return queue.remove(0)
    }

    @Override
    public File askFile(File dir, String defaultFileName)
    {
        return queue.remove(0)
    }

    @Override
    public ReadablePeriod askOffset()
    {
        return queue.remove(0)
    }

    @Override
    public DateTimeZone askTimeZone(DateTimeZone defaultZone)
    {
        return queue.remove(0)
    }

    @Override
    public void open(File f)
    {
        if(opened)
        {
            throw new IllegalStateException("Already present: "+opened)
        }
        opened = f
    }

    @Override
    public void registerWorker(SwingWorker worker)
    {
        if(this.worker)
        {
            this.worker.get()
        }
        this.worker = worker
    }

    public boolean isQueueEmpty()
    {
        return queue.isEmpty()
    }

    public void add(stuff)
    {
        queue.add(stuff)
    }

    public File getOpened()
    {
        def result = opened
        opened = null
        return result
    }

    @Override
    public void showAboutBox()
    {
        queue.remove(0)
    }
}


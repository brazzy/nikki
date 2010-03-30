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

import java.util.zip.ZipOutputStream;

import javax.swing.SwingWorker;

import de.brazzy.nikki.model.Day;

/**
 * Exports data to a KMZ file
 * 
 * @author Michael Borgwardt
 */
public class ExportWorker extends SwingWorker<Void, Void>
{
    private Day day;
    private ZipOutputStream out;
    
    public ExportWorker(Day day, ZipOutputStream out)
    {
        super();
        this.day = day;
        this.out = out;
    }
    
    @Override
    protected Void doInBackground() throws Exception
    {
        day.export(out, this);            
        return null;
    }   

}

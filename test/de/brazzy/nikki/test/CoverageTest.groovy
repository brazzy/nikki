package de.brazzy.nikki.test;

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

import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.swing.event.HyperlinkEvent;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Test;

import de.brazzy.nikki.model.Image;
import de.brazzy.nikki.model.ListDataModel;
import de.brazzy.nikki.model.Waypoint;
import de.brazzy.nikki.util.PropertyComparator;
import de.brazzy.nikki.util.Texts;
import de.brazzy.nikki.view.AboutBox;
import de.brazzy.nikki.view.ImageRenderer;
import de.brazzy.nikki.view.ImageView;


/**
 * Close gaps in test coverage
 * 
 * @author Michael Borgwardt
 */
class CoverageTest {
    @Test
    public void texts(){
        def dummy
        dummy = new Texts()
        dummy = new Texts.Dialogs()
        dummy = new Texts.Dialogs.About()
        dummy = new Texts.Dialogs.GeotagOptions()
        dummy = new Texts.Dialogs.ScanOptions()
        dummy = new Texts.Dialogs.Export()
        dummy = new Texts.Dialogs.Save()
        dummy = new Texts.Dialogs.Scan()
        dummy = new Texts.Image()
        dummy = new Texts.Main()
    }

    @Test
    public void propertyComparator(){
        def dummy1a = new PropertyComparator(propertyName: "a")
        def dummy1b = new PropertyComparator(propertyName: "a")
        def dummy1c = new PropertyComparator(propertyName: "b")
        def dummy2a = new PropertyComparator(propertyName: "a", secondary: "b")
        def dummy2b = new PropertyComparator(propertyName: "a", secondary: "b")
        def dummy2c = new PropertyComparator(propertyName: "a", secondary: "c")
        assert dummy1a == dummy1b
        assert dummy1a != dummy1c
        assert dummy1a != dummy2a
        assert dummy2a == dummy2b
        assert dummy2a != dummy2c

        def set = new HashSet();
        set.add(dummy1a)
        assertEquals(1, set.size())
        set.add(dummy1b)
        assertEquals(1, set.size())
        set.add(dummy1c)
        assertEquals(2, set.size())

        set.add(dummy2a)
        assertEquals(3, set.size())
        set.add(dummy2b)
        assertEquals(3, set.size())
        set.add(dummy2c)
        assertEquals(4, set.size())
    }

    @Test
    public void toStringMethods(){
        def dummy = new Waypoint(timestamp: new DateTime())
        dummy.toString()
        dummy = new Image(fileName: "test")
        dummy.toString()
    }

    @Test
    public void listDataModel(){
        ListDataModel dummy = new ListDataModel<String>()
        dummy.add("a")
        dummy.add("b")
        def itr = dummy.iterator()
        assertEquals("a", itr.next())
        assertEquals("b", itr.next())
        assertFalse(itr.hasNext())
    }

    @Test
    public void aboutBox() {
        Logger.getRootLogger().getAppender("A1").rollOver()
        AboutBox box = new AboutBox();
        assertTrue(box.html.contains("Nikki GPS"))
        assertTrue(box.html.contains("Borgwardt"))
        box.content.fireHyperlinkUpdate(
                new HyperlinkEvent(this, HyperlinkEvent.EventType.ACTIVATED, new URL("http://ßß&about???")));
        File logFile = new File(System.getProperty("user.home")+"/nikki.log")
        assertTrue(GuiTest.logContains("about???"));
    }

    @Test
    public void imageRenderer(){
        TestDialogs td = new TestDialogs();
        ImageRenderer r = new ImageRenderer(td, new Image[1], null)
        Image img = new Image();
        img.thumbnail = new byte[0];
        ImageView result = (ImageView) r.getTableCellRendererComponent(null, img, false, false, 0, 0);
        assertSame(img, result.getValue());
    }
}

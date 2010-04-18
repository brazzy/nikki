package de.brazzy.nikki.test;
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

import java.util.Locale;

import junit.framework.TestSuite;

/**
 * @author Michael Borgwardt
 */
public class Suite extends TestSuite
{
    public static TestSuite suite()
    {
        Locale.setDefault(Locale.ENGLISH);
        
        TestSuite s=new UnitTest();
        
        s.addTest(IntegrationTest.suite());
        s.addTest(UnitTest.suite());
        return s;
    }
}

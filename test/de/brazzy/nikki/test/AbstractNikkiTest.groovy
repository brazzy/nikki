package de.brazzy.nikki.test

import de.brazzy.nikki.Nikki
import de.brazzy.nikki.model.NikkiModel
import de.brazzy.nikki.view.NikkiFrame

/**
 *
 * @author Brazil
 */
abstract class AbstractNikkiTest extends GroovyTestCase
{
    protected TestDialogs dialogs
    protected NikkiModel model
    protected NikkiFrame view

    public void setUp()
    {
        dialogs = new TestDialogs()
        def nikki = new Nikki()
        nikki.build(false, dialogs)
        model = nikki.model
        view = nikki.view
    }

}


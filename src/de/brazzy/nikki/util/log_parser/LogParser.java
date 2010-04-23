package de.brazzy.nikki.util.log_parser;

import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Iterator;

import de.brazzy.nikki.model.Waypoint;

/**
 * A parser for a GPS logger file format.
 * The inherited {@link #accept(java.io.File, String)}
 * method should accept files that this parser can parse
 *
 * @author Michael Borgwardt
 */
public interface LogParser extends FilenameFilter
{
    /**
     * @param input stream to read from
     * @return iterator over all waypoints in the file
     * @throws ParserException when the file's contents are not
     *         parseable. This can also be thrown by the iterator
     */
    public Iterator<Waypoint> parse(InputStream input) throws ParserException;
}

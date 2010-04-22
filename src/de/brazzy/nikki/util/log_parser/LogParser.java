package de.brazzy.nikki.util.log_parser;

import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Iterator;

import de.brazzy.nikki.model.Waypoint;

/**
 * A parser for a GPS logger file format
 *
 * @author Michael Borgwardt
 */
public interface LogParser
{
    /**
     * @return filter that accepts files this parser can parse.
     *         Typically just an instance of {@link ExtensionFilter}
     */
    public FilenameFilter getParseableFileNameFilter();

    /**
     * @param input stream to read from
     * @return iterator over all waypoints in the file
     * @throws ParserException when the file's contents are not
     *         parseable. This can also be thrown by the iterator
     */
    public Iterator<Waypoint> parse(InputStream input) throws ParserException;


}

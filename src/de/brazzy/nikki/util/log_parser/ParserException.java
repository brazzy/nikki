package de.brazzy.nikki.util.log_parser;
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

/**
 * Signals that something went wrong while parsing a GPS log file
 *
 * @author Michael Borgwardt
 */
public class ParserException extends RuntimeException
{
    /**
     * @param message should contain the part of the file that
     *        could not be parsed
     * @param cause exception that happened during parsing
     */
    public ParserException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param message should contain the part of the file that
     *        could not be parsed
     */
    public ParserException(String message)
    {
        super(message);
    }

    /**
     * @param cause exception that happened during parsing
     */
    public ParserException(Throwable cause)
    {
        super(cause);
    }

}

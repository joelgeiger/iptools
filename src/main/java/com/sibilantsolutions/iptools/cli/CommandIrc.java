package com.sibilantsolutions.iptools.cli;

import org.kohsuke.args4j.Option;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "IRC Client" )
public class CommandIrc
{
    public static final String COMMAND_NAME = "irc";

    public final static CommandIrc INSTANCE = new CommandIrc();

    public static final String[] ALIASES = {};

    @Parameter( names = { "-f", "--filename" }, required = true, description = "Where the params at?" )
    @Option( name = "-f", aliases = { "--filename" } )
    public String filename;

    public CommandIrc() {}

    public String getFilename()
    {
        return filename;
    }

}

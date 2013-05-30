package com.sibilantsolutions.iptools.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "IRC Client" )
public class CommandIrc
{
    public static final String COMMAND_NAME = "irc";

    public final static CommandIrc INSTANCE = new CommandIrc();

    public static final String[] ALIASES = {};

    @Parameter( names = { "-f", "--filename" }, required = true, description = "Where the params at?" )
    private String filename;

    private CommandIrc() {}

    public String getFilename()
    {
        return filename;
    }

}

package com.sibilantsolutions.iptools.cli;

import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "HTTP Server")
public class CommandHttp
{

    final static public String COMMAND_NAME = "http";

    public static final String[] ALIASES = {};

    public final static CommandHttp INSTANCE = new CommandHttp();

    private CommandHttp() {}

}

package com.sibilantsolutions.iptools.cli;

import com.beust.jcommander.Parameters;

@Parameters( commandDescription = "Redirector" )
public class CommandRedir
{

    public static final String COMMAND_NAME = "redirector";

    public static final String[] ALIASES = {"redir"};

    public final static CommandRedir INSTANCE = new CommandRedir();

    private CommandRedir() {}

}

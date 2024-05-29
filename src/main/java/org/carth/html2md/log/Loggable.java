package org.carth.html2md.log;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;


public interface Loggable {

    Logger getLogger();

    default void logTrace(String message, Object... objects) {
        getLogger().trace(message, objects);
    }

    default void logInfo(String message, Object... objects) {
        getLogger().info(message, objects);
        if (!TerminalLogger.get().debug) {
            String formattedMessage;
            if (objects != null) {
                formattedMessage = MessageFormatter.arrayFormat(message, objects).getMessage();
            } else {
                formattedMessage = message;
            }
            TerminalLogger.get().print(formattedMessage);
        }
    }

    default void logInfoLn(String message, Object... objects) {
        getLogger().info(message, objects);
        if (!TerminalLogger.get().debug) {
            String formattedMessage;
            if (objects != null) {
                formattedMessage = MessageFormatter.arrayFormat(message, objects).getMessage();
            } else {
                formattedMessage = message;
            }
            TerminalLogger.get().println(formattedMessage);
        }
    }


    default void logError(String message, Object... objects) {
        getLogger().error(message, objects);
        if (!TerminalLogger.get().debug) {
            String formattedMessage;
            if (objects != null) {
                formattedMessage = MessageFormatter.arrayFormat(message, objects).getMessage();
            } else {
                formattedMessage = message;
            }
            TerminalLogger.get().print(formattedMessage, PromptColor.RED);
        }
    }

    default void logError(String message, Throwable throwable) {
        getLogger().error(message, throwable);
        TerminalLogger.get().println(message, PromptColor.RED);
    }
}

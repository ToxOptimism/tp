package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteOwnerCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteOwnerCommand object
 */
public class DeleteOwnerCommandParser implements Parser<DeleteOwnerCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteOwnerCommand
     * and returns a DeleteOwnerCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteOwnerCommand parse(String args) throws ParseException {
        try {
            List<Index> index = ParserUtil.parseMultipleIndices(args);
            return new DeleteOwnerCommand(index.get(0), index.get(1));
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteOwnerCommand.MESSAGE_USAGE), pe);
        }
    }

}

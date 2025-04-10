package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_ARGUMENTS_EMPTY;
import static seedu.address.logic.Messages.MESSAGE_EXPECTED_TWO_INDICES;
import static seedu.address.logic.Messages.MESSAGE_LISTING_TAG_REQUIRED_FOR_DELETE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeletePreferenceTagCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code DeletePreferenceTagCommand} object.
 */
public class DeletePreferenceTagCommandParser implements Parser<DeletePreferenceTagCommand> {

    private static final String WHITESPACE_REGEX = "\\s+";
    private static final int EXPECTED_PREAMBLE_PARTS = 2;
    private static final int PERSON_INDEX = 0;
    private static final int PREFERENCE_INDEX = 1;

    /**
     * Parses the given {@code String} of arguments in the context of the DeletePreferenceTagCommand
     * and returns an DeletePreferenceTagCommand object for execution.
     *
     * @param args The arguments to be parsed.
     * @throws ParseException if the user input does not conform the expected format.
     */
    public DeletePreferenceTagCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TAG);
        argMultimap.verifyNoDuplicateTagValues(DeletePreferenceTagCommand.MESSAGE_USAGE);
        Index personIndex;
        Index preferenceIndex;

        checkCommandFormat(argMultimap, args);
        List<Index> multipleIndices = ParserUtil.parseMultipleIndices(argMultimap.getPreamble());
        personIndex = multipleIndices.get(PERSON_INDEX);
        preferenceIndex = multipleIndices.get(PREFERENCE_INDEX);
        Set<String> tags = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        return new DeletePreferenceTagCommand(personIndex, preferenceIndex, tags);
    }

    private static void checkCommandFormat(ArgumentMultimap argMultimap, String args) throws ParseException {
        String preamble = argMultimap.getPreamble();
        boolean hasTags = !argMultimap.getAllValues(PREFIX_TAG).isEmpty();

        if (args.trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_ARGUMENTS_EMPTY,
                    DeletePreferenceTagCommand.MESSAGE_USAGE));
        }

        if (!hasTags) {
            throw new ParseException(String.format(MESSAGE_LISTING_TAG_REQUIRED_FOR_DELETE,
                    DeletePreferenceTagCommand.MESSAGE_USAGE));
        }

        if (preamble.isEmpty() || preamble.split(WHITESPACE_REGEX).length != EXPECTED_PREAMBLE_PARTS) {
            throw new ParseException(String.format(MESSAGE_EXPECTED_TWO_INDICES,
                    DeletePreferenceTagCommand.MESSAGE_USAGE));
        }
    }
}

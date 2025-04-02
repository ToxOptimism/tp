package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEW_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PropertyPreference;
import seedu.address.model.tag.Tag;

/**
 * Overwrites all {@code Tag}s in a {@code PropertyPreference} identified using it's displayed index
 * from {@code Person} identified using it's displayed index in the address book.
 */
public class OverwritePreferenceTagCommand extends Command {

    public static final String COMMAND_WORD = "overwritePreferenceTag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Replaces all tags in an existing property preference "
            + "identified by the index number used in the displayed preference list of\n"
            + "a specific person, identified by index number used in the displayed person list.\n"
            + "Parameters: PERSON_INDEX (must be a positive integer) PREFERENCE_INDEX (must be a positive integer) "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_NEW_TAG + "NEW_TAG]...\n"
            + "Example: " + COMMAND_WORD + " 3 2 " + PREFIX_TAG + "2-bedrooms\n"
            + "Example: " + COMMAND_WORD + " 3 2 " + PREFIX_NEW_TAG + "2-toilets\n"
            + "Example: " + COMMAND_WORD + " 3 1 " + PREFIX_NEW_TAG + "pet-friendly "
            + PREFIX_NEW_TAG + "integrated cooling\n"
            + "Example: " + COMMAND_WORD + " 3 3 " + PREFIX_TAG + "4-bedrooms " + PREFIX_TAG
            + "2-toilets " + PREFIX_NEW_TAG + "seaside view " + PREFIX_NEW_TAG + "beach";

    public static final String MESSAGE_SUCCESS = "Tag in preference overwritten with: %s";
    public static final String MESSAGE_INVALID_TAGS = "At least one of the tags given does not exist.\n%s";
    public static final String MESSAGE_DUPLICATE_TAGS = "At least one of the new tags given already exist.\n%s";

    private final Index targetPersonIndex;
    private final Index targetPreferenceIndex;
    private final Set<String> tagSet;
    private final Set<String> newTagSet;

    /**
     * Creates an @{code OverwritePreferenceTagCommand} to replace all {@code Tag}s in {@code Preference}.
     *
     * @param personIndex The index of the person from which the preference is located in.
     * @param preferenceIndex The index of the preference in which tags will be overwritten.
     * @param tagSet The set of existing tags to be used in the preference.
     * @param newTagSet The set of new tags to be created and used in the preference.
     */
    public OverwritePreferenceTagCommand(Index personIndex, Index preferenceIndex, Set<String> tagSet,
                                      Set<String> newTagSet) {
        requireAllNonNull(personIndex, preferenceIndex, tagSet, newTagSet);

        this.targetPersonIndex = personIndex;
        this.targetPreferenceIndex = preferenceIndex;
        this.tagSet = tagSet;
        this.newTagSet = newTagSet;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person targetPerson = getPersonFromIndex(model);
        PropertyPreference preference = getPreferenceFromPerson(targetPerson);
        validateTags(model);
        updatePreferenceTags(model, preference, targetPerson);
        return generateCommandResult(preference);
    }

    /**
     * Retrieves the person from the model using the stored index.
     */
    private Person getPersonFromIndex(Model model) throws CommandException {
        List<Person> lastShownList = model.getSortedFilteredPersonList();
        if (targetPersonIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(String.format(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, MESSAGE_USAGE));
        }
        return lastShownList.get(targetPersonIndex.getZeroBased());
    }

    /**
     * Retrieves the property preference from the person using the stored index.
     */
    private PropertyPreference getPreferenceFromPerson(Person targetPerson) throws CommandException {
        List<PropertyPreference> targetPreferenceList = targetPerson.getPropertyPreferences();
        if (targetPreferenceIndex.getZeroBased() >= targetPreferenceList.size()) {
            throw new CommandException(String.format(Messages.MESSAGE_INVALID_PREFERENCE_DISPLAYED_INDEX,
                    MESSAGE_USAGE));
        }
        return targetPreferenceList.get(targetPreferenceIndex.getZeroBased());
    }

    /**
     * Validates that all existing tags exist and new tags don't exist.
     */
    private void validateTags(Model model) throws CommandException {
        if (!model.hasTags(tagSet)) {
            throw new CommandException(String.format(MESSAGE_INVALID_TAGS, MESSAGE_USAGE));
        }
        if (model.hasNewTags(newTagSet)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_TAGS, MESSAGE_USAGE));
        }
    }

    /**
     * Updates the preference's tags by removing existing ones and adding new ones.
     */
    private void updatePreferenceTags(Model model, PropertyPreference preference, Person targetPerson) {
        // Create new tags
        model.addTags(newTagSet);

        Set<String> tagNames = new HashSet<>(tagSet);
        Set<Tag> newTags = prepareNewTags(model, tagNames);

        removeExistingTags(model, preference);
        addNewTags(model, preference, newTags);

        model.setPerson(targetPerson, targetPerson);
        model.resetAllLists();
    }

    /**
     * Prepares the set of new tags to be added to the preference.
     */
    private Set<Tag> prepareNewTags(Model model, Set<String> tagNames) {
        tagNames.addAll(newTagSet);
        Set<Tag> newTags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = model.getTag(tagName);
            newTags.add(tag);
        }
        return newTags;
    }

    /**
     * Removes all existing tags from the preference.
     */
    private void removeExistingTags(Model model, PropertyPreference preference) {
        Set<Tag> existingTags = new HashSet<>(preference.getTags());
        for (Tag tag : existingTags) {
            tag.removePropertyPreference(preference);
            model.setTag(tag, tag);
            preference.removeTag(tag);
        }
    }

    /**
     * Adds new tags to the preference.
     */
    private void addNewTags(Model model, PropertyPreference preference, Set<Tag> newTags) {
        for (Tag tag : newTags) {
            tag.addPropertyPreference(preference);
            model.setTag(tag, tag);
            preference.addTag(tag);
        }
    }

    /**
     * Generates the command result with formatted tags.
     */
    private CommandResult generateCommandResult(PropertyPreference preference) {
        Set<Tag> newTags = preference.getTags();
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.formatTagsOnly(newTags)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof OverwritePreferenceTagCommand
                && targetPersonIndex.equals(((OverwritePreferenceTagCommand) other).targetPersonIndex)
                && targetPreferenceIndex.equals(((OverwritePreferenceTagCommand) other).targetPreferenceIndex)
                && tagSet.equals(((OverwritePreferenceTagCommand) other).tagSet))
                && newTagSet.equals(((OverwritePreferenceTagCommand) other).newTagSet);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("personIndex", targetPersonIndex)
                .add("preferenceIndex", targetPreferenceIndex)
                .add("tags", tagSet)
                .add("newTags", newTagSet)
                .toString();
    }
}

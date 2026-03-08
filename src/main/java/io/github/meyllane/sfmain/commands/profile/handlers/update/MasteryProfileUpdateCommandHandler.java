package io.github.meyllane.sfmain.commands.profile.handlers.update;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.MasteryArgument;
import io.github.meyllane.sfmain.commands.core.models.CommandOperation;
import io.github.meyllane.sfmain.commands.core.models.ModelUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.core.models.ModelUpdateCommandResult;
import io.github.meyllane.sfmain.domain.elements.MasteryElement;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.domain.models.ProfileMastery;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.entity.Player;


public class MasteryProfileUpdateCommandHandler extends ModelUpdateCommandHandler<Profile, ProfileMastery> {
    private final String OPERATION_NODE = "operationValue";
    private final String UPDATE_VALUE_NODE = "updateValue";
    private final String LEVEL_NODE = "level";

    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("masteries")
                .thenNested(
                        new MultiLiteralArgument(OPERATION_NODE, CommandOperation.SET.getName()),
                        new MasteryArgument(UPDATE_VALUE_NODE),
                        new IntegerArgument(LEVEL_NODE).setOptional(true)
                                .executesPlayer(this::execute)
                )
                .then(new MasterySpeProfileUpdateCommandHandler().buildBranch());
    }

    @Override
    public Profile persist(Profile target) {
        return SFMain.profileEntityRepository.update(target);
    }

    @Override
    public ProfileMastery parse(CommandArguments args) {
        MasteryElement masteryElement = args.getByClass(UPDATE_VALUE_NODE, MasteryElement.class);
        int level = args.getByClassOrDefault(LEVEL_NODE, Integer.class, 1);
        return new ProfileMastery(masteryElement, level);
    }

    @Override
    public void update(Profile target, ProfileMastery updateValue, CommandOperation operation) {
        ProfileMastery profileMastery = target.getProfileMastery();
        if (profileMastery == null) {
            target.setProfileMastery(updateValue);
            return;
        }

        if (!updateValue.getMasteryElement().equals(target.getProfileMastery().getMasteryElement())) {
            target.resetProfileMasterySpe();
        }

        profileMastery.setMasteryElement(updateValue.getMasteryElement());
        profileMastery.setLevel(updateValue.getLevel());

        target.setProfileMastery(profileMastery);
    }

    @Override
    public CommandOperation getOperation(CommandArguments args) {
        return CommandOperation.SET;
    }

    @Override
    protected Class<Profile> getTargetClass() {
        return Profile.class;
    }

    @Override
    public void handleCompletion(ModelUpdateCommandResult<Profile, ProfileMastery> result, Player player) {
        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                "La maîtrise du Profile a bien été mise à jour !",
                PluginMessageType.SUCCESS
        ));
    }
}

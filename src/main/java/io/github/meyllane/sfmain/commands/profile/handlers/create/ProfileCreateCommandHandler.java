package io.github.meyllane.sfmain.commands.profile.handlers.create;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.core.ModelCreateCommandHandler;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.entity.Player;

import java.awt.*;

public class ProfileCreateCommandHandler extends ModelCreateCommandHandler<Profile> {
    private final String PROFILE_NAME_NODE = "profileName";

    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("create")
                .then(
                        new GreedyStringArgument(PROFILE_NAME_NODE)
                                .executesPlayer(this::execute)
                );
    }

    @Override
    public Profile parse(CommandArguments args) {
        String profileName = args.getByClassOrDefault(PROFILE_NAME_NODE, String.class, "");

        if (profileName.isEmpty()) {
            throw new SFException(ErrorMessage.get("profile.empty_profile_name"));
        }

        return new Profile(profileName, SFMain.speciesElementRegistry.get(1));
    }

    @Override
    public Profile create(Profile model) {
        if (SFMain.profileRegistry.contains(model.getName())) {
            throw new SFException(ErrorMessage.get("profile.profile_name_already_taken"));
        }
        return SFMain.profileEntityRepository.create(model);
    }

    @Override
    public void updateRegistry(Profile model) {
        SFMain.profileRegistry.register(model);
    }

    @Override
    public void handleCompletion(Profile model, Player player) {
        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                "Le profile " + model.getName() + " a bien été créé !",
                PluginMessageType.SUCCESS
        ));
    }
}

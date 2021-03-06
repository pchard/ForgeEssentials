package com.forgeessentials.commands.player;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandDoAs extends FEcmdModuleCommands
{
    @Override
    public String getCommandName()
    {
        return "doas";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            ChatOutputHandler.chatError(sender, getCommandUsage(sender));
            return;
        }
        if ((sender instanceof EntityPlayerMP) && args[0].equalsIgnoreCase("[CONSOLE]"))
        {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            if (!PermissionManager.checkPermission(player, "fe.commands.doas.console"))
                throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

            if (args.length < 2)
                throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
                
            args = Arrays.copyOfRange(args, 1, args.length);
            String cmd = StringUtils.join(args, " ");
            MinecraftServer.getServer().getCommandManager().executeCommand(new DoAsCommandSender(APIRegistry.IDENT_SERVER, player), cmd);
        }

        StringBuilder cmd = new StringBuilder(args.toString().length());
        for (int i = 1; i < args.length; i++)
        {
            cmd.append(args[i]);
            cmd.append(" ");
        }
        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player != null)
        {
            ChatOutputHandler.chatWarning(player, Translator.format("Player %s is attempting to issue a command as you.", sender.getCommandSenderName()));
            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(player, cmd.toString());
            ChatOutputHandler.chatConfirmation(sender, Translator.format("Successfully issued command as %s", args[0]));
        }
        else
            throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/doas <player> <command> Run a command as another player.";
    }

    @Override
    public void registerExtraPermissions()
    {
        PermissionManager.registerPermission("fe.commands.doas.console", PermissionLevel.OP);
    }

}

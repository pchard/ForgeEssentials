package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import net.minecraftforge.event.world.BlockEvent;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.entity.ActionBlock.ActionBlockType;

public class LogEventBreak extends PlayerLoggerEvent<BlockEvent.BreakEvent>
{

    public LogEventBreak(BlockEvent.BreakEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        ActionBlock action = new ActionBlock();
        action.time = new Date();
        action.player = getPlayer(event.getPlayer().getPersistentID());
        action.world = getWorld(event.world.provider.dimensionId);
        action.block = getBlock(event.block);
        action.metadata = event.blockMetadata;
        action.entity = getTileEntityBlob(event.world.getTileEntity(event.x, event.y, event.z));
        action.type = ActionBlockType.BREAK;
        action.x = event.x;
        action.y = event.y;
        action.z = event.z;
        em.persist(action);
    }
    
}
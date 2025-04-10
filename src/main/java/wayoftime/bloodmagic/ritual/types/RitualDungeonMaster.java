package wayoftime.bloodmagic.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wayoftime.bloodmagic.BloodMagic;
import wayoftime.bloodmagic.potion.BloodMagicPotions;
import wayoftime.bloodmagic.ritual.*;

import java.util.*;
import java.util.function.Consumer;

@RitualRegister("dungeon_master")
public class RitualDungeonMaster extends Ritual
{
	private static final int radius = 50;

	public static final String GROUNDING_RANGE = "dungeonMaster";

	public RitualDungeonMaster()
	{
		super("masterDungeon", 3, 0, "ritual." + BloodMagic.MODID + ".dungeonMaster");
		addBlockRange(GROUNDING_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-radius, 0, -radius), 2*radius+1, 30, 2*radius+1));
		setMaximumVolumeAndDistanceOfRange(GROUNDING_RANGE, 0, 1000, 1000);
		System.out.println(getTranslationKey() + " : " + Component.translatable(getTranslationKey()).getVisualOrderText());
	}

	@Override
	public void performRitual(IMasterRitualStone masterRitualStone)
	{
		/* Default Ritual Stuff */
		Level world = masterRitualStone.getWorldObj();
		BlockPos pos = masterRitualStone.getMasterBlockPos();


		/* Actual ritual stuff begins here */
		AreaDescriptor groundingRange = masterRitualStone.getBlockRange(GROUNDING_RANGE);

		List<Player> players = world.getEntitiesOfClass(Player.class, groundingRange.getAABB(pos));
		players.forEach(player -> player.addEffect(new MobEffectInstance(BloodMagicPotions.DUNGEON_AURA.get(), 20, 0, true, false)));
	}

	@Override
	public int getRefreshTime()
	{
		return 10;
	}

	@Override
	public int getRefreshCost()
	{
		return 0;
	}

	@Override
	public void gatherComponents(Consumer<RitualComponent> components)
	{
		addParallelRunes(components, 1, 0, EnumRuneType.DUSK);
		addCornerRunes(components, 1, 0, EnumRuneType.DUSK);
		addParallelRunes(components, 2, 0, EnumRuneType.DAWN);
		addCornerRunes(components, 2, 0, EnumRuneType.DAWN);
	}

	@Override
	public Ritual getNewCopy()
	{
		return new RitualDungeonMaster();
	}
}
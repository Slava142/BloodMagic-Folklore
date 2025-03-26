package wayoftime.bloodmagic.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.checkerframework.checker.units.qual.A;
import wayoftime.bloodmagic.BloodMagic;
import wayoftime.bloodmagic.core.data.SoulTicket;
import wayoftime.bloodmagic.ritual.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@RitualRegister("ritual_of_duplication")
public class RitualOfDuplication extends Ritual
{

	private Item copy_target = null;

	public RitualOfDuplication()
	{
		super("ritualOfDuplication", 0, 40000, "ritual." + BloodMagic.MODID + ".ritualOfDuplication");
	}

	@Override
	public void performRitual(IMasterRitualStone masterRitualStone)
	{
		Level world = masterRitualStone.getWorldObj();
		int currentEssence = masterRitualStone.getOwnerNetwork().getCurrentEssence();

		if (currentEssence < getRefreshCost() || copy_target == null){
			return;
		}

		ItemStack iStack = new ItemStack(copy_target);
		BlockPos pos = masterRitualStone.getMasterBlockPos().below();

		ItemEntity itemEntity = new ItemEntity(world, pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, iStack);

		world.addFreshEntity(itemEntity);

		masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(getRefreshCost()));

	}


	@Override
	public boolean activateRitual(IMasterRitualStone masterRitualStone, Player player, UUID owner)
	{
		return designate_target(masterRitualStone);
	}

	@Override
	public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType)
	{
		this.copy_target = null;
	}

	private boolean designate_target(IMasterRitualStone masterRitualStone) {
		BlockPos pos = masterRitualStone.getMasterBlockPos().above();
		AABB aabb = new AABB(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, pos.getX()+0.5, pos.getY()+0.1, pos.getZ()+0.5);
		List<ItemFrame> frames = masterRitualStone.getWorldObj().getEntitiesOfClass(ItemFrame.class, aabb);
		if (frames.size() != 1) {
			this.copy_target = null;
			return false;
		}

		ItemFrame frame = frames.get(0);

		if (frame.getItem().isEmpty()){
			this.copy_target = null;
			return false;
		}
		this.copy_target = frame.getItem().getItem();

		return true;
	}



	@Override
	public int getRefreshTime()
	{
		return 25;
	}

	@Override
	public int getRefreshCost()
	{
		return 100;
	}

	@Override
	public void gatherComponents(Consumer<RitualComponent> components)
	{
		addCornerRunes(components, -3, -1, EnumRuneType.DUSK);
	}

	@Override
	public Ritual getNewCopy()
	{
		return new RitualOfDuplication();
	}
}
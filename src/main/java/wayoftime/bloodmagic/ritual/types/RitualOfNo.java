package wayoftime.bloodmagic.ritual.types;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wayoftime.bloodmagic.BloodMagic;
import wayoftime.bloodmagic.ritual.*;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

@RitualRegister("ritual_of_no")
public class RitualOfNo extends Ritual
{

	static HashMap<IMasterRitualStone, UUID> protected_players = new HashMap<IMasterRitualStone, UUID>();

	public RitualOfNo()
	{
		super("ritualOfNo", 0, 40000, "ritual." + BloodMagic.MODID + ".ritualOfNo");
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void performRitual(IMasterRitualStone masterRitualStone)
	{
		Level world = masterRitualStone.getWorldObj();
		int currentEssence = masterRitualStone.getOwnerNetwork().getCurrentEssence();

		if (currentEssence < getRefreshCost())
		{
			masterRitualStone.getOwnerNetwork().causeNausea();
			unprotect(masterRitualStone);
			return;
		}
		protect(masterRitualStone);
		masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(getRefreshCost()));
	}

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		if (protected_players.containsValue(event.getEntity().getUUID())) {
//			event.setCanceled(true);
			Entity source = event.getSource().getEntity();
			System.out.println(source);
			if (source != null && !protected_players.containsValue(source.getUUID())) {
				System.out.println(event.getSource().typeHolder());
				source.hurt(new DamageSource(event.getSource().typeHolder()), 5);
			}
		}
	}

	@Override
	public boolean activateRitual(IMasterRitualStone masterRitualStone, Player player, UUID owner)
	{
		protect(masterRitualStone);
		return true;
	}

	@Override
	public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType)
	{
		unprotect(masterRitualStone);
	}

	private void protect(IMasterRitualStone masterRitualStone){
		if (!protected_players.containsKey(masterRitualStone)) {
			protected_players.put(masterRitualStone, masterRitualStone.getOwner());
			System.out.println("Activate " + masterRitualStone.getOwner() + " for " + masterRitualStone);
			System.out.println(protected_players);
		}
	}

	private void unprotect(IMasterRitualStone masterRitualStone){
		protected_players.remove(masterRitualStone);
		System.out.println("Stop " + masterRitualStone);
		System.out.println(protected_players);
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
		return new RitualOfNo();
	}
}
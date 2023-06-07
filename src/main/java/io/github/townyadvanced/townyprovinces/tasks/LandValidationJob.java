package io.github.townyadvanced.townyprovinces.tasks;

import com.palmergames.bukkit.towny.object.Coord;
import io.github.townyadvanced.townyprovinces.TownyProvinces;
import io.github.townyadvanced.townyprovinces.data.TownyProvincesDataHolder;
import io.github.townyadvanced.townyprovinces.objects.Province;
import io.github.townyadvanced.townyprovinces.settings.TownyProvincesSettings;
import io.github.townyadvanced.townyprovinces.util.DataHandlerUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class LandValidationJob extends BukkitRunnable {
	
	private LandValidationJobStatus landValidationJobStatus;
	
	public LandValidationJob() {
		landValidationJobStatus = LandValidationJobStatus.STOPPED;
	}
	
	public LandValidationJobStatus getLandValidationJobStatus() {
		return landValidationJobStatus;
	}

	public void setLandValidationJobStatus(LandValidationJobStatus landValidationJobStatus) {
		this.landValidationJobStatus = landValidationJobStatus;
	}
	
	@Override
	public void run() {
		//Handle any requests to start the land validation job
		switch (landValidationJobStatus) {
			case START_REQUESTED:
				setLandValidationRequestsForAllProvinces(true);
				landValidationJobStatus = LandValidationJobStatus.STARTED;
				TownyProvinces.info("Land Validation Job Starting.");
				executeLandValidation();
				break;
			case CONTINUE_REQUESTED:
				landValidationJobStatus = LandValidationJobStatus.STARTED;
				TownyProvinces.info("Land Validation Job Continuing.");
				executeLandValidation();
				break;
		}
	}

	private void setLandValidationRequestsForAllProvinces(boolean value) {
		for(Province province: TownyProvincesDataHolder.getInstance().getProvincesSet()) {
			if(province.isLandValidationRequested()) {
				province.setLandValidationRequested(value);
				province.saveData();
			}
		}
	}

	/**
	 * Go through each province,
	 * And decide if it is land or sea,
	 * then set the isSea boolean as appropriate
	 * <p>
	 * This method will not always work perfectly
	 * because it checks only a selection if the biomes.
	 * It does this because checking a biome is hard on the processor
	 * <p>
	 * Mistakes are expected,
	 * which is why server owners can run /tp province sea x,y
	 */
	private void executeLandValidation() {
		TownyProvinces.info("Now Running land validation job.");
		double numProvincesProcessed = 0;
		List<Province> provinces = TownyProvincesDataHolder.getInstance().getCopyOfProvincesSetAsList();
		for(Province province: provinces) {
			if (province.isLandValidationRequested()) {
				boolean isSea = isProvinceMainlyOcean(province);
				try {
					//Sleep as the above check is hard on process
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				if(isSea != province.isSea()) {
					province.setSea(isSea);
					province.saveData();
				}
			}
			numProvincesProcessed++;
			int percentCompletion = (int) ((numProvincesProcessed / provinces.size()) * 100);
			TownyProvinces.info("Land Validation Job Progress: " + percentCompletion + "%");

			//Handle any stop requests
			switch (landValidationJobStatus) {
				case STOP_REQUESTED:
					setLandValidationRequestsForAllProvinces(false);
					landValidationJobStatus = LandValidationJobStatus.STOPPED;
					TownyProvinces.info("Land Validation Job Stopped.");
					return;
				case PAUSE_REQUESTED:
					landValidationJobStatus = LandValidationJobStatus.PAUSED;
					TownyProvinces.info("Land Validation Job Paused.");
					return;
				case RESTART_REQUESTED:
					setLandValidationRequestsForAllProvinces(false);
					landValidationJobStatus = LandValidationJobStatus.START_REQUESTED;
					TownyProvinces.info("Land Validation Job Stopped.");
					return;
			}
		}
		TownyProvinces.info("Land Validation Job Complete.");
	}

	private static boolean isProvinceMainlyOcean(Province province) {
		List<Coord> coordsInProvince = province.getCoordsInProvince();
		String worldName = TownyProvincesSettings.getWorldName();
		World world = Bukkit.getWorld(worldName);
		Biome biome;
		Coord coordToTest;
		for(int i = 0; i < 10; i++) {
			coordToTest = coordsInProvince.get((int)(Math.random() * coordsInProvince.size()));
			int x = (coordToTest.getX() * TownyProvincesSettings.getProvinceBlockSideLength()) + 8;
			int z = (coordToTest.getZ() * TownyProvincesSettings.getProvinceBlockSideLength()) + 8;
			biome = world.getHighestBlockAt(x,z).getBiome();
			System.gc();
			if(!biome.name().toLowerCase().contains("ocean") && !biome.name().toLowerCase().contains("beach")) {
				return false;
			}
		}
		return true;
	}

}

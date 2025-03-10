package safx.client.particle;

import java.net.URL;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import safx.client.particle.SAParticleSystemType.AlphaEntry;
import safx.client.particle.SAParticleSystemType.ColorEntry;
import safx.client.render.SARenderHelper.RenderType;
import safx.util.SALogger;

/**
 * Handles the loading and storage of all ParticleSystem types
 */
public class SAFX {

//	//TODO
//	public static SAParticleSystemType getType (int index) {
//		return null;
//	}
	
	public static final String FXLIST_DIR = "/assets/safx/particles/";
	public static String[] FXFILES = {"fxlist.txt", "mwffxlist.txt"};
	
	public static HashMap<String, SAFXType> FXList = new HashMap<String, SAFXType>();

	
	public static List<SAParticleSystem> createFX(World world, String fx, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
		if (FXList.containsKey(fx.toLowerCase())) {
			SAFXType fxtype = FXList.get(fx.toLowerCase());
			List<SAParticleSystem> list = fxtype.createParticleSystems(world, posX, posY, posZ, motionX, motionY, motionZ);
			return list;
		}else {
			SALogger.logger_client.warning("FX '"+fx+"' not found!");
			//System.out.println("FX '"+fx+"' not found!");
			return null;
		}
	}
	
	public static List<SAParticleSystem> createFXOnEntity(Entity ent, String fx) {

		if (FXList.containsKey(fx.toLowerCase())) {
			SAFXType fxtype = FXList.get(fx.toLowerCase());
			List<SAParticleSystem> list = fxtype.createParticleSystemsOnEntity(ent);
			return list;
		}else {
			SALogger.logger_client.warning("FX '"+fx+"' not found!");
			return null;
		}
	}
	
	public static List<SAParticleSystem> createFXOnParticle(World worldIn, SAParticle ent, String fx) {

		if (FXList.containsKey(fx.toLowerCase())) {
			SAFXType fxtype = FXList.get(fx.toLowerCase());
			List<SAParticleSystem> list = fxtype.createParticleSystemsOnParticle(worldIn, ent);
			return list;
		}else {
			SALogger.logger_client.warning("FX '"+fx+"' not found!");
			return null;
		}
	}
	
	/*public static List<SAParticleSystem> createFXOnEntityItemAttached(Entity ent, EnumHand hand, String fx) {

		if (FXList.containsKey(fx.toLowerCase())) {
			SAFXType fxtype = FXList.get(fx.toLowerCase());
			List<SAParticleSystem> list = fxtype.createParticleSystemsOnEntityItemAttached(ent, hand);
			return list;
		}else {
			SALogger.logger_client.warning("FX '"+fx+"' not found!");
			return null;
		}
	}*/
	
	public static void loadFXList() {
		URL url = SAFX.class.getResource(FXLIST_DIR);
		if (url != null) {			
			for (String filename : FXFILES) {
				loadFXListFile(FXLIST_DIR+filename);
			}
		}
	}

	public static boolean loadFXListFile(String filename) {
		//BufferedReader br = new BufferedReader(new InputStreamReader(FXList.class.getResourceAsStream(filename)));
		Scanner sc = new Scanner(SAFX.class.getResourceAsStream(filename));
		sc.useLocale(Locale.ENGLISH);
		sc.useDelimiter("(\\s*=\\s*)|\\s\\{|\\s*//.*|\\r\\n|\\s+"); //|^\\s*$		
		String error = "";
		String next = "";
		String value = "";
		int count = 0;
		try {
		while (sc.hasNextLine() && error.equals("")) {
			if (sc.hasNext()) next = sc.next();
			else break;
			//FXList
			if (next.toLowerCase().equals("fxlist") && sc.hasNext()) {
				String name = sc.next();
				SAParticleListType type = new SAParticleListType();
				boolean end = false;
				while (sc.hasNextLine() && !end && error.equals("")) {
					if (sc.hasNext() && !(next = sc.next()).equals("")) {
						if (next.equals("}")) {
							end = true;
						}else {
							type.addParticleSystem(next.toLowerCase());
						}
					}
				}
				count++;
				FXList.put(name.toLowerCase(), type);
			} else if (!next.equals("")) {
				String name = next;
				SAParticleSystemType type = new SAParticleSystemType();
				boolean end = false;
				while (sc.hasNextLine() && !end && error.equals("")) {
					if (sc.hasNext() && !(next = sc.next()).equals("")) {
						String key = next;
						switch(key.toLowerCase()) {
							case "extends":
								value = sc.next();
								if (FXList.containsKey(value.toLowerCase())) {
									SAFXType fxtype = FXList.get(value.toLowerCase());
									if (fxtype instanceof SAParticleSystemType) {
										type.extend((SAParticleSystemType)fxtype);
									}else {
										error = "'EXTENDS' must specifiy a ParticleSystem, not an FXList.";
									}
								}else {
									error = "ParticleSystem '"+value.toLowerCase()+"' not found. Can't extend.";
								}
								break;
							case "rendertype":
								value = sc.next();
								switch (value.toUpperCase().trim()) {
								case "ADDITIVE":
									type.renderType = RenderType.ADDITIVE;
									break;
								case "ALPHA":
									type.renderType = RenderType.ALPHA;
									break;
								case "ALPHA_SHADED":
									type.renderType = RenderType.ALPHA_SHADED;
									break;
								case "SOLID":
									type.renderType = RenderType.SOLID;
									break;
								case "NO_Z_TEST":
									type.renderType = RenderType.NO_Z_TEST;
									break;
								default:
									error = name;
									break;
								}
								break;
							case "texture":
								value = sc.next();
								type.texture = new ResourceLocation(value);
								break;
							case "rows":
								type.rows = sc.nextInt();
								break;
							case "columns":
								type.columns = sc.nextInt();
								break;
							case "frames":
								type.frames = sc.nextInt();
								break;
							case "hasvariations":
								type.hasVariations = sc.nextBoolean();
								break;
//							case "randomrotation":
//								type.randomRotation = sc.nextBoolean();
//								break;
							case "angle":
								type.angleMin = sc.nextFloat();
								type.angleMax = sc.nextFloat();
								break;
							case "anglerate":
								type.angleRateMin = sc.nextFloat();
								type.angleRateMax = sc.nextFloat();
								break;
							case "angleratedamping":
								type.angleRateDampingMin = sc.nextFloat();
								type.angleRateDampingMax = sc.nextFloat();
								break;
							case "lifetime":
								type.lifetimeMin = sc.nextInt();
								type.lifetimeMax = sc.nextInt();
								break;
							case "systemlifetime":
								type.systemLifetimeMin = sc.nextInt();
								type.systemLifetimeMax = sc.nextInt();
								break;
							case "animationspeed":
								type.animationSpeedMin = sc.nextFloat();
								type.animationSpeedMax = sc.nextFloat();
								break;
							case "size":
								type.sizeMin = sc.nextFloat();
								type.sizeMax = sc.nextFloat();
								break;
							case "sizerate":
								type.sizeRateMin = sc.nextFloat();
								type.sizeRateMax = sc.nextFloat();
								break;
							case "sizeratedamping":
								type.sizeRateDampingMin = sc.nextFloat();
								type.sizeRateDampingMax = sc.nextFloat();
								break;
							case "startsizerate":
								type.startSizeRateMin = sc.nextFloat();
								type.startSizeRateMax = sc.nextFloat();
								break;
							case "startsizeratedamping":
								type.startSizeRateDampingMin = sc.nextFloat();
								type.startSizeRateDampingMax = sc.nextFloat();
								break;
							case "alpha":
								type.alphaEntries.add(new AlphaEntry(sc.nextFloat(), sc.nextFloat()));
								break;
							case "color":
								type.colorEntries.add(new ColorEntry(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextFloat()));
								break;
							case "particlecount":
								type.particleCountMin = sc.nextInt();
								type.particleCountMax = sc.nextInt();
								break;
							case "initialdelay":
								type.initialDelayMin = sc.nextInt();
								type.initialDelayMax = sc.nextInt();
								break;
							case "spawndelay":
								type.spawnDelayMin = sc.nextInt();
								type.spawnDelayMax = sc.nextInt();
								break;
							case "velocitytype":
								value = sc.next();
								switch (value.toUpperCase()) {
								case "NONE":
									type.velocityType = SAParticleSystemType.VEL_NONE;
									break;
								case "SPHERICAL":
									type.velocityType = SAParticleSystemType.VEL_SPHERICAL;
									break;
								case "ORTHO":
									type.velocityType = SAParticleSystemType.VEL_ORTHO;
									break;
								case "HEMISPHERICAL":
									type.velocityType = SAParticleSystemType.VEL_HEMISPHERICAL;
									break;
								case "OUTWARD":
									type.velocityType = SAParticleSystemType.VEL_OUTWARD;
									break;
								default:
									error = name;
									break;
								}
								break;
							case "velocity1":
								type.velocityDataMin[0] = sc.nextFloat();
								type.velocityDataMax[0] = sc.nextFloat();
								break;
							case "velocity2":
								type.velocityDataMin[1] = sc.nextFloat();
								type.velocityDataMax[1] = sc.nextFloat();
								break;
							case "velocity3":
								type.velocityDataMin[2] = sc.nextFloat();
								type.velocityDataMax[2] = sc.nextFloat();
								break;
							case "velocitydamping":
								type.velocityDampingMin = sc.nextFloat();
								type.velocityDampingMax = sc.nextFloat();
								break;
							case "volumetype":
								value = sc.next();
								switch (value.toUpperCase()) {
								case "SPHERE":
									type.volumeType = SAParticleSystemType.VOL_SPHERE;
									break;
								case "CYLINDER":
									type.volumeType = SAParticleSystemType.VOL_CYLINDER;
									break;
								case "CYLINDER2":
									type.volumeType = SAParticleSystemType.VOL_CYLINDER2;
									break;
								case "POINT":
									type.volumeType = SAParticleSystemType.VOL_POINT;
									break;
								case "HEMISPHERE":
									type.volumeType = SAParticleSystemType.VOL_HEMISPHERE;
									break;
								case "TRAIL":
									type.volumeType = SAParticleSystemType.VOL_TRAIL;
									break;
								default:
									error = name;
									break;
								}
								break;
							case "volume1":
								type.volumeData[0] = sc.nextFloat();
								break;
							case "volume2":
								type.volumeData[1] = sc.nextFloat();
								break;
							case "volume3":
								type.volumeData[2] = sc.nextFloat();
								break;
							case "gravity":
								type.gravity = sc.nextFloat();
								break;
							case "ishollow":
								type.isHollow = sc.nextBoolean();
								break;
							case "removeonground":
								type.removeOnGround = sc.nextBoolean();
								break;
							case "systemvelocityfactor":
								type.systemVelocityFactorMin = sc.nextFloat();
								type.systemVelocityFactorMax = sc.nextFloat();
								break;
							case "velocitydampingonground":
								type.velocityDampingOnGroundMin = sc.nextFloat();
								type.velocityDampingOnGroundMax = sc.nextFloat();
								break;
							case "offset":
								float x = sc.nextFloat();
								float y = sc.nextFloat();
								float z = sc.nextFloat();
								type.offset = new Vec3d(x, y, z);
								break;
							case "particlesmovewithsystem":
								type.particlesMoveWithSystem = sc.nextBoolean();
								break;
							case "particlessticktosystem":
								type.particlesStickToSystem = sc.nextBoolean();
								break;
							case "groundaligned":
								type.groundAligned = sc.nextBoolean();
								break;
							case "streak":
								type.streak = sc.nextBoolean();
								break;
							case "attachedsystem":
								type.attachedSystem = sc.next();
								break;
							case "}":
								end = true;
								//System.out.println("Successfully parsed ParticleSystem "+name);
								break;
						}
						
					}
				}
				if (error.equals("")) {
					count++;
					FXList.put(name.toLowerCase(), type);
					//System.out.println("Parsed ParticleSystem" +name+" in file '"+filename+"'.");
				}else{
					System.err.println("Error(s) while parsing particle "+name+" in file '"+filename+"'.");
					System.err.println("error");
					sc.close();
					return false;
				}
			}
			
		}
		}catch (InputMismatchException e) {
			e.printStackTrace();
			sc.close();
			return false;
		}

		sc.close();
		//System.out.println("Successfully parsed file "+ filename+ ". " +count + " ParticleSystems loaded.");
		return true;

	}
	
	
//	public static void killAllParticles() {
//		Iterator iter = Minecraft.getMinecraft().theWorld.loadedEntityList.iterator();
//		while (iter.hasNext()) {
//			try {
//				Entity e = (Entity)iter.next();
//				if (e instanceof EntityFX) {
//					e.setDead();
//					Minecraft.getMinecraft().theWorld.removeEntity(e);
//					System.out.println("Removed FX entity.");
//				}
//			}catch (Exception e) {
//			}
//		}
//	}
}

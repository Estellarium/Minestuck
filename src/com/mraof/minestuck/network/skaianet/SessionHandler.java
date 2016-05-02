package com.mraof.minestuck.network.skaianet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;

import com.mraof.minestuck.util.Debug;
import com.mraof.minestuck.util.MinestuckPlayerData;
import com.mraof.minestuck.util.Title;
import com.mraof.minestuck.util.UsernameHandler.PlayerIdentifier;
import com.mraof.minestuck.world.MinestuckDimensionHandler;
import com.mraof.minestuck.world.lands.LandAspectRegistry;
import com.mraof.minestuck.world.lands.gen.ChunkProviderLands;
import com.mraof.minestuck.MinestuckConfig;

/**
 * Handles session related stuff like title generation, consort choosing, and other session management stuff.
 * @author kirderf1
 */
public class SessionHandler {
	
	public static final String GLOBAL_SESSION_NAME = "global";
	
	/**
	 * The max numbers of players per session.
	 */
	public static int maxSize;
	
	/**
	 * If the current Minecraft world will act as if Minestuck.globalSession is true or not.
	 * Will be for example false even if Minestuck.globalSession is true if it can't merge all
	 * sessions into a single session.
	 */
	public static boolean singleSession;
	
	/**
	 * An array list of the current worlds sessions.
	 */
	static List<Session> sessions = new ArrayList<Session>();
	static Map<String, Session> sessionsByName = new HashMap<String, Session>();
	
	/**
	 * Called when the server loads a new world, after
	 * Minestuck has loaded the sessions from file.
	 */
	public static void serverStarted() {
		singleSession = MinestuckConfig.globalSession;
		if(!MinestuckConfig.globalSession) {
			split();
		} else
		{
			mergeAll();
			if(sessions.size() == 0)
			{
				Session mainSession = new Session();
				mainSession.name = GLOBAL_SESSION_NAME;
				sessions.add(mainSession);
				sessionsByName.put(mainSession.name, mainSession);
			}
		}
	}
	
	/**
	 * Merges all available sessions into one if it can.
	 * Used in the conversion of a non-global session world
	 * to a global session world.
	 */
	static void mergeAll()
	{
		if(!canMergeAll() || sessions.size() == 0)
		{
			singleSession = sessions.size() == 0;
			if(!singleSession)
				Debug.print("Not allowed to merge all sessions together! Global session temporarily disabled for this time.");
			return;
		}
		
		Session session = sessions.get(0);
		for(int i = 1; i < sessions.size(); i++)
		{
			Session s = sessions.remove(i);
			session.connections.addAll(s.connections);
			session.predefinedPlayers.putAll(s.predefinedPlayers);	//Used only when merging the global session
			if(s.skaiaId != 0) session.skaiaId = s.skaiaId;
			if(s.prospitId != 0) session.prospitId = s.prospitId;
			if(s.derseId != 0) session.derseId = s.derseId;
		}
		session.name = GLOBAL_SESSION_NAME;
		sessionsByName.clear();
		sessionsByName.put(session.name, session);
		
		session.completed = false;
	}
	
	/**
	 * Checks if it can merge all sessions in the current world into one.
	 * @return False if all registered players is more than maxSize, or if there exists more
	 * than one skaia, prospit, or derse dimension.
	 */
	static boolean canMergeAll()
	{
		if(sessions.size() == 1 && (!sessions.get(0).isCustom() || sessions.get(0).name.equals(GLOBAL_SESSION_NAME)))
				return true;
		
		int players = 0;
		boolean skaiaUsed = false, prospitUsed = false, derseUsed = false;
		for(Session s : sessions)
		{
			if(s.skaiaId != 0)
				if(skaiaUsed) return false;
				else skaiaUsed = true;
			if(s.prospitId != 0)
				if(prospitUsed) return false;
				else prospitUsed = true;
			if(s.derseId != 0)
				if(derseUsed) return false;
				else derseUsed = true;
			if(s.isCustom() || s.locked)
				return false;
			players += s.getPlayerList().size();
		}
		if(players > maxSize)
			return false;
		else return true;
	}
	
	/**
	 * Looks for the session that the player is a part of.
	 * @param player A string of the player's username.
	 * @return A session that contains at least one connection, that the player is a part of.
	 */
	public static Session getPlayerSession(PlayerIdentifier player)
	{
		for(Session s : sessions)
			if(s.containsPlayer(player))
				return s;
		return null;
	}
	
	static String merge(Session cs, Session ss, SburbConnection sb)
	{
		String s = canMerge(cs, ss);
		if(s == null)
		{
			sessions.remove(ss);
			if(sb != null)
				cs.connections.add(sb);
			cs.connections.addAll(ss.connections);
			if(cs.skaiaId == 0) cs.skaiaId = ss.skaiaId;
			if(cs.prospitId == 0) cs.prospitId = ss.prospitId;
			if(cs.derseId == 0) cs.derseId = ss.derseId;
			
			if(ss.isCustom())
			{
				sessionsByName.remove(ss.name);
				cs.name = ss.name;
				sessionsByName.put(cs.name, cs);
			}
			
		}
		return s;
	}
	
	static String canMerge(Session s0, Session s1)
	{
		if(s0.isCustom() && s1.isCustom() || s0.locked || s1.locked)
			return "computer.messageConnectFail";
		if(MinestuckConfig.forceMaxSize && s0.getPlayerList().size()+s1.getPlayerList().size()>maxSize)
			return "session.bothSessionsFull";
		return null;
	}
	
	/**
	 * Splits up the main session into small sessions.
	 * Used for the conversion of a global session world to
	 * a non-global session.
	 */
	static void split()
	{
		if(MinestuckConfig.globalSession || sessions.size() != 1)
			return;
		
		Session s = sessions.get(0);
		split(s);
	}
	
	static void split(Session session)
	{
		if(session.locked)
			return;
		
		sessions.remove(session);
		if(session.isCustom())
			sessionsByName.remove(session.name);
		boolean first = true;
		while(!session.connections.isEmpty() || first)
		{
			Session s = new Session();
			if(!first)
			{
				s.connections.add(session.connections.remove(0));
				
			} else
			{
				if(session.isCustom() && (!session.name.equals(GLOBAL_SESSION_NAME) || !session.predefinedPlayers.isEmpty()))
				{
					s.name = session.name;
					s.predefinedPlayers.putAll(session.predefinedPlayers);
					sessionsByName.put(s.name, s);
				}
				s.skaiaId = session.skaiaId;
				s.prospitId = session.prospitId;
				s.derseId = session.derseId;
			}
			
			boolean found;
			do {
				found = false;
				Iterator<SburbConnection> iter = session.connections.iterator();
				while(iter.hasNext()){
					SburbConnection c = iter.next();
					if(s.containsPlayer(c.getClientIdentifier()) || s.containsPlayer(c.getServerIdentifier()) || first && !c.canSplit){
						found = true;
						iter.remove();
						s.connections.add(c);
					}
				}
			} while(found);
			s.checkIfCompleted();
			if(s.connections.size() > 0 || s.isCustom())
				sessions.add(s);
			first = false;
		}
	}
	
	/**
	 * Will check if two players can connect based on their main connections and sessions.
	 * Does NOT include session size checking.
	 * @return True if client connection is not null and client and server session is the same or 
	 * client connection is null and server connection is null.
	 */
	static boolean canConnect(PlayerIdentifier client, PlayerIdentifier server)
	{
		Session sClient = getPlayerSession(client), sServer = getPlayerSession(server);
		SburbConnection cClient = SkaianetHandler.getMainConnection(client, true);
		SburbConnection cServer = SkaianetHandler.getMainConnection(server, false);
		return cClient != null && sClient == sServer && (MinestuckConfig.allowSecondaryConnections || cClient == cServer)
				|| cClient == null && cServer == null && !(sClient != null && sClient.locked) && !(sServer != null && sServer.locked);
	}
	
	/**
	 * @return Null if successful or an unlocalized error message describing reason.
	 */
	static String onConnectionCreated(SburbConnection connection)
	{
		if(!canConnect(connection.getClientIdentifier(), connection.getServerIdentifier()))
			return "computer.messageConnectFailed";
		if(singleSession)
		{
			if(sessions.size() == 0)
				return "computer.messageConnectFailed";
			int i = (sessions.get(0).containsPlayer(connection.getClientIdentifier())?0:1)+(sessions.get(0).containsPlayer(connection.getServerIdentifier())?0:1);
			if(MinestuckConfig.forceMaxSize && sessions.get(0).getPlayerList().size()+i > maxSize)
				return "computer.singleSessionFull";
			else
			{
				sessions.get(0).connections.add(connection);
				return null;
			}
		} else
		{
			Session sClient = getPlayerSession(connection.getClientIdentifier()), sServer = getPlayerSession(connection.getServerIdentifier());
			if(sClient == null && sServer == null)
			{
				Session s = new Session();
				sessions.add(s);
				s.connections.add(connection);
				return null;
			} else if(sClient == null || sServer == null) {
				if((sClient == null?sServer:sClient).locked || MinestuckConfig.forceMaxSize && (sClient == null?sServer:sClient).getPlayerList().size()+1 > maxSize)
					return "computer."+(sClient == null?"server":"client")+"SessionFull";
				(sClient == null?sServer:sClient).connections.add(connection);
				return null;
			} else {
				if(sClient == sServer) {
					sClient.connections.add(connection);
					return null;
				}
				else return merge(sClient, sServer, connection);
			}
		}
	}
	
	/**
	 * @param normal If the connection was closed by normal means.
	 * (includes everything but getting crushed by a meteor and other reasons for removal of a main connection)
	 */
	static void onConnectionClosed(SburbConnection connection, boolean normal)
	{
		Session s = getPlayerSession(connection.getClientIdentifier());
		
		if(!connection.isMain)
		{
			s.connections.remove(connection);
			if(!singleSession)
				if(s.connections.size() == 0 && !s.isCustom())
					sessions.remove(s);
				else split(s);
		} else if(!normal) {
			s.connections.remove(connection);
			if(SkaianetHandler.getAssociatedPartner(connection.getClientIdentifier(), false) != null && !connection.getServerIdentifier().equals(new PlayerIdentifier(".null")))
			{
				SburbConnection c = SkaianetHandler.getMainConnection(connection.getClientIdentifier(), false);
				if(c.isActive)
					SkaianetHandler.closeConnection(c.getClientIdentifier(), c.getServerIdentifier(), true);
				switch(MinestuckConfig.escapeFailureMode) {
				case 0:
					c.serverIdentifier = connection.getServerIdentifier();
					break;
				case 1:
					c.serverIdentifier = new PlayerIdentifier(".null");
					break;
				}
			}
			if(s.connections.size() == 0 && !s.isCustom())
				sessions.remove(s);
		}
	}
	
	static List<Object> getServerList(PlayerIdentifier client)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		for(PlayerIdentifier server : SkaianetHandler.serversOpen.keySet())
		{
			if(canConnect(client, server))
			{
				list.add(server.getId());
				list.add(server.getUsername());
			}
		}
		return list;
	}
	
	/**
	 * Creates data to be used for the data checker
	 */
	public static NBTTagCompound createDataTag()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList sessionList = new NBTTagList();
		nbt.setTag("sessions", sessionList);
		int nameIndex = 1;
		for(int i = 0; i < sessions.size(); i++)
		{
			Session session = sessions.get(i);
			NBTTagList connectionList = new NBTTagList();
			Set<PlayerIdentifier> playerSet = new HashSet<PlayerIdentifier>();
			for(SburbConnection c :session.connections)
			{
				if(c.isMain)
					playerSet.add(c.getClientIdentifier());
				NBTTagCompound connectionTag = new NBTTagCompound();
				connectionTag.setString("client", c.getClientIdentifier().getUsername());
				connectionTag.setString("clientId", c.getClientIdentifier().getString());
				connectionTag.setString("server", c.getServerIdentifier().getUsername());
				connectionTag.setBoolean("isMain", c.isMain);
				connectionTag.setBoolean("isActive", c.isActive);
				if(c.isMain)
				{
					connectionTag.setInteger("clientDim", c.enteredGame ? c.clientHomeLand : 0);
					if(c.enteredGame && DimensionManager.isDimensionRegistered(c.clientHomeLand))
					{
						LandAspectRegistry.AspectCombination aspects = MinestuckDimensionHandler.getAspects(c.clientHomeLand);
						IChunkProvider chunkGen = MinecraftServer.getServer().worldServerForDimension(c.clientHomeLand).provider.createChunkGenerator();
						if(chunkGen instanceof ChunkProviderLands)
						{
							ChunkProviderLands landChunkGen = (ChunkProviderLands) chunkGen;
							if(landChunkGen.nameOrder)
							{
								connectionTag.setString("aspect1", aspects.aspectTerrain.getNames()[landChunkGen.nameIndex1]);
								connectionTag.setString("aspect2", aspects.aspectTitle.getNames()[landChunkGen.nameIndex2]);
							} else
							{
								connectionTag.setString("aspect1", aspects.aspectTitle.getNames()[landChunkGen.nameIndex2]);
								connectionTag.setString("aspect2", aspects.aspectTerrain.getNames()[landChunkGen.nameIndex1]);
							}
						}
						Title title = MinestuckPlayerData.getTitle(c.getClientIdentifier());
						connectionTag.setByte("class", title == null ? -1 : (byte) title.getHeroClass().ordinal());
						connectionTag.setByte("aspect", title == null ? -1 : (byte) title.getHeroAspect().ordinal());
					} else if(session.predefinedPlayers.containsKey(c.getClientIdentifier()))
					{
						PredefineData data = session.predefinedPlayers.get(c.getClientIdentifier());
						
						if(data.title != null)
						{
							connectionTag.setByte("class", (byte) data.title.getHeroClass().ordinal());
							connectionTag.setByte("aspect", (byte) data.title.getHeroAspect().ordinal());
						}
						
						if(data.landTerrain != null)
							connectionTag.setString("aspectTerrain", data.landTerrain.getPrimaryName());
						if(data.landTitle != null)
							connectionTag.setString("aspectTitle", data.landTitle.getPrimaryName());
					}
				}
				connectionList.appendTag(connectionTag);
			}
			
			for(Map.Entry<PlayerIdentifier, PredefineData> entry : session.predefinedPlayers.entrySet())
			{
				if(playerSet.contains(entry.getKey()))
					continue;
				
				NBTTagCompound connectionTag = new NBTTagCompound();
				
				connectionTag.setString("client", entry.getKey().getUsername());
				connectionTag.setString("clientId", entry.getKey().getString());
				connectionTag.setBoolean("isMain", true);
				connectionTag.setBoolean("isActive", false);
				connectionTag.setInteger("clientDim", 0);
				
				PredefineData data = entry.getValue();
				
				if(data.title != null)
				{
					connectionTag.setByte("class", (byte) data.title.getHeroClass().ordinal());
					connectionTag.setByte("aspect", (byte) data.title.getHeroAspect().ordinal());
				}
				
				if(data.landTerrain != null)
					connectionTag.setString("aspectTerrain", data.landTerrain.getPrimaryName());
				if(data.landTitle != null)
					connectionTag.setString("aspectTitle", data.landTitle.getPrimaryName());
				
				connectionList.appendTag(connectionTag);
			}
			
			NBTTagCompound sessionTag = new NBTTagCompound();
			if(session.name != null)
				sessionTag.setString("name", session.name);
			sessionTag.setTag("connections", connectionList);
			sessionList.appendTag(sessionTag);
		}
		return nbt;
	}
}
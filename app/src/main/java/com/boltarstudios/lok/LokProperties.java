package com.boltarstudios.lok;

import java.math.BigDecimal;
import java.util.List;

import android.app.Application;

import com.google.cloud.backend.core.CloudBackendFragment;
import com.google.cloud.backend.core.CloudEntity;

public class LokProperties extends Application {
	
	private CloudBackendFragment cbf;
	private List<CloudEntity> playersList;
	private CloudEntity ce;
	
	public static final String DS_REALM_NAME = "Realm_Name";
	public static final String DS_REALM_RACE = "Realm_Race";
	public static final String DS_REALM_GOLD = "Realm_Gold";
	public static final String DS_REALM_POP  = "Realm_Pop";
	public static final String DS_REALM_FARMS = "Realm_Farms";
	public static final String DS_ARCHERS = "Archers";
	public static final String DS_SWORDSMEN = "Swordsmen";
	public static final String DS_DRAGONS = "Dragons";
	
	public final int MAX_ARMY_PURCHASABLE_PER_TURN = 20;
	
	public CloudEntity getCe() {
		return ce;
	}

	public void setCe(CloudEntity ce) {
		this.ce = ce;
	}

	public List<CloudEntity> getPlayersList() {
		return playersList;
	}

	public void setPlayersList(List<CloudEntity> playersList) {
		this.playersList = playersList;
	}

	public CloudBackendFragment getCloudBackendFragment() {
		return cbf;
	}
	
	public void setCloudBackendFragment(CloudBackendFragment cbf) {
		this.cbf = cbf;
	}
	
	public void setFarms(BigDecimal farms) {
		ce.put(DS_REALM_FARMS, farms);
	}
	
	public Integer getFarms() {
		return getCloudProp(ce, DS_REALM_FARMS);
	}
	
	public String getName() {
		return (String) ce.getProperties().get(DS_REALM_NAME);
	}
	
	public Integer getGold() {
		return getCloudProp(ce, DS_REALM_GOLD);
	}
	
	public Integer getPop() {
		return getCloudProp(ce, DS_REALM_POP);
	}
	
	public String getRace() {
		return (String) ce.getProperties().get(DS_REALM_RACE);		
	}
	
	public Integer getArchers() {
		return getCloudProp(ce, DS_ARCHERS);
	}
	public Integer getSwordsmen() {
		return getCloudProp(ce, DS_SWORDSMEN);
	}
	public Integer getDragons() {
		return getCloudProp(ce, DS_DRAGONS);
	}
	
	// since google datastore sometimes likes to return BigDecimal
	// or String (after a memcache flush), we need a way to differentiate
	// here... *sigh*
	// use this only for number props
	public Integer getCloudProp(CloudEntity e, String prop) {
		Object o = e.getProperties().get(prop);
		if (o instanceof String)
			return Integer.valueOf((String)o);
		else if (o instanceof Integer)
			return (Integer)o;
		else if (o instanceof BigDecimal)
			return Integer.valueOf(((BigDecimal) o).intValue());
		else return Integer.valueOf(0);
	}

}

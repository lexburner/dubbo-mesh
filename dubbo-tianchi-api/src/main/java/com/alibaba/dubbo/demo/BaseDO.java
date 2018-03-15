/**
 * www.taobao.com
 * ��,��ϲ��!
 */
package com.alibaba.dubbo.demo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseDO implements Serializable {
	
	private long id;
	private String name;
	private Map<String, BaseDO> baseDOMap;
	
	public Map<String, BaseDO> getBaseDOMap() {
		return baseDOMap;
	}

	public void setBaseDOMap(Map<String, BaseDO> baseDOMap) {
		this.baseDOMap = baseDOMap;
	}

	private static final long serialVersionUID = -795376007608590602L;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public static BaseDO getSimpleBaseDO() {
		BaseDO baseDOArrayItem1 = new BaseDO();
		baseDOArrayItem1.setId(45);
		baseDOArrayItem1.setName("In the Orient ye demonstrates hissucceed.");
		BaseDO baseDOArrayItem2 = new BaseDO();
		baseDOArrayItem1.setId(44);
		baseDOArrayItem1.setName("In ye demonstrates hissucceed.the Orientthe Orientthe Orient");
		BaseDO baseDOArrayItem3 = new BaseDO();
		baseDOArrayItem1.setId(46);
		baseDOArrayItem1.setName("In 5433535 hissucceed.the Orientthe Orientthe Orient");
		Map<String, BaseDO> baseDOMap = new HashMap<String,BaseDO>();
		baseDOMap.put("baseDOArrayItem2", baseDOArrayItem2);
		baseDOMap.put("baseDOArrayItem3", baseDOArrayItem3);
		baseDOArrayItem1.setBaseDOMap(baseDOMap);
		return baseDOArrayItem1;
	}
}

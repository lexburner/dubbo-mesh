package com.alibaba.dubbo.demo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


public class VeryComplexDO implements Serializable {

//	public static String PROVIDER_IP = "10.101.167.23";
//
//	public final static String tag = "SUCCESS";
//
//	private static final long serialVersionUID = -6315109222257881914L;
//	private int pint;
//	private long plong;
//	private float pfloat;
//	private short fshort;
//	private byte pbyte;
//	private long[] plongArray;
//	private List<String> plist;
//	private Map<String, String> pmap;
//	private TreeSet<String> ptreeset;
//	private BaseDO pBaseDO;
//	private String ip;
//	public String getIp() {
//		return ip;
//	}
//
//	public void setIp(String ip) {
//		this.ip = ip;
//	}
//
//	public int getPint() {
//		return pint;
//	}
//
//	public void setPint(int pint) {
//		this.pint = pint;
//	}
//
//	public long getPlong() {
//		return plong;
//	}
//
//	public void setPlong(long plong) {
//		this.plong = plong;
//	}
//
//	public float getPfloat() {
//		return pfloat;
//	}
//
//	public void setPfloat(float pfloat) {
//		this.pfloat = pfloat;
//	}
//
//	public short getFshort() {
//		return fshort;
//	}
//
//	public void setFshort(short fshort) {
//		this.fshort = fshort;
//	}
//
//	public byte getPbyte() {
//		return pbyte;
//	}
//
//	public void setPbyte(byte pbyte) {
//		this.pbyte = pbyte;
//	}
//
//	public long[] getPlongArray() {
//		return plongArray;
//	}
//
//	public void setPlongArray(long[] plongArray) {
//		this.plongArray = plongArray;
//	}
//
//	public List<String> getPlist() {
//		return plist;
//	}
//
//	public void setPlist(List<String> plist) {
//		this.plist = plist;
//	}
//
//	public Map<String, String> getPmap() {
//		return pmap;
//	}
//
//	public void setPmap(Map<String, String> pmap) {
//		this.pmap = pmap;
//	}
//
//	public TreeSet<String> getPtreeset() {
//		return ptreeset;
//	}
//
//	public void setPtreeset(TreeSet<String> ptreeset) {
//		this.ptreeset = ptreeset;
//	}
//
//	public BaseDO getpBaseDO() {
//		return pBaseDO;
//	}
//
//	public void setpBaseDO(BaseDO pBaseDO) {
//		this.pBaseDO = pBaseDO;
//	}
//
//	public static VeryComplexDO getFixedComplexDO2048() {
//		VeryComplexDO vdo = new VeryComplexDO();
//		vdo.setFshort((short) 2);
//		vdo.setPbyte((byte) 3);
//		vdo.setPfloat(1.2f);
//		vdo.setPint(69);
//		List<String> tmp = new ArrayList<String>();
//		tmp.add("If you were a teardrop,In my eye");
//		tmp.add("For fear of losing you,I would never cry");
//		tmp.add("And if the golden sun,Should cease to shine its light");
//		tmp.add("Just one smile from you,Would make my whole world bright");
//		tmp.add("I was not delivered unto this world in defeat, nor does failure course in my veins. I am not a sheep waiting to be prodded by my shepherd. I am a lion and I refuse to talk, to walk, to sleep with the sheep. Let them join the sheep.");
//		vdo.setPlist(tmp);
//		vdo.setPlong(56);
//		vdo.setPlongArray(new long[] { 1, 2, 3, 4, 5, 6 });
//		BaseDO bdo = new BaseDO();
//		bdo.setId(45);
//		bdo.setName("��In the Orient young bulls are tested for the fight arena in a certain manner. Each is brought to the ring and allowed to attack a picador who pricks them with a lance. The bravery of each bull is then rated with care according to the number of times he demonstrates his willingness to charge in spite of the sting of the blade. Henceforth will I recognize that each day I am tested by life in like manner. If I persist, if I continue to try, if I continue to charge forward, I will succeed.");
//		vdo.setpBaseDO(bdo);
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("me", "A man was going to the house of some rich person. As he went along the road, he saw a box of good apples at the side of the road. He said, I do not want to eat those apples; for the rich man will give me much food; he will give me very nice food to eat. Then he took the apples and threw them away into the dust.");
//		map.put("love", "An old woman had a cat. The cat was very old; she could not run quickly, and she could not bite, because she was so old. One day the old cat saw a mouse; she jumped and caught the mouse. But she could not bite it; so the mouse got out of her mouth and ran away, because the cat could not bite it");
//		map.put("test", "Then the old woman became very angry because the cat had not killed the mouse. She began to hit the cat. The cat said, Do not hit your old servant. I have worked for you for many years, and I would work for you still, but I am too old. Do not be unkind to the old, but remember what good work the old did when they were young.");
//		vdo.setPmap(map);
//		TreeSet<String> ts = new TreeSet<String>();
//		ts.add("bbb");
//		ts.add("aaa");
//		ts.add("bbb");
//		vdo.setPtreeset(ts);
//		return vdo;
//	}
//
//	public static VeryComplexDO getFixedComplexDO256() {
//		VeryComplexDO vdo = new VeryComplexDO();
//		vdo.setFshort((short) 2);
//		vdo.setPbyte((byte) 3);
//		vdo.setPfloat(1.2f);
//		vdo.setPint(69);
//		List<String> tmp = new ArrayList<String>();
//		tmp.add("If you ");
//		tmp.add("For");
//		tmp.add("And ");
//		tmp.add("Just ");
//		vdo.setPlist(tmp);
//		vdo.setPlong(56);
//		vdo.setPlongArray(new long[] { 1, 2, 3, 4, 5, 6 });
//		BaseDO bdo = new BaseDO();
//		bdo.setId(45);
//		bdo.setName("Name In the ");
//		vdo.setpBaseDO(bdo);
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("me", "A man ");
//		map.put("love", "An ");
//		map.put("test", "Then the . ");
//		vdo.setPmap(map);
//		TreeSet<String> ts = new TreeSet<String>();
//		ts.add("bbb");
//		ts.add("aaa");
//		ts.add("bbb");
//		vdo.setPtreeset(ts);
//		return vdo;
//	}
//
//	public static boolean verify(VeryComplexDO actual) {
//		if(actual.getIp().equals(PROVIDER_IP)){
//			return true;
//		}
//		return false;
//	}

//	public static void main(String[] args){
//		String res = FastjsonUtils.jsonObject2JsonString(VeryComplexDO.getFixedComplexDO2048());
//		System.out.println(res);
//	}
}

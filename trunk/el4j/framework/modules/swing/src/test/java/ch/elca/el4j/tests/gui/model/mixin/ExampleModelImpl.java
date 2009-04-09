package ch.elca.el4j.tests.gui.model.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleModelImpl implements ExampleModel {
	private String m_property1;
	private List<Integer> m_list;
	private Map<Integer, Integer> m_map;
	
	public ExampleModelImpl() {
		m_list = new ArrayList<Integer>();
		m_map = new HashMap<Integer, Integer>();
	}
	
	public String getProperty1() {
		return m_property1;
	}

	public void setProperty1(String o) {
		m_property1 = o;
	}
	
	public List<Integer> getList() {
		return m_list;
	}
	
	public void setList(List<Integer> list) {
		m_list = list;
	}
	
	public Map<Integer, Integer> getMap() {
		return m_map;
	}
	
	public void setMap(Map<Integer, Integer> map) {
		m_map = map;
	}
	
	public void set() { }
	public void setXandY(int x, int y) { }
	public int getYofX(int x) {
		return 0;
	}
}
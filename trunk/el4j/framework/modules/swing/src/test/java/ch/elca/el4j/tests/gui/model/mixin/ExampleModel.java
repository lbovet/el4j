package ch.elca.el4j.tests.gui.model.mixin;

import java.util.List;
import java.util.Map;

/**
 * An example model.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public interface ExampleModel {
	public String getProperty1();
	public void setProperty1(String o);
	
	public List<Integer> getList();
	public void setList(List<Integer> list);
	
	public Map<Integer, Integer> getMap();
	public void setMap(Map<Integer, Integer> map);
	
	// no java bean getters/setters
	public void set();
	public void setXandY(int x, int y);
	public int getYofX(int x);
}
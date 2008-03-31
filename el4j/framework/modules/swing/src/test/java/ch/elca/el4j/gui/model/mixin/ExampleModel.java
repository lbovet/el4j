package ch.elca.el4j.gui.model.mixin;

import java.util.List;
import java.util.Map;

public interface ExampleModel {
    public Object getProperty1();
    public void setProperty1(Object o);
    
    public List<Integer> getList();
    public void setList(List<Integer> list);
    
    public Map<Integer, Integer> getMap();
    public void setMap(Map<Integer, Integer> map);
}
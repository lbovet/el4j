package ch.elca.el4j.gui.model.mixin;

public class ExampleModelImpl implements ExampleModel {
    private Object property1;

    public Object getProperty1() {
        return property1;
    }

    public void setProperty1(Object o) {
        property1 = o;
    }
}
package ch.elca.el4j.demos.gui.fs.object;

import java.util.LinkedList;
import java.util.List;

public class ExampleKonto {
    private List<KontoDTO> konti = new LinkedList<KontoDTO>();

    public ExampleKonto() {
        // generate data
        konti.add(new KontoDTO("301", 1, null, 100.0, "215", "301"));
        konti.add(new KontoDTO("302", 1, null, 100.0, "215", "301"));
        konti.add(new KontoDTO("303", 1, null, 100.0, "215", "301"));
        konti.add(new KontoDTO("303", 1, null, 100.0, "215", "301"));
    }

    public List<KontoDTO> getKonti() {
        return konti;
    }
}

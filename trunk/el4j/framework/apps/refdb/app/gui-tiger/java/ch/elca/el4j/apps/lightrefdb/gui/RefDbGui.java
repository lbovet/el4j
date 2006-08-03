/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.apps.lightrefdb.gui;

import ch.elca.el4j.apps.lightrefdb.dom.*;
import ch.elca.el4j.services.richclient.config.*;
import ch.elca.el4j.util.observer.impl.LiveValueFactory;


public class RefDbGui extends Gui {    
    static class MainWindow extends Window {{
        pages.add(new Page() {Search ks,rs; Table kt,rt; {
            name = "main";
            layout = Layout.horizontally;
            components.add(
                new Pane(Layout.vertically) {{
                    components.add(
                        ks = new Search(Keyword.class),
                        kt = new Table(Keyword.class) {{
                            filter=ks.query;
                        }},
                        new Edit(Keyword.class) {{
                            //properties.lock("name");
                            current = LiveValueFactory.theElementIn(kt.selection);
                        }}
                    );
                }},
                new Pane(Layout.vertically) {{
                    components.add(
                        rs = new Search(Reference.class) {{
                            properties.hide("keywords");
                        }},
                        rt = new Table(Reference.class) {{
                            filter=rs.query;
                            properties.hide("keywords");
                            executors.remove(delete);
                        }},
                        new Edit(Reference.class) {{
                            current = LiveValueFactory.theElementIn(rt.selection);
                            properties.hide("keywords");
                            properties.lock("whenInserted");
                        }}
                    );
                }}
            );
        }});
    }}
    
    // experimental, not supported by components yet.
    class ReferencePropertiesDialog extends Dialog {{
        components.add(
            new Edit(Reference.class) {{
                properties.hide("keywords");
            }}  
        );
    }}
    
    {
        windows.add(new MainWindow());
    }
}
-- Version: $Revision$
-- URL: $URL$
-- Date: $Date$
-- Author: $Author$

-- this nice and shiny script drops all tables and views contained in the schema for the current db user

BEGIN
  FOR lv_cur IN
    (
      SELECT 
        'alter table '||owner||'.'||table_name||' modify constraint '||constraint_name||' disable' 
                                       AS cmd,
        ROW_NUMBER() OVER (ORDER BY table_name, constraint_name)
                                       AS sofar,
        COUNT(*) OVER (PARTITION BY 1) AS totalwork,
        constraint_name                AS target
      FROM user_constraints
      WHERE constraint_type = 'R'
      AND status = 'ENABLED'
      ORDER BY table_name, constraint_name
    )
  LOOP
      EXECUTE IMMEDIATE lv_cur.cmd;
  END LOOP;
END;
/

BEGIN
  FOR lv_cur IN
    (
      SELECT 
        'drop table '||table_name||' cascade constraint'     AS cmd,
        ROW_NUMBER() OVER (ORDER BY table_name)
                                       AS sofar,
        COUNT(*) OVER (PARTITION BY 1) AS totalwork,
        table_name                     AS target
      FROM user_tables
      ORDER BY table_name
    )
  LOOP
    EXECUTE IMMEDIATE lv_cur.cmd;
    COMMIT;
  END LOOP;
END;
/


BEGIN
  FOR lv_cur IN
    (
      SELECT 
        'drop sequence '||sequence_name    AS cmd,
        ROW_NUMBER() OVER (ORDER BY sequence_name)
                                       AS sofar,
        COUNT(*) OVER (PARTITION BY 1) AS totalwork,
        sequence_name                     AS target
      FROM user_sequences
      ORDER BY sequence_name
    )
  LOOP
    EXECUTE IMMEDIATE lv_cur.cmd;
    COMMIT;
  END LOOP;
END;
/


BEGIN
  FOR lv_cur IN
    (
      SELECT 
	  --the command to be executed: drop view
        'drop view '||view_name||' cascade constraint'     AS cmd,
        ROW_NUMBER() OVER (ORDER BY view_name)
                                       AS sofar,
        COUNT(*) OVER (PARTITION BY 1) AS totalwork,
        view_name                     AS target
      FROM user_views
      ORDER BY view_name
    )
  LOOP
    EXECUTE IMMEDIATE lv_cur.cmd;
    COMMIT;
  END LOOP;
END;
/

PURGE RECYCLEBIN;
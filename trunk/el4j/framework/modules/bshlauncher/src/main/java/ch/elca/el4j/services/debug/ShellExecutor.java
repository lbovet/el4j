package ch.elca.el4j.services.debug;

/**
 * The {@link ShellExecutor} interface.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philipp H. Oser (POS)
 */
public interface ShellExecutor {

	/**
	 * Invoke a method on the shell
	 * @param expr
	 * @return
	 */
	public ResultHolder eval(String expr);
	
}

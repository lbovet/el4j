/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.findbugs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.DeepSubtypeAnalysis;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.Location;
import edu.umd.cs.findbugs.ba.type.NullType;
import edu.umd.cs.findbugs.ba.type.TopType;
import edu.umd.cs.findbugs.ba.type.TypeDataflow;
import edu.umd.cs.findbugs.ba.type.TypeFrame;

/**
 * This detector searches for banned API calls.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class FindBannedMethodCalls implements Detector {

	private BugReporter bugReporter;
	
	private AnalysisContext analysisContext;

	private static final boolean DEBUG = false;

	private class BannedMethodCalls {
		private AnalysisContext m_analysisContext;

		private HashMap<String, Set<JavaClass>> m_exact;
		private Set<String> m_classes;

		public BannedMethodCalls(AnalysisContext classContext) {
			m_analysisContext = classContext;
			m_exact = new HashMap<String, Set<JavaClass>>();
			m_classes = new HashSet<String>();
		}

		public void addClass(String clazz) {
			if (clazz.endsWith(".*")) {
				m_classes.add(clazz.substring(0, clazz.length() - 2));
			} else {
				m_classes.add(clazz);
			}
		}

		public void addMethod(String clazz, String method) {
			try {
				if (m_exact.containsKey(method)) {
					Set<JavaClass> classes = m_exact.get(method);
					classes.add(m_analysisContext.lookupClass(clazz));
				} else {
					Set<JavaClass> classes = new HashSet<JavaClass>();
					classes.add(m_analysisContext.lookupClass(clazz));
					m_exact.put(method, classes);
				}
			} catch (Exception e) {
				bugReporter.logError("Could not find class " + clazz);
			}
		}

		public boolean isBanned(String clazz, String method) {
			for (String classPattern : m_classes) {
				if (clazz.startsWith(classPattern)) {
					return true;
				}
			}
			try {
				JavaClass javaClass = m_analysisContext.lookupClass(clazz);
				if (m_exact.containsKey(method)) {
					Set<JavaClass> classes = m_exact.get(method);
					for (JavaClass candidate : classes) {
						if (javaClass.instanceOf(candidate)
							|| javaClass.implementationOf(candidate)) {
							return true;
						}
					}
				}
			} catch (Exception e) {
				bugReporter.logError("Could not find class " + clazz);
			}
			return false;
		}
	}

	BannedMethodCalls m_banned;

	public FindBannedMethodCalls(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	public void init(ClassContext classContext) {
		if (analysisContext != null && analysisContext == classContext.getAnalysisContext()) {
			// analysis context is the same -> use cached BannedMethodCalls
			return;
		}
		m_banned = new BannedMethodCalls(classContext.getAnalysisContext());

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
				FindBannedMethodCalls.class.getClassLoader()
					.getResourceAsStream("bannedApis.list")));
			String str;
			while ((str = in.readLine()) != null) {
				if (str.startsWith("#") || !str.contains(":")) {
					continue;
				}
				String[] splitted = str.split(":");
				if (splitted.length < 2 || splitted[1].equals("*")) {
					m_banned.addClass(splitted[0]);
				} else {
					m_banned.addMethod(splitted[0], splitted[1]);
				}
			}
			in.close();
		} catch (IOException e) {
			bugReporter.logError("Could not load bannedApis.list");
		}
	}

	public void visitClassContext(ClassContext classContext) {
		init(classContext);
		Method[] methodList = classContext.getJavaClass().getMethods();

		for (Method method : methodList) {
			if (method.getCode() == null)
				continue;

			try {
				analyzeMethod(classContext, method);
			} catch (CFGBuilderException e) {
				bugReporter.logError("Detector " + this.getClass().getName()
					+ " caught exception", e);
			} catch (DataflowAnalysisException e) {
				// bugReporter.logError("Detector " + this.getClass().getName() + " caught exception", e);
			}
		}
	}

	private void analyzeMethod(ClassContext classContext, Method method)
		throws CFGBuilderException, DataflowAnalysisException {
		MethodGen methodGen = classContext.getMethodGen(method);
		if (methodGen == null)
			return;
		BitSet bytecodeSet = classContext.getBytecodeSet(method);
		if (bytecodeSet == null)
			return;
		// We don't adequately model instanceof interfaces yet
		if (bytecodeSet.get(Constants.INSTANCEOF)
			|| bytecodeSet.get(Constants.CHECKCAST))
			return;
		CFG cfg = classContext.getCFG(method);
		TypeDataflow typeDataflow = classContext.getTypeDataflow(method);
		ConstantPoolGen cpg = classContext.getConstantPoolGen();

		String sourceFile = classContext.getJavaClass().getSourceFileName();
		if (DEBUG) {
			String methodName = methodGen.getClassName() + "."
				+ methodGen.getName();
			System.out.println("Checking " + methodName);
		}

		for (Iterator<Location> i = cfg.locationIterator(); i.hasNext();) {
			Location location = i.next();
			InstructionHandle handle = location.getHandle();
			//int pc = handle.getPosition();
			Instruction ins = handle.getInstruction();

			if (!(ins instanceof InvokeInstruction))
				continue;

			InvokeInstruction invoke = (InvokeInstruction) ins;
			String mName = invoke.getMethodName(cpg);
			String cName = invoke.getClassName(cpg);

			if (!m_banned.isBanned(cName, mName)) {
				if (DEBUG) {
					System.out.println("Check method " + cName + "." + mName);
				}
				continue;
			}

			TypeFrame frame = typeDataflow.getFactAtLocation(location);
			if (!frame.isValid()) {
				// This basic block is probably dead
				continue;
			}
			Type operandType = frame.getTopValue();

			if (operandType.equals(TopType.instance())) {
				// unreachable
				continue;
			}
			if (!(operandType instanceof ReferenceType)) {
				// Shouldn't happen - illegal bytecode
				continue;
			}
			ReferenceType refType = (ReferenceType) operandType;

			if (refType.equals(NullType.instance())) {
				continue;
			}
			String refSig = refType.getSignature();

			SourceLineAnnotation sourceLineAnnotation = SourceLineAnnotation
				.fromVisitedInstruction(classContext, methodGen, sourceFile,
					handle);

			bugReporter.reportBug(new BugInstance(this,
				"ESAPI_BANNED_API_CALL", 1).addClassAndMethod(methodGen,
				sourceFile).addClass(
				DeepSubtypeAnalysis.getComponentClass(refSig)).addSourceLine(
				sourceLineAnnotation));

		}
	}

	public void report() { }

}

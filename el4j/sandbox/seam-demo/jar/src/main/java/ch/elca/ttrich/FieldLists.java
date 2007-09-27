package ch.elca.ttrich;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

/**
 * A class to compute field/column lists based on hibernate metadata as well
 * as given inclusion("shown") / exclusion ("hidden") field lists.
 * 
 * CAUTION: This code is (partially) hibernate specific, thus not portable!
 *
 * @author  Baeni Christoph (CBA)
 */
public class FieldLists {
	private SessionFactory m_SessionFactory;
	private EntityShortNameMapping m_shortNameMapping;
	
	private ResultCache cache = new ResultCache();
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		m_SessionFactory = sessionFactory;
	}
	
	public void setShortNameMapping(EntityShortNameMapping entityShortnameMapping) {
		m_shortNameMapping = entityShortnameMapping;
	}

	private String[] getCompleteFieldListInternal(String entityClassName) {
		if (m_SessionFactory == null) {
			throw new RuntimeException("Session factory not set.");
		}
		
		if (m_shortNameMapping == null) {
			throw new RuntimeException("Entity shortname mapping not set.");
		}
		
		ClassMetadata metadata;
		try {
			metadata = m_SessionFactory.getClassMetadata(Class.forName(entityClassName));
		} catch (ClassNotFoundException e) {
			return null;
		}
		
		String[] propertyNames = metadata.getPropertyNames();
		List<String> propertyList = new ArrayList(Arrays.asList(propertyNames));
		propertyList.remove("version");
		propertyList.remove("optimisticLockingVersion");
		String entityShortName = m_shortNameMapping.getShortName(entityClassName);
		Comparator humanizationComparator = new HumanizationComparator(entityShortName);
		Collections.sort(propertyList, humanizationComparator);
		
		return propertyList.toArray(new String[0]);
	}
	
	private String[] getCompleteFieldList(String entityClassName) {
		String cacheKey = cache.computeKey("getCompleteFieldList", entityClassName);
		
		if (!cache.doesExist(cacheKey)) {
			cache.store(cacheKey, getCompleteFieldListInternal(entityClassName));
		}
		
		return (String[])cache.lookup(cacheKey);
	}
	
	public String[] parseList(String fieldsString) {
		if (fieldsString.trim().equals("")) {
			return new String[0];
		}
		return fieldsString.trim().split(" *, *");
	}
	
	private String[] computeFieldListInternal(String entityClassName, String shown, String hidden) {
		String[] fieldArray;
		
		if ((shown == null) || shown.equals("")) {
			fieldArray = getCompleteFieldList(entityClassName);
		} else {
			fieldArray = parseList(shown);
		}
		
		LinkedHashSet<String> fields = new LinkedHashSet<String>(Arrays.asList(fieldArray));
		if ((hidden != null) && !hidden.equals("")) {
			fields.removeAll(Arrays.asList(parseList(hidden)));
		}
		
		return fields.toArray(new String[0]);
	}
	
	public String[] computeFieldList(String entityClassName, String shown, String hidden) {
		String cacheKey = cache.computeKey("computeFieldList", entityClassName, shown, hidden);
		
		if (!cache.doesExist(cacheKey)) {
			cache.store(cacheKey, computeFieldListInternal(entityClassName, shown, hidden));
		}
		
		return (String[])cache.lookup(cacheKey);
	}
	
	private TableColumn[] makeTableColumnArray(String[] fieldArray, boolean allLinked) {
		TableColumn[] columnArray = new TableColumn[fieldArray.length];
		
		for (int i = 0; i < fieldArray.length; i++) {
			columnArray[i] = new TableColumn(fieldArray[i], allLinked);
		}
		
		return columnArray;
	}
	
	private TableColumn[] computeColumnListInternal(String entityClassName, String shown, String hidden) {
		TableColumn[] columnArray;
		
		if ((shown == null) || shown.equals("")) {
			String[] fieldArray = getCompleteFieldList(entityClassName);
			columnArray = makeTableColumnArray(fieldArray, true);
		} else {
			String[] fieldArray = parseList(shown);
			columnArray = makeTableColumnArray(fieldArray, false);
		}
		
		LinkedHashSet<TableColumn> columns = new LinkedHashSet<TableColumn>(Arrays.asList(columnArray));
		if ((hidden != null) && !hidden.equals("")) {
			columns.removeAll(Arrays.asList(makeTableColumnArray(parseList(hidden), false)));
		}
		
		return columns.toArray(new TableColumn[0]);
	}
	
	public TableColumn[] computeColumnList(String entityClassName, String shown, String hidden) {
		String cacheKey = cache.computeKey("computeColumnList", entityClassName, shown, hidden);
		
		if (!cache.doesExist(cacheKey)) {
			cache.store(cacheKey, computeColumnListInternal(entityClassName, shown, hidden));
		}
		
		return (TableColumn[])cache.lookup(cacheKey);
	}
}

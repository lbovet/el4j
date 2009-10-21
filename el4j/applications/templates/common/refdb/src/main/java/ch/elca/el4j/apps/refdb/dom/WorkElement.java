/*
 * Copyright 2006 by ELCA Informatique SA
 * Av. de la Harpe 22-24, 1000 Lausanne 13
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ELCA Informatique SA. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with ELCA.
 */
package ch.elca.el4j.apps.refdb.dom;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.validator.AssertTrue;
import org.hibernate.validator.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;
import ch.elca.el4j.util.codingsupport.JodaTimeUtils;


/**
 * An element of work performed on a day. 
 * Logical equality is defined over all fields.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
@Entity
@Table(name = "WORKELEMENTS")
@SequenceGenerator(name = "keyid_generator", sequenceName = "workelement_sequence")
public class WorkElement extends AbstractIntKeyIntOptimisticLockingDto implements Serializable {

	/** The date on which the work was done. */
	private LocalDate m_day;
	
	/** The time this work started. */
	private LocalTime m_from;
	
	/** The time this work ended. */
	private LocalTime m_to;
	
	/**
	 * Empty constructor. 
	 */
	public WorkElement() { }

	/**
	 * @param day The day.
	 * @param from The start time.
	 * @param to The end time.
	 */
	public WorkElement(LocalDate day, LocalTime from, LocalTime to) {
		m_day = day;
		m_from = from;
		m_to = to;
	}

	/**
	 * Get the day.
	 * @return The day.
	 */
	@NotNull
	@Type(type = "org.joda.time.contrib.hibernate.PersistentLocalDate")
	public LocalDate getDay() {
		return m_day;
	}

	/**
	 * Get the from.
	 * @return The from.
	 */
	@NotNull
	@Column(name = "startOfWorkElement") 
	@Type(type = "org.joda.time.contrib.hibernate.PersistentLocalTimeAsTime")
	public LocalTime getFrom() {
		return m_from;
	}

	/**
	 * Get the to.
	 * @return The to.
	 */
	@NotNull
	@Column(name = "finishOfWorkElement")
	@Type(type = "org.joda.time.contrib.hibernate.PersistentLocalTimeAsTime")
	public LocalTime getTo() {
		return m_to;
	}

	/**
	 * Setter for day.
	 * @param day The new day to set.
	 */
	public void setDay(LocalDate day) {
		m_day = day;
	}

	/**
	 * Setter for from.
	 * @param from The new from to set.
	 */
	public void setFrom(LocalTime from) {
		m_from = from;
	}

	/**
	 * Setter for to.
	 * @param to The new to to set.
	 */
	public void setTo(LocalTime to) {
		m_to = to;
	}
	
	/**
	 * @return The work's duration as a double (1.0d = 1h)
	 */
	@Transient 
	public double getDurationInHours() {			
		if (m_from.compareTo(m_to) > 0) {
			return 0; 
		}
		return m_to.getHourOfDay() - m_from.getHourOfDay()
			+ (m_to.getMinuteOfHour() - m_from.getMinuteOfHour()) / 60d;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return JodaTimeUtils.getLocalDateString(m_day, "dd.MM.HH")  + " : " + JodaTimeUtils.getLocalTimeString(m_from, "HH:mm:ss")
			+ " - "	+ JodaTimeUtils.getLocalTimeString(m_to, "HH:mm:ss") + " " + super.toString();
	}
	
	/**
	 * Asserts that the start time is before the finish time.
	 * 
	 * @return true if the starting time of this WorkElement is before the finishing time.
	 */
	@Transient
	@AssertTrue
	public boolean startTimeBeforeFinishTime() {
		// make sure that none of the used values are null
		// if one of them should be null, return true as this
		// will anyway be handled at another place 
		if ((getFrom() == null) || (getTo() == null)) {
			return true;
		}
		return getFrom().isBefore(getTo());
	}	
}

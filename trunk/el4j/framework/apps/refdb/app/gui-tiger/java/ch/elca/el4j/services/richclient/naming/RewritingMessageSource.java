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
package ch.elca.el4j.services.richclient.naming;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * A MessageSource based on {@link MessageRewriter MessageRewriters}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */ 
public class RewritingMessageSource implements MessageSource {
    /** The base names. See {@link #setBasenames(String[])} */
    protected String[] m_basenames;
    
    /** A list of additional rules to be used by every locale. */
    protected MessageRewriter.Rule[] m_additionalRules;
    
    /** The already loaded rewriters. */
    protected Map<Locale, MessageRewriter> m_rewriters
        = new HashMap<Locale, MessageRewriter>();
    
    /** The logger. (duh!) */
    protected static Log s_logger 
        = LogFactory.getLog(RewritingMessageSource.class);
    
    /**
     * Like {@link
     * org.springframework.context.support.ResourceBundleMessageSource
     * #setBasenames(String[]).
     */
    public void setBasenames(String... basenames) {
        m_basenames = basenames;
    }
    
    /** 
     * Sets {@link #m_additionalRules}.
     */
    public void setAdditionalRules(MessageRewriter.Rule... rules) {
        m_additionalRules = rules;
    }

    /** 
     * Assembles the filename consisting of the given parts, separated by
     * '_'. 
     * @param parts the parts to assembles. A part may be null, in which case
     *              it is omitted.
     * @return the assembled filename
     */
    protected String assembleFileName(String... parts) {
        StringBuilder r = new StringBuilder();
        for (String p : parts) {
            if (StringUtils.hasText(p)) {
                r.append(p).append('_');
            }
        }
        return r.substring(0, r.length() - 1);
    }
    
    
    /**
     *  Creates a new MessageRewriter for the given locale.
     * @param loc .
     * @return the MessageRewriter created
     */
    // Checkstyle: EmptyBlock off
    protected MessageRewriter createRewriter(Locale loc) {
        ArrayList<Reader> ruleSources = new ArrayList<Reader>();
        for (String bn : m_basenames) {
            String filename = assembleFileName(
                bn,
                loc.getLanguage(),
                loc.getCountry(),
                loc.getVariant()
            );
            
            String fn = filename;
            do {
                Resource r = new ClassPathResource(
                    fn.replace('.', '/') + ".properties"
                );
                try {
                    Reader reader = new InputStreamReader(r.getInputStream());
                    if (s_logger.isDebugEnabled()) {
                        s_logger.debug("found: " + r);
                    }
                    ruleSources.add(reader);
                } catch (IOException e) {
                    // nothing to do
                }                    

                int i = fn.lastIndexOf('_');
                if (i == -1) { break; }
                fn = fn.substring(0, i);
            } while (true);
        }
        Collections.reverse(ruleSources);
        MessageRewriter rewriter = new MessageRewriter(ruleSources);
        rewriter.add(m_additionalRules);
        return rewriter;
    }
    // Checkstyle: EmptyBlock on

    
    /** 
     * Fetches the rewriter for a given locale, creating it if needed.
     * @param loc .
     * @return the rewriter
     **/
    private MessageRewriter getRewriter(Locale loc) {
        MessageRewriter r = m_rewriters.get(loc);
        if (r == null) {
            r = createRewriter(loc);
            m_rewriters.put(loc, r);
        }
        return r;
    }
    
    /** {@inheritDoc} */
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) 
        throws NoSuchMessageException {
        
        NoSuchMessageException ex = null;
        for (String key : resolvable.getCodes()) {
            try {
                return getMessage(key, resolvable.getArguments(), locale);
            } catch (NoSuchMessageException e) {
                s_logger.info(e);
                if (ex == null) {
                    ex = e;
                }
            }
        }
        Reject.ifNull(
            ex, "a non-empty list of codes is required to resolve a message"
        );
        if (resolvable.getDefaultMessage() != null) {
            return resolvable.getDefaultMessage();
        } else {
            throw ex;
        }
    }

    /** {@inheritDoc} */
    public String getMessage(String code, Object[] args, Locale locale)
        throws NoSuchMessageException {
        return getRewriter(locale).resolve(code, args);
    }

    /** {@inheritDoc} */
    public String getMessage(String code, Object[] args, 
                             String defaultMessage, Locale locale) {
        try {
            return getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return defaultMessage;
        }
    }
}

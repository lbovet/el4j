<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="yes" encoding="US-ASCII"/>
  <xsl:param name="set"/>
  <xsl:template match="testsuites">
    <xsl:variable name="testCount" select="sum(testsuite/@tests)"/>
    <xsl:variable name="errorCount" select="sum(testsuite/@errors)"/>
    <xsl:variable name="failureCount" select="sum(testsuite/@failures)"/>
    <xsl:variable name="timeCount" select="sum(testsuite/@time)"/>
    <xsl:variable name="successRate" select="($testCount - $failureCount - $errorCount) div $testCount"/>

    <p class="header"><xsl:value-of select="$set"/>: 
       <xsl:text disable-output-escaping="yes"><![CDATA[<a href="junit/]]></xsl:text>
       <xsl:value-of select="$set"/>
       <xsl:text disable-output-escaping="yes"><![CDATA[/index.html">JUnit execution</a>]]></xsl:text>
    </p>
    <table>
    <tr>
      <th>Tests</th>
      <th>Failures</th>
      <th>Errors</th>
      <th>Success rate</th>
      <th>Time</th>
    </tr>
    <tr>
      <xsl:attribute name="class">
        <xsl:choose>
          <xsl:when test="$failureCount &gt; 0">Failure</xsl:when>
          <xsl:when test="$errorCount &gt; 0">Error</xsl:when>
        </xsl:choose>
      </xsl:attribute>
      <td><xsl:value-of select="$testCount"/></td>
      <td><xsl:value-of select="$failureCount"/></td>
      <td><xsl:value-of select="$errorCount"/></td>
      <td>
        <xsl:call-template name="display-percent">
          <xsl:with-param name="value" select="$successRate"/>
        </xsl:call-template>
      </td>
      <td>
        <xsl:call-template name="display-time">
          <xsl:with-param name="value" select="$timeCount"/>
        </xsl:call-template>
      </td>
    </tr>
    </table>
  </xsl:template>

  <xsl:template name="display-time">
    <xsl:param name="value"/>
    <xsl:value-of select="format-number($value,'0.000')"/>
  </xsl:template>

  <xsl:template name="display-percent">
    <xsl:param name="value"/>
    <xsl:value-of select="format-number($value,'0.00%')"/>
  </xsl:template>

</xsl:stylesheet>

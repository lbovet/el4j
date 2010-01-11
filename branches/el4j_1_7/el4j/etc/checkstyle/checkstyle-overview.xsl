<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="yes" encoding="US-ASCII"/>
  <xsl:param name="set"/>
  <xsl:template match="/">
    <xsl:variable name="totalFiles" select="count(descendant::file)"/>
    <xsl:variable name="errorFiles" select="count(descendant::file[error])"/>
    <xsl:variable name="totalErrors" select="count(descendant::error)"/>
    <xsl:variable name="errorRate" select="count(descendant::error) div count(descendant::file)"/>

    <p class="header"><xsl:value-of select="$set"/>:
       <xsl:text disable-output-escaping="yes"><![CDATA[<a href="checkstyle/]]></xsl:text>
       <xsl:value-of select="$set"/>
       <xsl:text disable-output-escaping="yes"><![CDATA[/index.html">coding style checks</a>]]></xsl:text>
    </p>
    <table>
      <tr>
        <th>Total files checked</th>
        <th>Files with errors</th>
        <th>Total errors</th>
        <th>Errors per file</th>
      </tr>
      <tr>
       <td><xsl:value-of select="$totalFiles"/></td>
       <td><xsl:value-of select="$errorFiles"/></td>
       <td><xsl:value-of select="$totalErrors"/></td>
       <td><xsl:value-of select="$errorRate"/></td>
      </tr>
    </table>
  </xsl:template>
</xsl:stylesheet>

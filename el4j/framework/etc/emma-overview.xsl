<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="yes" encoding="US-ASCII"/>
  <xsl:param name="set"/>

  <xsl:template match="all">
    <p class="header"><xsl:value-of select="$set"/>: EMMA summary</p>
    <table>
    <tr>
      <xsl:for-each select="coverage">
        <xsl:call-template name="coverage-type"/> 
      </xsl:for-each>
    </tr>
    <tr>
      <xsl:for-each select="coverage">
        <xsl:call-template name="coverage-value"/>
      </xsl:for-each>
    </tr>
    </table>
  </xsl:template>

  <xsl:template name="coverage-type">
    <th><xsl:value-of select="@type"/></th>
  </xsl:template>

  <xsl:template name="coverage-value">
    <td><xsl:value-of select="@value"/></td>
  </xsl:template>

</xsl:stylesheet>

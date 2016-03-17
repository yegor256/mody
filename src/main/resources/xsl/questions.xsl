<?xml version="1.0"?>
<!--
 * Copyright (c) 2015, Yegor Bugayenko
 * All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" version="1.0">
    <xsl:output method="html" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes" />
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template match="page" mode="head">
        <title>
            <xsl:text>mody</xsl:text>
        </title>
        <link id='favicon' rel="shortcut icon" type="image/png"
            data-origin="{links/link[@rel='favicon']/@href}"
            href="{links/link[@rel='favicon']/@href}?{@date}{@sla}"/>
        <script type="text/javascript">
            setInterval(
                function() {
                    var link = document.getElementById('favicon');
                    link.setAttribute(
                        'href',
                        link.getAttribute('data-origin') + '?' + new Date().getTime()
                    );
                },
                60 * 1000
            );
        </script>
    </xsl:template>
    <xsl:template match="page" mode="body">
        <p>
            <xsl:text>@</xsl:text>
            <xsl:value-of select="identity/login"/>
            <xsl:text> | </xsl:text>
            <xsl:value-of select="version/name"/>
            <xsl:text> | </xsl:text>
            <a href="{links/link[@rel='takes:logout']/@href}">
                <xsl:text>logout</xsl:text>
            </a>
        </p>
        <xsl:apply-templates select="questions"/>
    </xsl:template>
    <xsl:template match="questions">
        <xsl:apply-templates select="question"/>
        <xsl:if test="count(question) &lt; @total">
            <p>
                <xsl:text>There are </xsl:text>
                <xsl:value-of select="@total - count(question)"/>
                <xsl:text>+ more questions below.</xsl:text>
            </p>
        </xsl:if>
    </xsl:template>
    <xsl:template match="question">
        <p>
            <code><xsl:value-of select="coords"/></code>
            <xsl:text> </xsl:text>
            <xsl:value-of select="count"/>
            <xsl:text> time</xsl:text>
            <xsl:if test="count &gt; 1">
                <xsl:text>s</xsl:text>
            </xsl:if>
        </p>
        <p>
            <xsl:value-of select="text" disable-output-escaping="yes"/>
        </p>
        <form action="{links/link[@rel='answer']/@href}" method="post">
            <input name="text" type="text" style="width:100%">
                <xsl:if test="position() = 1">
                    <xsl:attribute name="autofocus">autofocus</xsl:attribute>
                </xsl:if>
                <xsl:if test="guess">
                    <xsl:attribute name="value">
                        <xsl:value-of select="guess"/>
                    </xsl:attribute>
                </xsl:if>
            </input>
            <input name="coords" type="hidden" value="{coords}"/>
            <button type="submit">post</button>
        </form>
    </xsl:template>
</xsl:stylesheet>

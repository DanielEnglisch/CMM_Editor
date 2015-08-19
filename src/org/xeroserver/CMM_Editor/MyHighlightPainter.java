package org.xeroserver.CMM_Editor;

import java.awt.Color;

import javax.swing.text.DefaultHighlighter;

class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter
{
    public MyHighlightPainter(Color color)
    {
        super(color);
    }
}
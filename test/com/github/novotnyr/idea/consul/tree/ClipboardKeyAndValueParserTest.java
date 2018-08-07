package com.github.novotnyr.idea.consul.tree;

import com.github.novotnyr.idea.consul.util.ClipboardKeyAndValueParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ClipboardKeyAndValueParserTest {
    @Test
    public void testSingleLinePaste() {
        String clipboardContents = "key=value";
        String[] split = clipboardContents.split("=", 2);

        Assert.assertEquals("key", split[0]);
        Assert.assertEquals("key", split[1]);
    }

    @Test
    public void testMultilinePaste() {
        String clipboardContents = "key=value\nanotherKey=anotherValue";
        ClipboardKeyAndValueParser clipboardKeyAndValueParser = new ClipboardKeyAndValueParser(new KeyAndValue("root"));

        List<KeyAndValue> keyAndValues = clipboardKeyAndValueParser.parseClipboard(clipboardContents);

        Assert.assertEquals(2, keyAndValues.size());

        Assert.assertEquals("key", keyAndValues.get(0).getKey());
        Assert.assertEquals("value", keyAndValues.get(0).getValue());
    }

    @Test
    public void testMultilinePasteWithEmptyLine() {
        String clipboardContents = "key=value\nanotherKey=anotherValue\n";
        ClipboardKeyAndValueParser clipboardKeyAndValueParser = new ClipboardKeyAndValueParser(new KeyAndValue("root"));

        List<KeyAndValue> keyAndValues = clipboardKeyAndValueParser.parseClipboard(clipboardContents);

        Assert.assertEquals(2, keyAndValues.size());

        Assert.assertEquals("key", keyAndValues.get(0).getKey());
        Assert.assertEquals("value", keyAndValues.get(0).getValue());
    }
    @Test
    public void testMultilinePasteWithWrongSyntax() {
        String clipboardContents = "key=value\nWRONGDATA\n";
        ClipboardKeyAndValueParser clipboardKeyAndValueParser = new ClipboardKeyAndValueParser(new KeyAndValue("root"));

        List<KeyAndValue> keyAndValues = clipboardKeyAndValueParser.parseClipboard(clipboardContents);

        Assert.assertEquals(1, keyAndValues.size());

        Assert.assertEquals("key", keyAndValues.get(0).getKey());
        Assert.assertEquals("value", keyAndValues.get(0).getValue());
    }
}


package com.github.novotnyr.idea.consul.tree;

import org.junit.Assert;
import org.junit.Test;

public class KeyAndValueTest {
    @Test
    public void test() throws Exception {
        KeyAndValue keyAndValue = new KeyAndValue("lorem/ipsum/dolor/sit/", "amet");
        String parentFqn = keyAndValue.getParentFullyQualifiedKey();
        Assert.assertEquals("lorem/ipsum/dolor/", parentFqn);
    }

    @Test
    public void getParentFullyQualifiedKeyWithRootParent() throws Exception {
        KeyAndValue keyAndValue = new KeyAndValue("lorem", "amet");
        String parentFqn = keyAndValue.getParentFullyQualifiedKey();
        Assert.assertEquals("", parentFqn);
    }

    @Test
    public void getParentFullyQualifiedKey() throws Exception {
        KeyAndValue keyAndValue = new KeyAndValue("lorem/ipsum/", "amet");
        String parentFqn = keyAndValue.getParentFullyQualifiedKey();
        Assert.assertEquals("lorem/", parentFqn);
    }
}
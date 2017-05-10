package com.github.novotnyr.idea.consul.tree;

import com.github.novotnyr.idea.consul.Consul;
import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import org.junit.Test;

import javax.swing.tree.DefaultMutableTreeNode;

public class ConsulTreeModelTest {
    @Test
    public void test() throws Exception {
        ConsulTreeModel consulTreeModel = new ConsulTreeModel(null, new Consul(new ConsulConfiguration()));
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) consulTreeModel.getNode(new KeyAndValue("config/default/multitenancy/ft"));

        KeyAndValue userObject = (KeyAndValue) node.getUserObject();
        System.out.println(userObject.getFullyQualifiedKey());
    }
}
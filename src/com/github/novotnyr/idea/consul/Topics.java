package com.github.novotnyr.idea.consul;

import com.github.novotnyr.idea.consul.config.ConsulConfiguration;
import com.github.novotnyr.idea.consul.tree.KeyAndValue;
import com.intellij.util.messages.Topic;

public class Topics {
    public interface RefreshTree {

        Topic<RefreshTree> REFRESH_TREE_TOPIC = Topic.create("Refresh Tree", RefreshTree.class);

        void refreshTree();
    }

    public interface ConsulConfigurationChanged {
        Topic<ConsulConfigurationChanged> CONSUL_CONFIGURATION_CHANGED_TOPIC = Topic.create("Consul configuration changed", ConsulConfigurationChanged.class);

        void consulConfigurationChanged(ConsulConfiguration newConfiguration);
    }

    public interface KeyValueChanged {
        Topic<KeyValueChanged> KEY_VALUE_CHANGED = Topic.create("Value changed", KeyValueChanged.class);

        void keyValueChanged(KeyAndValue changedKeyAndValue);
    }

    public interface ConsulTreeSelectionChanged {
        Topic<ConsulTreeSelectionChanged> CONSUL_TREE_SELECTION_CHANGED = Topic.create("Consul Tree Selection Changed", ConsulTreeSelectionChanged.class);

        void consulTreeSelectionChanged(KeyAndValue changedKeyAndValue);
    }
}

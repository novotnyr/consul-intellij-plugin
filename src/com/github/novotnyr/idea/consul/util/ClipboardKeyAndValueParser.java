package com.github.novotnyr.idea.consul.util;

import com.github.novotnyr.idea.consul.tree.KeyAndValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ClipboardKeyAndValueParser {
    private final KeyAndValue pasteDestinationKeyAndValue;

    public ClipboardKeyAndValueParser(KeyAndValue pasteDestinationKeyAndValue) {
        this.pasteDestinationKeyAndValue = pasteDestinationKeyAndValue;
    }

    public List<KeyAndValue> parseClipboard(String clipboardContents) {
        if (isSingleLine(clipboardContents)) {
            return parseKeyAndValue(clipboardContents)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        } else {
            List<KeyAndValue> result = new ArrayList<>();
            Scanner scanner = new Scanner(clipboardContents);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                parseKeyAndValue(line)
                        .ifPresent(result::add);
            }
            return result;
        }
    }

    private boolean isSingleLine(String clipboardContents) {
        return !clipboardContents.contains("\n");
    }

    public Optional<KeyAndValue> parseKeyAndValue(String line) {
        String[] components = line.split("=", 2);
        if (components.length != 2) {
            return Optional.empty();
        }
        KeyAndValue kv = new KeyAndValue(this.pasteDestinationKeyAndValue.getFullyQualifiedKey() + components[0], components[1]);
        return Optional.of(kv);
    }
}

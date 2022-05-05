package de.xnonymous.usefulapi.structure;

public class NameableRegistry<t extends INameable> extends Registry<t> {

    public t getByName(String name) {
        return this.objects.stream().filter(n -> n.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
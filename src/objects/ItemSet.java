package objects;

import java.util.List;

public class ItemSet {
    List<Item> items;
    int maxCapacity;

    public ItemSet(List<Item> items, int maxCapacity) {
        this.items = items;
        this.maxCapacity = maxCapacity;
    }

    @Override
    public String toString() {
        return "ItemSet{" +
                "items=" + items +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}

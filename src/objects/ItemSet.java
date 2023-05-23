package objects;

import java.util.List;

public class ItemSet {
    List<Item> items;
    int itemSize;
    int maxCapacity;

    public ItemSet(List<Item> items, int maxCapacity) {
        this.items = items;
        this.itemSize = items.size();
        this.maxCapacity = maxCapacity;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getItemSize() {
        return itemSize;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public String toString() {
        return "ItemSet{" +
                "items=" + items +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}

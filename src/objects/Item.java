package objects;

public class Item {
    int value;
    int weight;

    public Item(int value, int weight) {
        this.value = value;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Item{" +
                "value=" + value +
                ", weight=" + weight +
                '}';
    }
}

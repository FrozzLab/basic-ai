import objects.Item;
import objects.ItemSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class DataProcessor {
    public static ItemSet processFileIntoItemSet(String fileName) {
        List<Item> items;
        Path filePath = Path.of(fileName);
        int maxCapacity;

        try (Stream<String> fileStream = Files.lines(filePath)) {
            maxCapacity = fileStream
                    .findFirst()
                    .map(Integer::parseInt)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Stream<String> fileStream = Files.lines(filePath)) {
            items = fileStream
                    .skip(1)
                    .map(l -> new Item(Integer.parseInt(l.split(" ")[0]),
                                                 Integer.parseInt(l.split(" ")[1])))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ItemSet(items, maxCapacity);
    }
}

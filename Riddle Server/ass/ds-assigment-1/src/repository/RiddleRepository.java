package repository;

import domain.Riddle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RiddleRepository {

    private List<Riddle> riddles = new ArrayList<Riddle>();

    public RiddleRepository() {
        riddles.add(new Riddle("What has roots as nobody sees,\n" +
                "Is taller than trees\n" +
                "Up, up it goes,\n" +
                "And yet never grows?", "Mountain"));
        riddles.add(new Riddle("This thing all things devours:\n" +
                "Birds, beasts, trees, flowers;\n" +
                "Gnaws iron, bites steel;\n" +
                "Grinds hard stones to meal;\n" +
                "Slays king, ruins town,\n" +
                "And beats high mountain down.", "Time"));
        riddles.add(new Riddle("Thirty white horses on a red hill,\n" +
                "First they champ,\n" +
                "Then they stamp,\n" +
                "Then they stand still.", "Teeth"));
        riddles.add(new Riddle("Alive without breath,\n" +
                "As cold as death;\n" +
                "Never thirsty, ever drinking,\n" +
                "All in mail never clinking", "Fish"));
    }

    public List<Riddle> findAll() {
        return riddles;
    }

    public int countAllRiddles() {
        return riddles.size();
    }

    public Riddle getRiddle(int index) {
        if (riddles.size() <= index) {
            throw new IllegalArgumentException();
        }

        return riddles.get(index);
    }


}

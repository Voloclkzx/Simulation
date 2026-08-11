import karty.units.Rock;
import karty.units.Tree;
import karty.utils.PathFinder;
import karty.map.Coordinates;
import karty.map.GameMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

public class TestPathFinder {
    private GameMap map;
    @Before
    public void init() {
        this.map = new GameMap(5, 5);
    }
    @Test
    public void testEmpty() {
        Optional<List<Coordinates>> pathOptional = PathFinder.findPath(map, new Coordinates(1, 1), entity -> entity instanceof Rock);
        Assert.assertTrue(pathOptional.isEmpty());
    }

    @Test
    public void testRock() {
        map.put(new Coordinates(5, 5), new Rock());
        map.put(new Coordinates(4, 4), new Tree());
        map.put(new Coordinates(5, 4), new Tree());
//        map.put(new Coordinates(2, 4), new Rock());
        Optional<List<Coordinates>> pathOptional = PathFinder.findPath(map, new Coordinates(1, 1), entity -> entity instanceof Rock);
        System.out.println(pathOptional);
        Assert.assertEquals(5, pathOptional.get().size());
    }

}

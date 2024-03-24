import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class MyMapTest {

    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String KEY_EXISTS = "key1";
    private static final String KEY_NOT_EXISTS = "someKey";
    private static final int COUNT_OBJECTS = 100000;

    private MyMap<Object, Object> map;

    @Before
    public void setUp() throws Exception {
        map = new MyHashMap<>();
    }

    @Test
    public void whenAddOneObjectThenSizeShouldBeIncrease() {
        assertEquals(0, map.size());
        map.put(KEY, VALUE);
        assertEquals(1, map.size());
    }

    @Test
    public void whenAddManyObjectThenSizeShouldBeIncrease() {
        assertEquals(0, map.size());
        addManyObjects();
        assertEquals(COUNT_OBJECTS, map.size());
    }


    @Test
    public void whenGetExistsValueByKeyThenReturnThisValue() {
        map.put(KEY, VALUE);
        assertEquals(VALUE, map.get(KEY));
    }

    @Test
    public void whenKeyNotExistsThenReturnNull() {
        map.put(KEY, VALUE);
        assertNull(map.get(KEY + 1));
    }


    @Test
    public void whenKeyExistsThenReturnTrue() {
        map.put(KEY, VALUE);
        assertTrue(map.containsKey(KEY));
    }

    @Test
    public void whenKeyNotExistsThenReturnFalse() {
        assertFalse(map.containsKey(KEY));
    }

    @Test
    public void whenRemoveExistsElementThenReturnTrue() {
        map.put(KEY, VALUE);
        assertTrue(map.remove(KEY));
    }

    @Test
    public void whenClearMapThanSizeMustBe0() {
        addManyObjects();
        assertEquals(COUNT_OBJECTS, map.size());
        map.clear();
        assertEquals(0, map.size());
    }


    @Test(expected = IllegalArgumentException.class)
    public void whenCreateMyMapWithIncorrectCapacity() {
        map = new MyHashMap<>(-1, 0.75f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCreateMyMapWithIncorrectLoadFactor() {
        map = new MyHashMap<>(20, 1.1f);
    }

    @Test
    public void whenRemoveNotExistKeyThanReturnFalse() {
        addManyObjects();
        boolean actual = map.remove(KEY_NOT_EXISTS);
        assertFalse(actual);
    }

    @Test
    public void whenRemoveExistKeyThanReturnTrue() {
        addManyObjects();
        int actualSize = map.size();
        assertEquals(COUNT_OBJECTS, actualSize);
        boolean actual = map.remove(KEY_EXISTS);
        actualSize = map.size();
        assertEquals(COUNT_OBJECTS - 1, actualSize);
        assertTrue(actual);
    }

    @Test
    public void whenExistsValueThanReturnTrue() {
        map.put(KEY, VALUE);
        boolean actual = map.containsValue(VALUE);
        assertTrue(actual);
    }

    @Test
    public void whenGetNotExistsValueThanReturnFalse() {
        boolean actual = map.containsValue(VALUE);
        assertFalse(actual);
    }

    @Test
    public void whenMapEmptyThanReturnTrue() {
        assertTrue(map.isEmpty());
    }

    @Test
    public void whenMapNotEmptyThanReturnFalse() {
        map.put(KEY, VALUE);
        assertFalse(map.isEmpty());
    }

    @Test
    public void whenGetCollectionKeysThanReturnAllSavedKeys() {
        Set<Object> expect = new HashSet<>();
        createCollectionEntry().forEach(el -> expect.add(el.getKey()));
        addManyObjects();
        Set<Object> actual = map.keySet();
        assertEquals(expect, actual);
    }


    private void addManyObjects() {
        long before = System.currentTimeMillis();
        createCollectionEntry().forEach(el -> map.put(el.getKey(), el.getValue()));
        long after = System.currentTimeMillis();
        System.out.println("Время добавления " + COUNT_OBJECTS + " объектов = " + (after - before));
    }

    private Set<MyMap.Entry> createCollectionEntry() {
        Set<MyMap.Entry> entries = new HashSet<>();
        for (int i = 0; i < COUNT_OBJECTS; i++) {
            final int temp = i;
            entries.add(new MyMap.Entry() {
                @Override
                public Object getKey() {
                    return KEY + temp;
                }

                @Override
                public Object getValue() {
                    return VALUE + temp;
                }

                @Override
                public MyMap.Entry next() {
                    return null;
                }
            });
        }
        return entries;
    }
}
import java.util.*;

/**
 * @param <K> Тип ключа
 * @param <V> тип значения
 * @author Александр Трофимов
 * Реализация интерфейса MyMap с использованием хэш-таблицы.
 * Позволяет хранить пары ключ-значение.
 */

public class MyHashMap<K, V> implements MyMap<K, V> {

    /**
     * Размер хещ-таблицы по умолчанию.
     */
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * Коэффициент увеличения размера хеш-таблицы.
     */
    private final static byte MULTIPLIER = 2;

    /**
     * Процент занятого пространства в хэш-таблице при превышении которого требуется увеличение его размера.
     */
    private float loadFactor = 0.75f;
    /**
     * Таблица хранения объектов.
     */
    private Entry<K, V>[] array;
    /**
     * Количество сохраненных объектов.
     */
    private int size;


    /**
     * Конструктор без параметров, инициализирует массив по умолчанию.
     */
    public MyHashMap() {
        array = new Entry[DEFAULT_CAPACITY];
    }

    /**
     * Конструктор с параметрами, инициализирует массив указанным размером и коэффициентом загрузки.
     *
     * @param capacity   Максимальная емкость.
     * @param loadFactor Коэффициент загрузки в пределах от 0 до 1. (по умолчанию равен 0,75)
     */

    public MyHashMap(int capacity, float loadFactor) {
        if (loadFactor < 0 || loadFactor > 1) {
            throw new IllegalArgumentException("Неверный процент загрузки: " + loadFactor);
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Неверный размер: " + capacity);
        }
        this.loadFactor = loadFactor;
        array = new Entry[capacity];
    }

    /**
     * Метод для добавления элемента по ключу и значению в хэш-таблицу.
     * Если количество занятых ячеек превышает заданный loadFactor, происходит увеличение размера массива.
     * Если по указанному ключу уже есть значение, оно перезаписывается новым значением.
     *
     * @param key   ключ элемента
     * @param value значение элемента
     */
    @Override
    public void put(K key, V value) {
        Entry<K, V> entryToAdd = new Entry<>(key, value, null);
        int count = countInitializedCells();
        if (count >= array.length * loadFactor) {
            increaseArray();
        }
        int index = getIndex(key);

        Entry<K, V> current = array[index];
        if (current == null) {
            array[index] = entryToAdd;
            size++;
        } else {
            while (current.next != null) {
                if (equalsKey(key, current.getKey())) {
                    current.value = value;
                    return;
                }
                current = current.next;
            }
            current.next = entryToAdd;
            size++;
        }
    }

    /**
     * Метод для получения значения элемента по ключу из хэш-таблицы.
     *
     * @param key ключ элемента
     * @return значение элемента, соответствующее указанному ключу, или null, если элемент с таким ключом отсутствует
     */
    @Override
    public V get(K key) {
        int index = getIndex(key);
        Entry<K, V> currentEntry = array[index];
        V value = null;
        while (currentEntry != null) {
            if (currentEntry.key.equals(key)) {
                value = currentEntry.value;
                break;
            } else {
                currentEntry = currentEntry.next;
            }
        }
        return value;
    }


    /**
     * Удаляет элемент по ключу из таблицы хэширования.
     *
     * @param key ключ элемента, который нужно удалить
     * @return true, если элемент успешно удален, иначе false
     */
    @Override
    public boolean remove(K key) {
        int index = getIndex(key);
        Entry<K, V> current = array[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if (equalsKey(current.key, key)) {
                if (prev == null) {
                    array[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    /**
     * Проверяет наличие ключа в хэш-таблице.
     *
     * @param key ключ, который необходимо проверить
     * @return true, если ключ найден, иначе false
     */
    @Override
    public boolean containsKey(Object key) {
        int index = getIndex(key);
        Entry<K, V> currentEntry = array[index];

        while (currentEntry != null) {
            if (currentEntry.key.equals(key)) {
                return true;
            }
            currentEntry = (Entry<K, V>) currentEntry.next();
        }
        return false;
    }


    /**
     * Проверяет наличие значения в хэш-таблице.
     *
     * @param value значение, которое необходимо проверить
     * @return true, если значение найдено, иначе false
     */
    @Override
    public boolean containsValue(Object value) {
        return values().stream().findFirst().isPresent();
    }

    /**
     * Проверяет наличие записей в хэш-таблице.
     *
     * @return true, если значение не найдено, иначе false
     */
    @Override
    public boolean isEmpty() {
        return !(size > 0);
    }

    /**
     * Возвращает множество ключей хэш-таблицы.
     *
     * @return возвращает множество ключей.
     */
    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        getEntrySet().forEach(el -> keys.add(el.getKey()));
        return keys;
    }


    /**
     * Возвращает коллекцию значений хэш-таблицы.
     *
     * @return коллекция значений.
     */
    @Override
    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        Set<MyMap.Entry<K, V>> entrys = getEntrySet();
        entrys.forEach(el -> values.add(el.getValue()));
        return values;
    }

    /**
     * Возвращает множество элементов хэш-таблицы.
     *
     * @return множество элементов
     */
    @Override
    public Set<MyMap.Entry<K, V>> entrySet() {
        return getEntrySet();
    }

    /**
     * Очищает хэш-таблицу, устанавливая массив элементов в начальное состояние и обнуляя размер.
     */
    @Override
    public void clear() {
        array = new Entry[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * Возвращает количество сохраненных элементов внутри хэш-таблицы.
     */
    @Override
    public int size() {
        return this.size;
    }


    private int countInitializedCells() {
        int count = 0;
        for (Entry entry : array) {
            if (entry != null) {
                count++;
            }
        }
        return count;
    }

    private Set<MyMap.Entry<K, V>> getEntrySet() {
        Set<MyMap.Entry<K, V>> entries = new HashSet<>();
        Entry<K, V> current;
        for (Entry entry : array) {
            if (entry == null) {
                continue;
            }
            current = entry;
            entries.add(current);
            while (current.next() != null) {
                current = current.next;
                entries.add(current);
            }
        }
        return entries;
    }

    private int getIndex(Object key) {
        return Math.abs(key.hashCode() % array.length);
    }

    private boolean equalsKey(K key, K key1) {
        return equalsHashCode(key, key1) && key.equals(key1);
    }

    private void increaseArray() {
        Set<MyMap.Entry<K, V>> entries = entrySet();
        array = new Entry[(int) (array.length * MULTIPLIER)];
        size = 0;
        entries.forEach(el -> put(el.getKey(), el.getValue()));
    }

    private boolean equalsHashCode(K key, K key1) {
        return key.hashCode() == key1.hashCode();
    }


    /**
     * Внутренний класс, представляющий элемент карты.
     * Реализует интерфейс MyMap.Entry<K, V>.
     *
     * @param <K> тип ключа.
     * @param <V> тип значения.
     */
    private static class Entry<K, V> implements MyMap.Entry<K, V> {
        /**
         * Ключ элемента.
         */
        private final K key;
        /**
         * Значение элемента.
         */
        private V value;
        /**
         * Ссылка на следующий элемент.
         */
        private Entry<K, V> next;


        /**
         * Конструктор для создания нового элемента.
         *
         * @param key   ключ элемента
         * @param value значение элемента
         * @param next  ссылка на следующий элемент
         */
        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        /**
         * Возвращает ключ элемента.
         *
         * @return ключ элемента
         */
        @Override
        public K getKey() {
            return key;
        }

        /**
         * Возвращает значение элемента.
         *
         * @return значение элемента
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Возвращает ссылку на следующий элемент карты.
         *
         * @return ссылка на следующий элемент карты
         */
        @Override
        public MyMap.Entry<K, V> next() {
            return next;
        }
    }
}

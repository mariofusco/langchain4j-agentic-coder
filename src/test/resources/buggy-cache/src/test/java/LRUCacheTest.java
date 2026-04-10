import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LRUCacheTest {

    @Test
    void put_and_get() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1);
        cache.put("b", 2);
        assertEquals(1, cache.get("a"));
        assertEquals(2, cache.get("b"));
    }

    @Test
    void get_missing_key_returns_null() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        assertNull(cache.get("missing"));
    }

    @Test
    void put_overwrites_existing_value() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("b", 99);
        assertEquals(99, cache.get("b"));
        assertEquals(1, cache.get("a"));
    }

    @Test
    void size_tracks_entries() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        assertEquals(0, cache.size());
        cache.put("a", 1);
        assertEquals(1, cache.size());
        cache.put("b", 2);
        assertEquals(2, cache.size());
    }

    @Test
    void eviction_removes_lru_item() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4); // evicts "a"
        assertNull(cache.get("a"));
        assertEquals(4, cache.get("d"));
    }

    @Test
    void capacity_of_one() {
        LRUCache<String, Integer> cache = new LRUCache<>(1);
        cache.put("a", 1);
        assertEquals(1, cache.get("a"));
        cache.put("b", 2);
        assertNull(cache.get("a"));
        assertEquals(2, cache.get("b"));
    }

    @Test
    void multiple_evictions() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3); // evicts "a"
        cache.put("d", 4); // evicts "b"
        assertNull(cache.get("a"));
        assertNull(cache.get("b"));
        assertEquals(3, cache.get("c"));
        assertEquals(4, cache.get("d"));
    }

    @Test
    void accessed_item_survives_eviction() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1);  // LRU order (oldest first): a
        cache.put("b", 2);  // LRU order: a, b
        cache.put("c", 3);  // LRU order: a, b, c

        // Access "a" so it becomes the most recently used item.
        // LRU order should now be: b, c, a
        assertEquals(1, cache.get("a"));

        // Adding "d" must evict "b" (the least recently used).
        cache.put("d", 4);

        // "a" was recently accessed and must still be present.
        assertEquals(1, cache.get("a"));
    }
}

package top.shenjack;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

@SuppressWarnings("All")
public class MallocRand {

    public static final int MALLOC_BATCH_SIZE = 16 * 16 * 128;

    private static Field field;
    private static final Unsafe unsafe;

    static {
        try {
            field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private int counter = 0;
    private long address = 0;
    private final byte[] buffer = new byte[MALLOC_BATCH_SIZE];

    public MallocRand() {
        this.refresh();
    }

    public void refresh() {
        if (this.address == 0) {
            this.address = this.unsafe.allocateMemory(MALLOC_BATCH_SIZE);
            this.readBytes();
            return;
        }
        this.unsafe.freeMemory(address);
        this.address = this.unsafe.allocateMemory(MALLOC_BATCH_SIZE);
        this.readBytes();
    }

    private void readBytes() {
        for (int i = 0; i < MALLOC_BATCH_SIZE; i++) {
            buffer[i] = this.unsafe.getByte(address + i);
        }
    }

    public double sample() {
        if (this.counter >= MALLOC_BATCH_SIZE) {
            this.refresh();
            this.counter = 0;
        }
        return this.buffer[this.counter++];
    }

    public double sample_pos(int x, int y, int z) {
        if (this.address == 0) {
            this.refresh();
        }
        // Normalize coordinates into valid ranges
        x = ((x % 16) + 16) % 16;
        y = ((y % 128) + 128) % 128;
        z = ((z % 16) + 16) % 16;

        // Compute linear index in a 16 (x) x 128 (y) x 16 (z) layout
        // Ensure we read within the current buffer batch, refresh if needed.
        int idx = x + (y * 16) + (z * 16 * 128);

        // If the computed index exceeds the current buffer size, refresh to a new batch
        // and fold the index into the current buffer range.
        if (idx >= MALLOC_BATCH_SIZE) {
            this.refresh();
            idx = idx % MALLOC_BATCH_SIZE;
        }

        // Return the byte value as a double
        return this.buffer[idx];
    }

    /**
     * 从当前缓冲区采样一个整数，并将其映射到 [min, max] 范围（包含端点）。
     * 如果 min > max 将会自动交换。
     */
    public int sampleIntInRange(int min, int max) {
        if (min > max) {
            int t = min;
            min = max;
            max = t;
        }
        if (this.counter >= MALLOC_BATCH_SIZE) {
            this.refresh();
            this.counter = 0;
        }
        // 读取一个字节并转为无符号 0..255
        int unsigned = this.buffer[this.counter++] & 0xFF;
        int range = max - min + 1;
        // 使用取模将 0..255 映射到 0..range-1，再平移到 min..max
        return min + (unsigned % range);
    }
}

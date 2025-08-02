use std::mem::MaybeUninit;

pub const MALLOC_BATCH_SIZE: usize = 16 * 16 * 128;

pub struct Randomer {
    pub counter: usize,
    pub buffer: [MaybeUninit<f64>; MALLOC_BATCH_SIZE],
}

impl Randomer {
    pub fn new() -> Self {
        Self {
            counter: 0,
            buffer: unsafe { MaybeUninit::uninit().assume_init() },
        }
    }

    pub fn refresh_buffer(&mut self) {
        self.buffer = unsafe {
            MaybeUninit::uninit().assume_init() // alloc
        }
    }

    pub fn sample(&mut self, x: i32, y: i32, z: i32) -> f64 {
        if self.counter >= MALLOC_BATCH_SIZE {
            self.refresh_buffer();
            self.counter = 0;
        }
        let result = unsafe { self.buffer[self.counter].assume_init() };
        self.counter += 1;
        result
    }
}

fn main() {
    let rand = Randomer::new();
}

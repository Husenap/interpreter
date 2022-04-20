#include <clox/chunk.h>
#include <clox/debug.h>
#include <math.h>

int main(int argc, const char* argv[]) {
  Chunk chunk;
  initChunk(&chunk);

  for (int i = 0; i < 300; ++i) {
    writeConstant(&chunk, cos(i), i + 1);
  }

  disassembleChunk(&chunk, "test chunk");
  freeChunk(&chunk);
  return 0;
}
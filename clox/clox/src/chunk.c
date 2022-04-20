#include "clox/chunk.h"

#include <stdlib.h>

#include "clox/memory.h"

void initChunk(Chunk* chunk) {
  chunk->count    = 0;
  chunk->capacity = 0;
  chunk->code     = NULL;
  initLines(&chunk->lines);
  initValueArray(&chunk->constants);
}

void freeChunk(Chunk* chunk) {
  FREE_ARRAY(uint8_t, chunk->code, chunk->capacity);
  freeLines(&chunk->lines);
  freeValueArray(&chunk->constants);
  initChunk(chunk);
}

void writeChunk(Chunk* chunk, uint8_t byte, int line) {
  if (chunk->capacity < chunk->count + 1) {
    int oldCapacity = chunk->capacity;
    chunk->capacity = GROW_CAPACITY(oldCapacity);
    chunk->code     = GROW_ARRAY(uint8_t, chunk->code, oldCapacity, chunk->capacity);
  }

  chunk->code[chunk->count] = byte;
  ++chunk->count;
  writeLines(&chunk->lines, line);
}

int addConstant(Chunk* chunk, Value value) {
  writeValueArray(&chunk->constants, value);
  return chunk->constants.count - 1;
}

int getLine(Chunk* chunk, int instruction) {
  int startOffset = 0;
  for (int i = 0; i < chunk->lines.count; ++i) {
    if (instruction >= startOffset &&
        instruction <= startOffset + chunk->lines.lines[i].length - 1) {
      return chunk->lines.lines[i].line;
    }
    startOffset += chunk->lines.lines[i].length;
  }
  return -1;
}
#ifndef _CHUNK_H
#define _CHUNK_H

#include "common.h"
#include "lines.h"
#include "value.h"

typedef enum {
  OP_CONSTANT,
  OP_CONSTANT_LONG,
  OP_ADD,
  OP_SUBTRACT,
  OP_MULTIPLY,
  OP_DIVIDE,
  OP_NEGATE,
  OP_RETURN,
} OpCode;

typedef struct {
  int        count;
  int        capacity;
  uint8_t*   code;
  Lines      lines;
  ValueArray constants;
} Chunk;

void initChunk(Chunk* chunk);
void freeChunk(Chunk* chunk);
void writeChunk(Chunk* chunk, uint8_t byte, int line);
void writeConstant(Chunk* chunk, Value value, int line);
int  addConstant(Chunk* chunk, Value value);
int  getLine(Chunk* chunk, int instruction);

#endif  // _CHUNK_H
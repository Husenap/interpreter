#include "clox/debug.h"

#include <stdio.h>

#include "clox/value.h"

void disassembleChunk(Chunk* chunk, const char* name) {
  printf("== %s ==\n", name);

  int previousLine = 0;
  for (int offset = 0; offset < chunk->count;) {
    offset = disassembleInstruction(chunk, offset, &previousLine);
  }
}

static int simpleInstruction(const char* name, int offset) {
  printf("%s\n", name);
  return offset + 1;
}

static int constantInstruction(const char* name, Chunk* chunk, int offset) {
  int constant = chunk->code[offset + 1];
  printf("%-16s %4d '", name, constant);
  printValue(chunk->constants.values[constant]);
  printf("'\n");
  return offset + 2;
}
static int constantLongInstruction(const char* name, Chunk* chunk, int offset) {
  int constant =
      chunk->code[offset + 1] | chunk->code[offset + 2] << 8 | chunk->code[offset + 3] << 16;
  printf("%-16s %4d '", name, constant);
  printValue(chunk->constants.values[constant]);
  printf("'\n");
  return offset + 4;
}

int disassembleInstruction(Chunk* chunk, int offset, int* previousLine) {
  printf("%04d ", offset);

  int currentLine = getLine(chunk, offset);
  if (currentLine != *previousLine) {
    printf("%4d ", currentLine);
    *previousLine = currentLine;
  } else {
    printf("   | ");
  }

  uint8_t instruction = chunk->code[offset];
  switch (instruction) {
  case OP_CONSTANT:
    return constantInstruction("OP_CONSTANT", chunk, offset);
  case OP_CONSTANT_LONG:
    return constantLongInstruction("OP_CONSTANT_LONG", chunk, offset);
  case OP_RETURN:
    return simpleInstruction("OP_RETURN", offset);
  default:
    printf("Unknown opcode %d\n", instruction);
    return offset + 1;
  }
}
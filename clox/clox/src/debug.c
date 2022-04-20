#include "clox/debug.h"

#include <stdio.h>

#include "clox/value.h"

void disassembleChunk(Chunk* chunk, const char* name) {
  printf("== %s ==\n", name);

  for (int offset = 0; offset < chunk->count;) {
    offset = disassembleInstruction(chunk, offset);
  }
}

static int simpleInstruction(const char* name, int offset) {
  printf("%s\n", name);
  return offset + 1;
}

static int constantInstruction(const char* name, Chunk* chunk, int offset) {
  uint8_t constant = chunk->code[offset + 1];
  printf("%-16s %4d '", name, constant);
  printValue(chunk->constants.values[constant]);
  printf("'\n");
  return offset + 2;
}

static void printLine(Chunk* chunk, int offset) {
  int startOffset = 0;
  for (int i = 0; i < chunk->lines.count; ++i) {
    if (offset == startOffset) {
      printf("%4d ", chunk->lines.lines[i].line);
      return;
    } else if (offset > startOffset && offset <= startOffset + chunk->lines.lines[i].length - 1) {
      printf("   | ");
      return;
    }
    startOffset += chunk->lines.lines[i].length;
  }
}

int disassembleInstruction(Chunk* chunk, int offset) {
  printf("%04d ", offset);

  printLine(chunk, offset);

  uint8_t instruction = chunk->code[offset];
  switch (instruction) {
  case OP_CONSTANT:
    return constantInstruction("OP_CONSTANT", chunk, offset);
  case OP_RETURN:
    return simpleInstruction("OP_RETURN", offset);
  default:
    printf("Unknown opcode %d\n", instruction);
    return offset + 1;
  }
}
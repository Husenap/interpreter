#include "clox/lines.h"

#include "clox/memory.h"

void initLines(Lines* lines) {
  lines->count        = 0;
  lines->capacity     = 0;
  lines->previousLine = 0;
  lines->lines        = NULL;
}
void freeLines(Lines* lines) {
  FREE_ARRAY(RLE, lines->lines, lines->capacity);
  initLines(lines);
}
void writeLines(Lines* lines, int line) {
  if (lines->previousLine != line || lines->lines == NULL) {
    if (lines->capacity < lines->count + 1) {
      int oldCapacity = lines->capacity;
      lines->capacity = GROW_CAPACITY(oldCapacity);
      lines->lines    = GROW_ARRAY(RLE, lines->lines, oldCapacity, lines->capacity);
    }

    lines->lines[lines->count].line   = line;
    lines->lines[lines->count].length = 0;
    ++lines->count;
  }

  ++lines->lines[lines->count - 1].length;
  lines->previousLine = line;
}
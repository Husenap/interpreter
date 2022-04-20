#ifndef _LINES_H
#define _LINES_H

#include "common.h"

typedef struct {
  int line;
  int length;
} RLE;

typedef struct {
  int  capacity;
  int  count;
  int  previousLine;
  RLE* lines;
} Lines;

void initLines(Lines* lines);
void freeLines(Lines* lines);
void writeLines(Lines* lines, int line);

#endif  // _LINES_H
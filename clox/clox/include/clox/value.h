#ifndef _VALUE_H
#define _VALUE_H

#include "common.h"

typedef double Value;

typedef struct {
  int    count;
  int    capacity;
  Value* values;
} ValueArray;

void initValueArray(ValueArray* array);
void freeValueArray(ValueArray* array);
void writeValueArray(ValueArray* array, Value value);
void printValue(Value value);

#endif  // _VALUE_H
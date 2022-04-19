#include "clox/memory.h"

#include <assert.h>
#include <stdlib.h>

void* reallocate(void* pointer, size_t oldSize, size_t newSize) {
  if (newSize == 0) {
    free(pointer);
    return NULL;
  }

  void* result = realloc(pointer, newSize);
  assert(result != NULL);
  return result;
}
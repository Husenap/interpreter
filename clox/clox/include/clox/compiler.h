#ifndef _COMPILER_H
#define _COMPILER_H

#include "vm.h"

bool compile(const char* source, Chunk* chunk);

#endif  // _COMPILER_H
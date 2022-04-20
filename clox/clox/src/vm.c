#include "clox/vm.h"

#include <assert.h>
#include <stdio.h>

#include "clox/common.h"
#include "clox/debug.h"

VM vm;

static void resetStack() {
  vm.stackTop = vm.stack;
}

void initVM() {
  resetStack();
}

void freeVM() {}

static uint8_t READ_BYTE() {
  return (*vm.ip++);
}

static Value READ_CONSTANT() {
  return vm.chunk->constants.values[READ_BYTE()];
}

static Value READ_CONSTANT_LONG() {
  int index = READ_BYTE();
  index |= READ_BYTE() << 8;
  index |= READ_BYTE() << 16;
  return vm.chunk->constants.values[index];
}

static InterpretResult run() {
#define BINARY_OP(op) \
  do {                \
    double b = pop(); \
    double a = pop(); \
    push(a op b);     \
  } while (false)

  for (;;) {
#ifdef DEBUG_TRACE_EXECUTION
    printf("          ");
    for (Value *slot = vm.stack; slot < vm.stackTop; ++slot) {
      printf("[");
      printValue(*slot);
      printf("] ");
    }
    printf("\n");
    disassembleInstruction(vm.chunk, (int)(vm.ip - vm.chunk->code));
#endif
    uint8_t instruction;
    switch (instruction = READ_BYTE()) {
    case OP_CONSTANT:
      push(READ_CONSTANT());
      break;
    case OP_CONSTANT_LONG:
      push(READ_CONSTANT_LONG());
      break;
    case OP_ADD:
      BINARY_OP(+);
      break;
    case OP_SUBTRACT:
      BINARY_OP(-);
      break;
    case OP_MULTIPLY:
      BINARY_OP(*);
      break;
    case OP_DIVIDE:
      BINARY_OP(/);
      break;
    case OP_NEGATE:
      push(-pop());
      break;
    case OP_RETURN:
      printValue(pop());
      printf("\n");
      return INTERPRET_OK;
    default:
      break;
    }
  }

#undef BINARY_OP
}

InterpretResult interpret(Chunk *chunk) {
  vm.chunk = chunk;
  vm.ip    = vm.chunk->code;
  return run();
}

void push(Value value) {
  assert(vm.stackTop - vm.stack < STACK_MAX && "STACK OVERFLOW");
  *vm.stackTop = value;
  ++vm.stackTop;
}
Value pop() {
  assert(vm.stackTop - vm.stack > 0);
  return *--vm.stackTop;
}
#include <gtest/gtest.h>

extern "C" {
#include <clox/chunk.h>
}

namespace {
class ChunkTest : public ::testing::Test {
protected:
  Chunk chunk;
  void  SetUp() override { initChunk(&chunk); }
  void  TearDown() override { freeChunk(&chunk); }
};
}  // namespace

TEST_F(ChunkTest, init) {
  EXPECT_EQ(chunk.capacity, 0);
  EXPECT_EQ(chunk.count, 0);
  EXPECT_EQ(chunk.code, nullptr);
}

TEST_F(ChunkTest, write) {
  writeChunk(&chunk, OP_RETURN, 123);
  EXPECT_EQ(chunk.count, 1);
  EXPECT_EQ(chunk.code[0], OP_RETURN);
  writeChunk(&chunk, OP_CONSTANT, 123);
  EXPECT_EQ(chunk.count, 2);
  EXPECT_EQ(chunk.code[1], OP_CONSTANT);
}

TEST_F(ChunkTest, getLine) {
  writeChunk(&chunk, OP_CONSTANT, 1);
  writeChunk(&chunk, OP_RETURN, 1);
  writeChunk(&chunk, OP_RETURN, 2);
  EXPECT_EQ(getLine(&chunk, 0), 1);
  EXPECT_EQ(getLine(&chunk, 1), 1);
  EXPECT_EQ(getLine(&chunk, 2), 2);
}

TEST_F(ChunkTest, free) {
  writeChunk(&chunk, OP_RETURN, 123);
  EXPECT_EQ(chunk.count, 1);
  EXPECT_EQ(chunk.code[0], OP_RETURN);
  freeChunk(&chunk);
  EXPECT_EQ(chunk.capacity, 0);
  EXPECT_EQ(chunk.count, 0);
  EXPECT_EQ(chunk.code, nullptr);
}
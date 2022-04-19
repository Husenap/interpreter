#include <gtest/gtest.h>

extern "C" {
#include <clox/value.h>
}

namespace {
class ValueArrayTest : public ::testing::Test {
protected:
  ValueArray array;
  void       SetUp() override { initValueArray(&array); }
  void       TearDown() override { freeValueArray(&array); }
};
}  // namespace

TEST_F(ValueArrayTest, init) {
  EXPECT_EQ(array.capacity, 0);
  EXPECT_EQ(array.count, 0);
  EXPECT_EQ(array.values, nullptr);
}

TEST_F(ValueArrayTest, write) {
  writeValueArray(&array, 1.2);
  writeValueArray(&array, 4.2);
  EXPECT_EQ(array.count, 2);
  EXPECT_EQ(array.values[0], 1.2);
  EXPECT_EQ(array.values[1], 4.2);
}

TEST_F(ValueArrayTest, free) {
  writeValueArray(&array, 1.2);
  EXPECT_EQ(array.count, 1);
  EXPECT_EQ(array.values[0], 1.2);
  freeValueArray(&array);
  EXPECT_EQ(array.capacity, 0);
  EXPECT_EQ(array.count, 0);
  EXPECT_EQ(array.values, nullptr);
}
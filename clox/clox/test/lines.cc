#include <gtest/gtest.h>

extern "C" {
#include <clox/lines.h>
}

namespace {
class LinesTest : public ::testing::Test {
protected:
  Lines lines;
  void  SetUp() override { initLines(&lines); }
  void  TearDown() override { freeLines(&lines); }
};
}  // namespace

TEST_F(LinesTest, init) {
  EXPECT_EQ(lines.count, 0);
  EXPECT_EQ(lines.capacity, 0);
  EXPECT_EQ(lines.previousLine, 0);
  EXPECT_EQ(lines.lines, nullptr);
}
TEST_F(LinesTest, write) {
  writeLines(&lines, 1);
  writeLines(&lines, 1);
  EXPECT_EQ(lines.count, 1);
  EXPECT_EQ(lines.lines[0].line, 1);
  EXPECT_EQ(lines.lines[0].length, 2);
  writeLines(&lines, 2);
  EXPECT_EQ(lines.lines[1].line, 2);
  EXPECT_EQ(lines.lines[1].length, 1);
}
TEST_F(LinesTest, free) {
  writeLines(&lines, 42);
  EXPECT_EQ(lines.count, 1);
  EXPECT_EQ(lines.previousLine, 42);
  freeLines(&lines);
  EXPECT_EQ(lines.count, 0);
  EXPECT_EQ(lines.capacity, 0);
  EXPECT_EQ(lines.previousLine, 0);
  EXPECT_EQ(lines.lines, nullptr);
}
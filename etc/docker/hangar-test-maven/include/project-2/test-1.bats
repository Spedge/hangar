#!/usr/bin/env bats

@test "Compiled target jar exists" {
  $(test -e ./project-2/target/hangar-test-project2-0.0.1.jar)
  [ $? -eq 0 ]
}
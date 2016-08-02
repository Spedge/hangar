#!/usr/bin/env bats

@test "Compiled target jar exists" {
  $(test -e ./project-1/target/hangar-test-project1-0.0.1-SNAPSHOT.jar)
  [ $? -eq 0 ]
}
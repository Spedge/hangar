#!/usr/bin/env bats

@test "Compiled target jar exists" {
  result="$(ls -l ./project-1/target/hangar-test-0.0.1-SNAPSHOT.jar | wc -l)"
  [ "$result" -eq 1 ]
}
#!/bin/sh

original_working_dir=`pwd`
destination_dir='build/reports/comparison'

mkdir -p $destination_dir
cd $destination_dir

remote_url=`git ls-remote --get-url`
git clone $remote_url .

./gradlew check

destination_full_path=`pwd`
cd $original_working_dir

echo "${destination_full_path}"

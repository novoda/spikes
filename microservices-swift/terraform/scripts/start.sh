#! /usr/bin/env bash

pkill swift
cd .build/release
./microservices-swift
cd -
